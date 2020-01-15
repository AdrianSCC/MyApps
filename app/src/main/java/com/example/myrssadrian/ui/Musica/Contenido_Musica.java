package com.example.myrssadrian.ui.Musica;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myrssadrian.R;

import java.io.File;
import java.util.ArrayList;

public class Contenido_Musica extends Fragment {

    //Declaracion de las variables a utilizar
    File cancion;
    Thread updateSeekBar;
    static MediaPlayer mp;
    ArrayList<File> mySongs;
    int position;
    String sname;
    long art;

    //Creamos los contructores
    public Contenido_Musica(File file) {
        this.cancion = file;
    }

    public Contenido_Musica(File file, ArrayList<File> mySongs, int position) {
        this.cancion = file;
        this.mySongs = mySongs;
        this.position = position;
    }

    public Contenido_Musica(File file, ArrayList<File> mySongs, int position, Long aLong) {
        this.cancion = file;
        this.mySongs = mySongs;
        this.position = position;
        this.art = aLong;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.musica_contenido, container, false);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TextView songNameText = getView().findViewById(R.id.txtSongLabel);
        final Button pause =getView().findViewById(R.id.pause);
        final Button previous = getView().findViewById(R.id.previous);
        final Button next = getView().findViewById(R.id.next);

        //variables a utilizar para ampliar el trabajo
        //final SeekBar sb = (SeekBar) getView().findViewById(R.id.seekBar);
        //final ImageView imageView = getView().findViewById(R.id.album_art);

        if (mp != null) {
            mp.stop();
            mp.release();
        }

        //introducimos el nombre de la cancion
        sname = cancion.getName();
        songNameText.setText(sname);
        songNameText.setSelected(true);

        //Iniciamos la cancion
        Uri u = Uri.parse(cancion.toString());
        mp = MediaPlayer.create(getContext().getApplicationContext(), u);
        mp.start();
        //sb.setMax(mp.getDuration());



        /*
        * Controlamos los metodos onClick de los distintos botones
        * */

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sb.setMax(mp.getDuration());
                if (mp.isPlaying()) {
                    pause.setBackgroundResource(R.drawable.ic_play);
                    mp.pause();

                } else {
                    pause.setBackgroundResource(R.drawable.pause);
                    mp.start();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                mp.release();
                position = ((position + 1) % mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());
                // songNameText.setText(getSongName);
                mp = MediaPlayer.create(getContext().getApplicationContext(), u);

                sname = mySongs.get(position).getName().toString();
                songNameText.setText(sname);

                try {
                    mp.start();
                } catch (Exception e) {
                }

            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //songNameText.setText(getSongName);
                mp.stop();
                mp.release();

                position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getContext().getApplicationContext(), u);
                sname = mySongs.get(position).getName().toString();
                songNameText.setText(sname);
                mp.start();
            }
        });



    }

}

//Creacion del seekbar que al dar errores habra que controlarlo mas adelante
        /*
        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);



                    }catch (Exception ex){
                        mp.stop();
                        mp.release();
                        position = ((position + 1) % mySongs.size());
                        Uri u = Uri.parse(mySongs.get(position).toString());
                        // songNameText.setText(getSongName);
                        mp = MediaPlayer.create(getContext().getApplicationContext(), u);

                        sname = mySongs.get(position).getName().toString();
                        songNameText.setText(sname);
                        mp.start();


                    }
                }

                try {

                    if(mp.getDuration() == sb.getProgress()){

                        mp.stop();
                        mp.release();
                        position = ((position + 1) % mySongs.size());
                        Uri u = Uri.parse(mySongs.get(position).toString());
                        // songNameText.setText(getSongName);
                        mp = MediaPlayer.create(getContext().getApplicationContext(), u);

                        sname = mySongs.get(position).getName().toString();
                        songNameText.setText(sname);

                        mp.start();


                    }

                }catch (Exception ex){
                    mp.stop();
                    mp.release();
                    position = ((position + 1) % mySongs.size());
                    Uri u = Uri.parse(mySongs.get(position).toString());
                    // songNameText.setText(getSongName);
                    mp = MediaPlayer.create(getContext().getApplicationContext(), u);

                    sname = mySongs.get(position).getName().toString();
                    songNameText.setText(sname);
                    mp.start();
                }


            }
        };




        updateSeekBar.start();
        sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        sb.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);



        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i,
                                          boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());

            }
        });

         */
