package com.example.myrssadrian.ui.Musica;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myrssadrian.MainActivity;
import com.example.myrssadrian.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MusicaFragment extends Fragment {

    //Declaramos la variables
    ListView listView;
    String[] items;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.musica_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Creamos la referencia al listView
        listView = (ListView) getView().findViewById(R.id.listViewCanciones);


        //Concedemos y miramos si nestan los permisos otorgados
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        display();
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {}
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    /**
     * Buscamos las canciones y las metemos en un ArrayList de Ficheros
     * @param root
     * @return ArrayList<File>
     */
    public ArrayList<File> findSong(File root){
        ArrayList<File> at = new ArrayList<File>();
        File[] files = root.listFiles();
        for(File singleFile : files){
            if(singleFile.isDirectory()){
                //Si es un directorio volvemos a llamar al metodo
                at.addAll(findSong(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3")/*||singleFile.getName().endsWith(".wav")*/){

                    //Controlamos que solo se introduzca en nuestro array las canciones de la carpeta musica
                    String palabra = "musica";
                    String texto = singleFile.getParent();

                    if(texto.contains(palabra)){
                        at.add(singleFile);
                    }
                }
            }
        }
        return at;
    }

    /**
     * Metodo encargado de llamar al metodo findSong para ir creando el arraylist y sacando los nombres para la listView
     */
    public void display(){

        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        items = new String[ mySongs.size() ];
        for(int i=0;i<mySongs.size();i++){

            items[i] = mySongs.get(i).getName().toString().replace(".mp3","")/*.replace(".wav","")*/;

        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,items);
        listView.setAdapter(adp);


        //Metodo para meternos en el fragment de las canciones y poder pararlas, darle a siguiente... etc
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Contenido_Musica contenido_musica = new Contenido_Musica(mySongs.get(position), mySongs, position/*, todoMusica.get(0)*/);
                ((MainActivity) getActivity()).setFragmentMan(getFragmentManager());
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.nav_host_fragment,contenido_musica );
                transaction.addToBackStack(null);
                transaction.commit();
            }

        });
    }
}
