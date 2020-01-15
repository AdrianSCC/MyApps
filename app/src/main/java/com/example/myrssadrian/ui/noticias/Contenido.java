package com.example.myrssadrian.ui.noticias;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myrssadrian.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class Contenido extends Fragment {

    private static String enlace;
    private ContenidoViewModel mViewModel;
    private Noticia noticia;


    //constructores
    public  Contenido (){
        this.noticia = noticia;
    }
    public Contenido(Noticia noticia) {
        this.noticia = noticia;
    }

    public static Contenido newInstance(Noticia noticia) {
        return new Contenido(noticia);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.contenido_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ContenidoViewModel.class);

        //referenciamos los objjetos creados
        final TextView textViewTitulo = (TextView) getView().findViewById(R.id.tvTituloContenido);
        final ImageView imageView = (ImageView) getView().findViewById(R.id.ivContenido);
        final WebView webViewContenido = (WebView) getView().findViewById(R.id.wvContenido);

        //damos funcionalidad al Floating action Button
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(noticia.getLink());//recogemos el link de la noticia
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //metemos el contenido de la noticia en las variables
        textViewTitulo.setText(noticia.getTitulo());
        Picasso.get()
                .load(noticia.getImagen())
                .into(imageView);
        webViewContenido.loadData(noticia.getContenido(),"text/html",null);

        enlace = noticia.getLink();
    }

    //metodo con el que le pasamos el enlace al boton compartir del menu
    public static String link(){
        return enlace;
    }


    //visualizamos los botones de compartir y volver atras
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_compartir);
        item.setVisible(true);
        MenuItem item1 = menu.findItem(R.id.action_volver);
        item1.setVisible(true);
    }


}
