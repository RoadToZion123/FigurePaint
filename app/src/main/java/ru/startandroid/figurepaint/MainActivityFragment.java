package ru.startandroid.figurepaint;

import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;


public class MainActivityFragment extends Fragment {

    private LinearLayout linearLayout;

    private ImageButton imageButtonCameraPhoto;
    private ImageButton imageButtonGalleryPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        linearLayout = (LinearLayout)view.findViewById(R.id.linearLayout);

        imageButtonCameraPhoto = (ImageButton)view.findViewById(R.id.imageButtonCameraPhoto);
        imageButtonGalleryPhoto = (ImageButton)view.findViewById(R.id.imageButtonGalleryPhoto);
        imageButtonGalleryPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playImageButtonsAnimation();
            }
        });

        playImageButtonsAnimation();
        return view;
    }

    private void playImageButtonsAnimation(){
        Animation cameraAnimation  = AnimationUtils.loadAnimation(getActivity(), R.anim.camera_animation);
        imageButtonCameraPhoto.startAnimation(cameraAnimation);
        Animation galleryAnimation  = AnimationUtils.loadAnimation(getActivity(), R.anim.gallery_animation);
        imageButtonGalleryPhoto.startAnimation(galleryAnimation);
    }
}
