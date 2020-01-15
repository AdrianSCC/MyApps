package com.example.myrssadrian.ui.juegos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myrssadrian.MainActivity;
import com.example.myrssadrian.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class JuegosFragment extends Fragment implements AdaptadorJuegos.ListItemClick{

    private RecyclerView recyclerView;
    JuegosDBController conexion;
    private AdaptadorJuegos adaptadorJuegos;
    private SwipeRefreshLayout swipeRefreshLayout;
    Lector leer;
    static int tipoOrdenacion = 1;
    Spinner spinnerOrdenacion;
    // Deslizamientos horizontales
    private Paint p = new Paint();
    private ArrayList<Juego> listaJuegos = new ArrayList<>();


    public static JuegosFragment newInstance() {
        return new JuegosFragment();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.juegos_fragment, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //llamamos al recycler view
        recyclerView = getView().findViewById(R.id.rvJuegos);

        //creamos el linear layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getView().getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //introducimos un separador entre los juegos de la lista
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager)recyclerView.getLayoutManager()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        //llamada a la base de datos
        conexion = new JuegosDBController(getContext(),"juegos", null,1);

        //creacion del spinner de ordenacion
        spinnerOrdenacion = getView().findViewById(R.id.spinnerOrdenacion);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.ordenacion, android.R.layout.simple_spinner_item);
        spinnerOrdenacion.setAdapter(adapter);

        //realizaremos una opcion u otra en funcion del item seleccionado
        spinnerOrdenacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        tipoOrdenacion=2;
                        leer = new Lector();
                        leer.execute();

                        break;
                    case 1:
                        tipoOrdenacion = 3;
                        leer = new Lector();
                        leer.execute();

                        break;
                    case 2:
                        tipoOrdenacion = 4;
                        leer = new Lector();
                        leer.execute();

                        break;
                    case 3:
                        tipoOrdenacion = 5;
                        leer = new Lector();
                        leer.execute();
                        break;
                    default:
                        leer = new Lector();
                        leer.execute();
                        break;
                }
            }

            // Debido a que el AdapterView es una abstract class, onNothingSelected debe ser tambien definido
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                leer = new Lector();
                leer.execute();
            }
        });

        //iniciar el Swiper de recargar
        iniciarSwipeRecargar();
        //iniciar el swipe Horizontal
        iniciarSwipeHorizontal();
        //cargar los datos de la base de datos
        leer = new Lector();
        leer.execute();

        //damos funcionalidad al Floating action Button
        FloatingActionButton fab = getView().findViewById(R.id.fabContenidoJuegos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ContenidoJuego contenido = new ContenidoJuego(0);
                ((MainActivity) getActivity()).setFragmentMan(getFragmentManager());
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.nav_host_fragment,contenido );
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    //metodo de refrescar
    private void iniciarSwipeRecargar() {

        // Para refrescar y volver al cargar
        swipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayoutJuegos);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Cambiamos colores
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary);
                // Volvemos a cargar los datos
                //cargar los datos de la base de datos
                tipoOrdenacion =1;//le damos el valor 1 para que muestre todos los datos
                leer = new Lector();
                leer.execute();
            }
        });
    }

    //metodo de deslizar
    private void iniciarSwipeHorizontal() {
        //inicializamos el item
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            // Evento al mover
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                // Si nos movemos a la izquierda
                if (direction == ItemTouchHelper.LEFT) {
                    //nos metemos dentro del contenido para borrarlo
                    Juego juego = listaJuegos.get(position);
                    ContenidoJuego contenido = new ContenidoJuego(juego,2);
                    ((MainActivity) getActivity()).setFragmentMan(getFragmentManager());
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment,contenido );
                    transaction.addToBackStack(null);
                    transaction.commit();



                } else {
                    //Si no movemos a la derecha
                    //Meterse dentro del contenido para modificarlo
                    Juego juego = listaJuegos.get(position);
                    ContenidoJuego contenido = new ContenidoJuego(juego,1);
                    ((MainActivity) getActivity()).setFragmentMan(getFragmentManager());
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment,contenido );
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }



            // Dibujamos los botones y eventos
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    // direccion hacia la derecha
                    if (dX > 0) {
                        p.setColor(Color.parseColor("#1371cb"));//ponemos el color de fondo
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        //añadimos el icono
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_abrir);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width
                                , (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);

                    } else {//direccion hacia la izquierda
                        p.setColor(Color.RED);//ponemos el color de fondo
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        //añadimos el icono
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_borrar);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width
                                , (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onListItemClick(int clickedItem) {

    }

    //creamos la clase asyntask para ir rescatando de la base de datos los juegos
    public class Lector extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {

            try {
                //le pasamos al lector la url de donde extraer las noticas
                listaJuegos = LeerJuegos();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        //metodo que se ejecutara al final
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ((MainActivity) getActivity()).setFragmentMan(getFragmentManager());
            //creamos el adaptador que nos devolvera la lista de noticas
            adaptadorJuegos = new AdaptadorJuegos(listaJuegos, getFragmentManager());
            recyclerView.setAdapter(adaptadorJuegos);
            adaptadorJuegos.notifyDataSetChanged();
            recyclerView.setHasFixedSize(true);
            //ocultamos el swipe
            swipeRefreshLayout.setRefreshing(false);

        }


        public ArrayList<Juego> LeerJuegos()  {
            listaJuegos.clear();//limpiamos el arraylist para rellenarlo con los datos de la bbdd
            Juego juego;
            boolean fin = false;
            SQLiteDatabase db = conexion.getReadableDatabase();
            String sql="SELECT * FROM "+ JuegosDBController.TABLA;
            switch (tipoOrdenacion){//controlamos el tipo de orden que debe llevar la consulta
                case 1://Todos
                    sql = "SELECT * FROM "+ JuegosDBController.TABLA;
                    break;
                case 2://Nombre
                    sql = "SELECT * FROM "+ JuegosDBController.TABLA+" ORDER BY "+JuegosDBController.NOMBRE;
                    break;
                case 3://Plataforma
                    sql = "SELECT * FROM "+ JuegosDBController.TABLA+" ORDER BY "+JuegosDBController.PLATAFORMA;
                    break;
                case 4://Fecha
                    sql = "SELECT * FROM "+ JuegosDBController.TABLA+" ORDER BY "+JuegosDBController.FECHA;
                    break;
                case 5://Precio
                    sql = "SELECT * FROM "+ JuegosDBController.TABLA+" ORDER BY "+JuegosDBController.PRECIO;
                    break;
            }
            try {
                //Cursor cursor = db.query(JuegosDBController.TABLA,campos, null,null,null,null,null);
                Cursor cursor = db.rawQuery(sql,null);
                cursor.moveToFirst();
                while (!fin){
                    if (cursor.isLast()){
                        fin= true;
                    }
                    juego = new Juego(cursor.getInt(0),cursor.getString(1),cursor.getString(2)
                            ,cursor.getString(3),cursor.getString(4),cursor.getFloat(5));
                    listaJuegos.add(juego);
                    cursor.moveToNext();
                }
                fin = false;
                cursor.close();
            }catch (Exception e){
                Toast.makeText(getContext(), "El documento no existe", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            tipoOrdenacion =1;//volvemos a darle el valor 1
            return listaJuegos;
        }
    }
}
