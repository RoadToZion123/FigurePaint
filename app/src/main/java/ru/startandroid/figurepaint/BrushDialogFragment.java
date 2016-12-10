package ru.startandroid.figurepaint;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;


public class BrushDialogFragment extends DialogFragment {

    private ImageView imageView;
    private SeekBar seekBar;
    private FigureView figureView;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        figureView = getPaintFragment().getFigureView();

        bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(figureView.getPaintColor());
        Log.d("DIALOG", "setColor " + Integer.toHexString(figureView.getPaintColor()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_view, null);
        imageView = (ImageView)view.findViewById(R.id.dialog_image_view);
        seekBar = (SeekBar)view.findViewById(R.id.dialog_seek_bar);
        seekBar.setOnSeekBarChangeListener(changeListener);
        seekBar.setProgress(figureView.getStrokeWidth());
        Log.d("DIALOG", "setProgress " + figureView.getStrokeWidth());

        builder.setView(view);
        builder.setTitle("Choose stroke width");
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                figureView.setStrokeWidth(seekBar.getProgress());
            }
        });

        return builder.create();
    }

    private PaintFragment getPaintFragment(){
        return (PaintFragment)getFragmentManager().findFragmentById(R.id.fragmentContainer);
    }

    SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d("DIALOG", "onProgressChanged");
            paint.setStrokeWidth(progress);
            bitmap.eraseColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
            canvas.drawLine(30, 50, 370, 50, paint);
            imageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
