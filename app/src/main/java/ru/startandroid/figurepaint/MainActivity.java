package ru.startandroid.figurepaint;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

    public static final String EXTRA_KEY_PHOTO_PATH = "extra_key_photo_path";

    private int currentClickedImageButton;

    private File directory;
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
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ZHEKA", "onDestroy dirPath = " + directory.getAbsolutePath());
        deleteDirectory(directory);
    }

    public static boolean deleteDirectory(File path) {
        Log.d("ZHEKA", "deleteDirectory path = " + path.getAbsolutePath());
        if( path.exists() ) {
            File[] files = path.listFiles();
            Log.d("ZHEKA", "deleteDirectory filesLength = " + files.length);
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    Log.d("ZHEKA", "deleteDirectory file = " + files[i].getAbsolutePath());
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
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

                break;
        }
    }

    private void createDirectory(){
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FigurePaintPhoto");
        if(!directory.exists()){
            directory.mkdirs();
        }
    }

    private Uri generateUri(){
        photoFile = new File(directory.getPath() + "/photo_" + System.currentTimeMillis() + ".jpg");
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
                imageButtonCameraPhoto.setEnabled(true);
                imageButtonGalleryPhoto.setEnabled(true);
                break;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        switch(currentClickedImageButton){
            case R.id.imageButtonCameraPhoto:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, generateUri());
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;
            case R.id.imageButtonGalleryPhoto:
                //startActivity(new Intent(this, PaintActivity.class));
                break;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
