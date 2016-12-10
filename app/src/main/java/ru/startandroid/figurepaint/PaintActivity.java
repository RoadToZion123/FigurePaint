package ru.startandroid.figurepaint;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;

public class PaintActivity extends AppCompatActivity implements View.OnClickListener{

    private int color;
    private int lastSelectedInstrument;


    private Drawable drawableCircle;
    private Drawable drawableRectangle;
    private Drawable drawableLine;
    private Drawable drawablePallete;


    private AlertDialog colorPickerAlertDialog;

    private FragmentManager fragmentManager;

    private BottomSheetBehavior bottomSheetBehavior;

    private TextView textViewInstruments;
    private ImageButton imageButtonCircle;
    private ImageButton imageButtonRectangle;
    private ImageButton imageButtonLine;
    private ImageButton imageButtonBrush;
    private ImageButton imageButtonPallete;

    public FloatingActionButton fabInstruments;
    public FloatingActionButton fabFinish;
    public FloatingActionButton fabSave;
    public FloatingActionButton fabDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ZHEKA", "onCreate");
        setContentView(R.layout.paint_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        color = ContextCompat.getColor(this, R.color.colorImageViewBottomSheet);


        drawableCircle = ContextCompat.getDrawable(this, R.drawable.ic_circle);
        drawableRectangle = ContextCompat.getDrawable(this, R.drawable.ic_rectangle);
        drawableLine = ContextCompat.getDrawable(this, R.drawable.ic_line);
        drawablePallete = ContextCompat.getDrawable(this, R.drawable.ic_pallete);

        drawableCircle = DrawableCompat.wrap(drawableCircle);
        drawableRectangle = DrawableCompat.wrap(drawableRectangle);
        drawableLine = DrawableCompat.wrap(drawableLine);
        drawablePallete = DrawableCompat.wrap(drawablePallete);



        colorPickerAlertDialog = ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(color)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(10)
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        color = selectedColor;
                        setColorInTextViewAndPallete();
                        setPaintColor();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build();



        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if(fragment == null){
            fragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, PaintFragment.newInstance(getIntent()
                            .getStringExtra(MainActivity.EXTRA_KEY_PHOTO_PATH)))
                    .commit();
        }


        View bottomSheet = findViewById(R.id.bottom_sheet);
        textViewInstruments = (TextView)bottomSheet.findViewById(R.id.textViewInstruments);
        imageButtonCircle = (ImageButton)bottomSheet.findViewById(R.id.imageButtonCircle);
        imageButtonRectangle = (ImageButton)bottomSheet.findViewById(R.id.imageButtonRectangle);
        imageButtonLine = (ImageButton)bottomSheet.findViewById(R.id.imageButtonLine);
        imageButtonBrush = (ImageButton)bottomSheet.findViewById(R.id.imageButtonBrush);
        imageButtonPallete = (ImageButton)bottomSheet.findViewById(R.id.imageButtonPallete);

        setColorInTextViewAndPallete();

        imageButtonCircle.setOnClickListener(this);
        imageButtonRectangle.setOnClickListener(this);
        imageButtonLine.setOnClickListener(this);
        imageButtonBrush.setOnClickListener(this);
        imageButtonPallete.setOnClickListener(this);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_DRAGGING){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        fabInstruments = (FloatingActionButton)findViewById(R.id.fabInstruments);
        fabInstruments.setOnClickListener(this);

        fabFinish = (FloatingActionButton)findViewById(R.id.fabFinish);
        fabFinish.setOnClickListener(this);
        fabFinish.hide();

        fabSave = (FloatingActionButton)findViewById(R.id.fabSave);
        fabSave.setOnClickListener(this);
        fabSave.hide();

        fabDelete = (FloatingActionButton)findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(this);
        fabDelete.hide();
    }

    private void setPaintColor(){
        PaintFragment paintFragment = (PaintFragment)fragmentManager.findFragmentById(R.id.fragmentContainer);
        paintFragment.setPaintColor(color);
    }

    private void setColorInTextViewAndPallete(){
        textViewInstruments.setTextColor(color);
        DrawableCompat.setTint(drawablePallete, color);
        imageButtonPallete.setImageDrawable(drawablePallete);
    }

    private void setDrawablesInImageButtons(){
        imageButtonCircle.setImageDrawable(drawableCircle);
        imageButtonRectangle.setImageDrawable(drawableRectangle);
        imageButtonLine.setImageDrawable(drawableLine);
    }

    private void setLastSelectedInstrumentId(){
        PaintFragment paintFragment = (PaintFragment)fragmentManager.findFragmentById(R.id.fragmentContainer);
        paintFragment.setLastSelectedInstrumentId(lastSelectedInstrument);
    }

    /*private void setCanDraw(boolean can){
        PaintFragment paintFragment = (PaintFragment)fragmentManager.findFragmentById(R.id.fragmentContainer);
        paintFragment.setCanDraw(can);
    }*/

    private void saveBitmap(){
        PaintFragment paintFragment = (PaintFragment)fragmentManager.findFragmentById(R.id.fragmentContainer);
        paintFragment.saveBitmap();
    }

    private void deleteFigure(){
        PaintFragment paintFragment = (PaintFragment)fragmentManager.findFragmentById(R.id.fragmentContainer);
        paintFragment.deleteFigure();
    }

    private void drawLastFigureOnBitmap(){
        PaintFragment paintFragment = (PaintFragment)fragmentManager.findFragmentById(R.id.fragmentContainer);
        paintFragment.drawLastFigureOnBitmap();
    }

    private File getFile(){
        PaintFragment paintFragment = (PaintFragment)fragmentManager.findFragmentById(R.id.fragmentContainer);
        return paintFragment.getFile();
    }

    public boolean deleteDirectory(File path) {
        Log.d("ZHEKA", "deleteDirectory path = " + path.getAbsolutePath());
        if( path.exists() ) {
            Log.d("ZHEKA", "deleteDirectory path.exists() = " + path.exists());
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
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.fabInstruments:
                Log.d("SHEET", "state = " + bottomSheetBehavior.getState());
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                    Log.d("SHEET", "if");
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    fabInstruments.setImageResource(R.drawable.ic_collapse);
                    //setCanDraw(false);
                }else{
                    Log.d("SHEET", "else");
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    fabInstruments.setImageResource(R.drawable.ic_instruments);
                    //setCanDraw(true);
                }
                break;
            case R.id.fabFinish:
                drawLastFigureOnBitmap();
                fabFinish.hide();
                fabDelete.hide();
                break;
            case R.id.fabSave:
                saveBitmap();
                /*sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://" +
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                        "/" + "FigurePaintPhoto")));*/
                File imageFile = new File(getFile().getAbsolutePath());
                Log.d("FLASH", imageFile.getPath());
                MediaScannerConnection.scanFile(this, new String[] { imageFile.getPath() }, new String[] { "image/jpeg" }, null);
                fabSave.hide();
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FigurePaintPhoto");
                deleteDirectory(file);
                DrawableCompat.setTint(drawableCircle, Color.WHITE);
                DrawableCompat.setTint(drawableRectangle, Color.WHITE);
                DrawableCompat.setTint(drawableLine, Color.WHITE);
                finish();
                break;
            case R.id.fabDelete:
                deleteFigure();
                fabFinish.hide();
                fabDelete.hide();
                break;
            case R.id.imageButtonCircle:
                lastSelectedInstrument = id;
                setLastSelectedInstrumentId();

                DrawableCompat.setTint(drawableCircle, ContextCompat.getColor(this, R.color.colorImageViewBottomSheet));
                DrawableCompat.setTint(drawableRectangle, Color.WHITE);
                DrawableCompat.setTint(drawableLine, Color.WHITE);

                setDrawablesInImageButtons();
                break;
            case R.id.imageButtonRectangle:
                lastSelectedInstrument = id;
                setLastSelectedInstrumentId();

                DrawableCompat.setTint(drawableCircle, Color.WHITE);
                DrawableCompat.setTint(drawableRectangle, ContextCompat.getColor(this, R.color.colorImageViewBottomSheet));
                DrawableCompat.setTint(drawableLine, Color.WHITE);

                setDrawablesInImageButtons();
                break;
            case R.id.imageButtonLine:
                if(fabFinish.isShown()){
                    Toast.makeText(this, "You are not save path!", Toast.LENGTH_SHORT).show();
                    return;
                }
                lastSelectedInstrument = id;
                setLastSelectedInstrumentId();

                DrawableCompat.setTint(drawableCircle, Color.WHITE);
                DrawableCompat.setTint(drawableRectangle, Color.WHITE);
                DrawableCompat.setTint(drawableLine, ContextCompat.getColor(this, R.color.colorImageViewBottomSheet));

                setDrawablesInImageButtons();
                break;
            case R.id.imageButtonBrush:
                BrushDialogFragment brushDialogFragment = new BrushDialogFragment();
                brushDialogFragment.show(fragmentManager, "brush");
                break;
            case R.id.imageButtonPallete:
                colorPickerAlertDialog.show();
                break;
        }
    }
}
