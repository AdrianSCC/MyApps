package com.example.myrssadrian.ui.Video;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myrssadrian.R;

public class VideoFragment extends Fragment {

    //Declaramos las variables
    Button examinar;
    VideoView reproductor;
    private final int VIDEO = 1;


    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.video_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //referenciamos los valores
        examinar = getView().findViewById(R.id.btnExaminar);
        reproductor = getView().findViewById(R.id.vvReproductor);

        examinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirVideo();
            }
        });
    }

    /**
     * Creamos el intent para elegir el video y reproducirlo
     */
    public void elegirVideo() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT,android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        galleryIntent.setType("video/*");
        startActivityForResult(galleryIntent, VIDEO);
    }


    // Siempre se ejecuta al realizar las accion
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("FOTO", "Opción::--->" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == VIDEO) {
            Log.d("VIDEO", "Entramos en Galería");
            if (data != null) {
                // Obtenemos su URI con su dirección temporal
                Uri contentURI = data.getData();

                reproductor.setMediaController(new MediaController(getContext()));
                reproductor.setVideoURI(contentURI);
                reproductor.requestFocus();
                reproductor.start();

            }

        }


    }
}
