package com.example.myrssadrian.ui.noticias;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrssadrian.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ViewHolder>{

    private ArrayList<Noticia> lista;
    private FragmentManager fragmentManager;

    //Contructor
    public Adaptador(ArrayList<Noticia> lista, FragmentManager fragmentManager) {
        this.lista = lista;
        this.fragmentManager = fragmentManager;
    }

    public interface ListItemClick{
        void onListItemClick(int clickedItem);
    }


    @Override //inflara la vista
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //creamos la vista
        View view = inflater.inflate(R.layout.list_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //Controlamos como vamos a meter la informacion en los textView
        final Noticia noticia = lista.get(position);
        String fecha = noticia.getFecha();
        fecha = fecha.substring(0,16);
        String hora = noticia.getFecha().substring(16,25);

        //Metemos en los textView la informacion de la noticia
        holder.textViewTitulo.setText(noticia.getTitulo());
        holder.textViewFecha.setText(fecha);
        holder.textViewHora.setText(hora);

        //Con la libreria picasso rescatamos y redondeamos la imagen
        Picasso.get()
                .load(noticia.getImagen())
                .transform(new CircleTransform())
                .resize(375,200)
                .into(holder.imageViewImagen);


        // Cargamos los eventos de los componentes
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //Meterse dentro del contenido

                Contenido contenido = new Contenido(noticia);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment, contenido);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    // Para borrar
    public void removeItem(int position) {
        lista.remove(position);
        Log.i("posicion", Integer.toString(position));
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, lista.size());
    }

    //para restaurar
    public void restoreItem(Noticia item, int position) {
        //listdata.set(position, item);
        lista.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, lista.size());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewTitulo;
        public TextView textViewFecha;
        public TextView textViewHora;
        public ImageView imageViewImagen;
        public RelativeLayout relativeLayout;


        //referenciamos mediante su id
        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewTitulo = (TextView) itemView.findViewById(R.id.tvTituloNoticia);
            this.textViewFecha = (TextView) itemView.findViewById(R.id.tvFechaContenidoJuego);
            this.textViewHora = (TextView) itemView.findViewById(R.id.tvHora);
            this.imageViewImagen = (ImageView) itemView.findViewById(R.id.ivImagenNoticia);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.rlItem);

        }
    }


    /**
     * Creamos la clase para hacer circulares las imagenes
     */
    class CircleTransform implements Transformation {

        boolean mCircleSeparator = false;

        public CircleTransform() {
        }

        public CircleTransform(boolean circleSeparator) {
            mCircleSeparator = circleSeparator;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
            Canvas canvas = new Canvas(bitmap);
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
            paint.setShader(shader);
            float r = size / 2f;
            canvas.drawCircle(r, r, r - 1, paint);

            Paint paintBorder = new Paint();
            paintBorder.setStyle(Paint.Style.STROKE);
            paintBorder.setColor(Color.argb(84, 0, 0, 0));
            paintBorder.setAntiAlias(true);
            paintBorder.setStrokeWidth(1);
            canvas.drawCircle(r, r, r - 1, paintBorder);

            if (mCircleSeparator) {
                Paint paintBorderSeparator = new Paint();
                paintBorderSeparator.setStyle(Paint.Style.STROKE);
                paintBorderSeparator.setColor(Color.parseColor("#ffffff"));
                paintBorderSeparator.setAntiAlias(true);
                paintBorderSeparator.setStrokeWidth(4);
                canvas.drawCircle(r, r, r + 1, paintBorderSeparator);
            }
            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

}
