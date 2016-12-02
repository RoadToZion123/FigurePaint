package ru.startandroid.figurepaint;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PaintFragment extends Fragment{

    private PaintActivity paintActivity;

    private FigureView figureView;
    private String photoPath;

    public interface Callback{
        public void showFab();
    }

    public static PaintFragment newInstance(String photoPath){
        Bundle arguments = new Bundle();
        arguments.putString(MainActivity.EXTRA_KEY_PHOTO_PATH, photoPath);
        PaintFragment paintFragment = new PaintFragment();
        paintFragment.setArguments(arguments);
        return paintFragment;
    }

    public PaintActivity getPaintActivity(){
        return paintActivity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        paintActivity = (PaintActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        photoPath = getArguments().getString(MainActivity.EXTRA_KEY_PHOTO_PATH);
        Log.d("ZHEKA", "onCreateView photoPath = " + photoPath);

        View view = inflater.inflate(R.layout.paint_fragment, container, false);

        figureView = (FigureView)view.findViewById(R.id.figureView);
        figureView.setPhotoPath(photoPath);
        figureView.setFragmentInstance(this);

        return view;
    }

    public void setLastSelectedInstrumentId(int instrumentId){
        figureView.setLastSelectedInstrumentId(instrumentId);
    }

    public void setPaintColor(int color){
        figureView.setPaintColor(color);
    }

    public void setCanDraw(boolean can){
        figureView.setCanDraw(can);
    }
}
