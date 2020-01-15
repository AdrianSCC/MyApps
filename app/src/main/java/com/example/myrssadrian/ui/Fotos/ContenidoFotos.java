package com.example.myrssadrian.ui.Fotos;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myrssadrian.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ContenidoFotos extends Fragment {

    //Variables a utilizar en la clase
    private ImageView ivImagenSeleccionada;
    private FloatingActionButton fabCompartir;
    String path;

    //Contructores
    public ContenidoFotos() {
    }
    public ContenidoFotos(String imagen) {
        this.path = imagen;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contenido_fotos, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //asignamos las variables a su valor en el layout
        ivImagenSeleccionada = getView().findViewById(R.id.ivImagenContenidoFoto);
        //fabCompartir = getView().findViewById(R.id.fabImagenContenidoFoto);
        String img = path;
        //Obtiene la uri de la imagen.
        Uri uriImagen = Uri.parse(img);
        //Agrega imagen al ImageView.
        ivImagenSeleccionada.setImageURI(uriImagen);
    }

}
