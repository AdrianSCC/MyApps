package com.example.myrssadrian.ui.juegos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Base64;
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
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class AdaptadorJuegos extends RecyclerView.Adapter<AdaptadorJuegos.ViewHolder> {

    Juego juego;
    ContenidoJuego contenidoJuego;
    private ArrayList<Juego> listaJuegos;
    private FragmentManager fragmentManager;

    //Contructor
    public AdaptadorJuegos(ArrayList<Juego> listaJuegos, FragmentManager fragmentManager){
        this.listaJuegos = listaJuegos;
        this.fragmentManager = fragmentManager;
    }
    @NonNull
    @Override
    public AdaptadorJuegos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //Creamos la vista
        View view =inflater.inflate(R.layout.list_items_juegos, parent, false);
        ViewHolder viewHolder = new ViewHolder((view));

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull AdaptadorJuegos.ViewHolder holder, int position) {

        final Juego juego = listaJuegos.get(position);
        //Metemos la informacion del juego
        holder.textViewNombre.setText(juego.getNombre());
        holder.textViewPrecio.setText(Float.toString(juego.getPrecio()));
        holder.textViewPlataforma.setText(juego.getPlataforma());
        holder.textViewFecha.setText(juego.getFecha());
        Bitmap imagenJuego = base64ToBitmap(juego.getImagen());
        holder.imageViewJuego.setImageBitmap(imagenJuego);

        //Cargamos los eventos
        holder.relativeLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Meterse dentro del contenido
                ContenidoJuego contenidoJuego = new ContenidoJuego(juego,3);
                FragmentTransaction transaction= fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment, contenidoJuego);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }
    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    @Override
    public int getItemCount() {
        return listaJuegos.size();
    }

    // Para borrar
    public void removeItem(int position) {
        listaJuegos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listaJuegos.size());
    }

    //para restaurar
    public void restoreItem(Juego item, int position) {
        listaJuegos.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, listaJuegos.size());
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        //declaracion de variables
        public TextView textViewNombre;
        public TextView textViewPrecio;
        public TextView textViewFecha;
        public TextView textViewPlataforma;
        public ImageView imageViewJuego;
        public RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewNombre = itemView.findViewById(R.id.tvTituloJuego);
            this.textViewPrecio = itemView.findViewById(R.id.tvPrecio);
            this.textViewPlataforma = itemView.findViewById(R.id.tvPlataforma);
            this.textViewFecha = itemView.findViewById(R.id.tvFecha);
            this.imageViewJuego = itemView.findViewById(R.id.ivImagenJuego);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.rlItemJuegos);
        }
    }

    public interface ListItemClick{
        void onListItemClick(int clickedItem);
    }

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
