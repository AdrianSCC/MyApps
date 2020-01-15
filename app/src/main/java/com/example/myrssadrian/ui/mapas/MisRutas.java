package com.example.myrssadrian.ui.mapas;

import android.graphics.Color;
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

import com.example.myrssadrian.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.util.ArrayList;

public class MisRutas extends Fragment {

    //Declaracion de variables
    ArrayList<String> listaRutas = new ArrayList<String>();
    private Mapas mapas;
    ListView listView;

    /**
     * Constructor
     * @param mapas
     */
    public MisRutas(Mapas mapas) { this.mapas=mapas;}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_rutas, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Creamos la referencia al listView
        listView =  getView().findViewById(R.id.listviewMisRutas);

        //Le pasamos la carpeta para listar los titulos
        File file = new File(Environment.getExternalStorageDirectory().toString()+"/misMapas/rutasGPX");
        final File listaFichero[] = file.listFiles();

        for (int i=0;i<listaFichero.length;i++){
            listaRutas.add(listaFichero[i].getName());
        }

        /**
         * Pintamos en el ListView los nombres de las rutas
         */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,listaRutas);
        listView.setAdapter(adapter);
        listView.setBackgroundColor(Color.GRAY);


        /**
         * Metodo para poder seleccionar las rutas y pintarlas en el mapa
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                try {

                    File fichero = new File("/storage/emulated/0/misMapas/rutasGPX/"+listaRutas.get(position));
                    ArrayList<LatLng> listaRutas = null;

                    listaRutas = mapas.lectorGPX(fichero);

                    PolylineOptions opciones = new PolylineOptions();
                    opciones.color(Color.BLACK);
                    for(int i=0;i<listaRutas.size();i++){
                        opciones.add(listaRutas.get(i));
                    }
                    //Limpiamos el mapa y pintamos la ruta guardada
                    mapas.obtenerMapa().clear();
                    mapas.setmarkerInicio(listaRutas.get(0));
                    mapas.setmarkerFin(listaRutas.get(listaRutas.size()-1));
                    mapas.obtenerMapa().addPolyline(opciones);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
