package ru.startandroid.figurepaint;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Animation.AnimationListener {

    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_GALLERY = 1;
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 2;

    public static final String EXTRA_KEY_PHOTO_PATH = "extra_key_photo_path";

    private int currentClickedImageButton;

    private File directoryPhoto;
    private File directoryChangedPhoto;
    private File photoFile;

    private Animation cameraAnimation;
    private Animation galleryAnimation;
    private Animation onClickAnimation;

    private ImageButton imageButtonCameraPhoto;
    private ImageButton imageButtonGalleryPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        createDirectory();

        cameraAnimation  = AnimationUtils.loadAnimation(this, R.anim.camera_animation);
        galleryAnimation  = AnimationUtils.loadAnimation(this, R.anim.gallery_animation);

        onClickAnimation  = AnimationUtils.loadAnimation(this, R.anim.onclick_animation);
        onClickAnimation.setAnimationListener(this);

        Toolbar toolBar  = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);

        imageButtonCameraPhoto = (ImageButton)findViewById(R.id.imageButtonCameraPhoto);
        imageButtonGalleryPhoto = (ImageButton)findViewById(R.id.imageButtonGalleryPhoto);
        imageButtonCameraPhoto.setOnClickListener(this);
        imageButtonGalleryPhoto.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        imageButtonCameraPhoto.startAnimation(cameraAnimation);
        imageButtonGalleryPhoto.startAnimation(galleryAnimation);
        imageButtonCameraPhoto.setEnabled(true);
        imageButtonGalleryPhoto.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ZHEKA", "onActivityResult");
        switch(requestCode){
            case REQUEST_CODE_CAMERA:
                if(resultCode == RESULT_OK){
                    startActivity(new Intent(this, PaintActivity.class)
                        .putExtra(EXTRA_KEY_PHOTO_PATH, photoFile.getAbsolutePath()));
                }
                break;
            case REQUEST_CODE_GALLERY:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    Log.d("IGOR", "uri = " + uri);
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String photoPath = cursor.getString(columnIndex);
                    cursor.close();
                    Log.d("IGOR", "photoPath = " + photoPath);
                    startActivity(new Intent(this, PaintActivity.class)
                            .putExtra(EXTRA_KEY_PHOTO_PATH, photoPath));
                }
                break;
        }
    }

    private void createDirectory(){
        directoryPhoto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FigurePaintPhoto");
        directoryChangedPhoto = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FigurePaintChangedPhoto");
        if(!directoryPhoto.exists()){
            directoryPhoto.mkdirs();
            Log.d("ZHEKA", "createDirectory directoryPhoto.exists() = " + directoryPhoto.exists());
        }
        if(!directoryChangedPhoto.exists()){
            directoryChangedPhoto.mkdirs();
            Log.d("ZHEKA", "createDirectory directoryChangedPhoto.exists() = " + directoryChangedPhoto.exists());
        }
    }

    private Uri generateUri(){
        photoFile = new File(directoryPhoto.getPath() + "/photo_" + System.currentTimeMillis() + ".jpg");
        Log.d("ZHEKA", "photoPath = " + photoFile.getAbsolutePath());
        return Uri.fromFile(photoFile);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imageButtonCameraPhoto:
                currentClickedImageButton = id;
                imageButtonCameraPhoto.startAnimation(onClickAnimation);
                imageButtonCameraPhoto.setEnabled(false);
                imageButtonGalleryPhoto.setEnabled(false);
                break;
            case R.id.imageButtonGalleryPhoto:
                currentClickedImageButton = id;
                imageButtonGalleryPhoto.startAnimation(onClickAnimation);
                imageButtonCameraPhoto.setEnabled(false);
                imageButtonGalleryPhoto.setEnabled(false);
                break;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Intent intent;
        switch(currentClickedImageButton){
            case R.id.imageButtonCameraPhoto:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    saveImage();
                }else{
                    createDirectory();
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, generateUri());
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }
                break;
            case R.id.imageButtonGalleryPhoto:
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
                break;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void saveImage() {
        // Проверить, есть ли у приложения разрешение,
        // необходимое для сохранения
        if (checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            // Объяснить, почему понадобилось разрешение
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this);

                // Назначить сообщение AlertDialog
                builder.setMessage(R.string.permission_explanation);

                // Добавить кнопку OK в диалоговое окно
                builder.setPositiveButton( android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Запросить разрешение
                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                        }
                    }
                );

                // Отображение диалогового окна
                builder.create().show();
            }else {
                // Запросить разрешение
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                }
            }
        else { // Если разрешение уже имеет разрешение для записи
            createDirectory();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, generateUri());
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }
    }

    // Вызывается системой, когда пользователь предоставляет
    // или отклоняет разрешение для сохранения изображения
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // switch выбирает действие в зависимости от того,
        // какое разрешение было запрошено
        switch (requestCode) {
            case SAVE_IMAGE_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    createDirectory();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, generateUri());
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }
                break;
        }
    }
}
