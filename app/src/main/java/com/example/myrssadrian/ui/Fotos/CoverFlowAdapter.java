package com.example.myrssadrian.ui.Fotos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myrssadrian.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CoverFlowAdapter extends BaseAdapter {

    //Variables a utilizar en la clase
    private ArrayList<String> imagenes;
    private Context activity;
    private FragmentManager fragmentManager;
    private FotosFragment fotosFragment = new FotosFragment();


    //Constructor
    public CoverFlowAdapter(Context context, ArrayList<String> imagenes, FragmentManager fragmentManager) {
        this.activity = context;
        this.imagenes = imagenes;
        this.fragmentManager = fragmentManager;
    }


    @Override
    public int getCount() {
        return imagenes.size();
    }

    @Override
    public String getItem(int position) {
        return imagenes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Creamos la vista
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_flow_view, null, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            Bitmap bitmap = null;
            File fichero = new File(imagenes.get(position));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(fichero),null,options);
            viewHolder.image.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        convertView.setOnClickListener(onClickListener(position));

        return convertView;
    }


    private static class ViewHolder {
        private TextView name;
        private ImageView image;

        public ViewHolder(View v) {
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
        }
    }

    /**
     * Metodo para entrar en las fotos de la galeria
     * @param position
     * @return
     */
    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Toast.makeText(activity, "pulsaste"+getItemId(position)+imagenes.get(position), Toast.LENGTH_LONG).show();
                entrarContenido(imagenes.get(position));

            }
        };
    }
    /**
     * Metodo para entrar en el contenido de la foto seleccionada
     * @param imagen
     */
    public void entrarContenido(String imagen){

        ContenidoFotos contenidoFotos = new ContenidoFotos(imagen);
        //((MainActivity) getActivity()).setFragmentMan(getFragmentManager());

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment,contenidoFotos );
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

