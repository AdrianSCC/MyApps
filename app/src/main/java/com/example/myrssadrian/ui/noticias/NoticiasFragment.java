package com.example.myrssadrian.ui.noticias;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myrssadrian.MainActivity;
import com.example.myrssadrian.R;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class NoticiasFragment extends Fragment implements Adaptador.ListItemClick{

    //creacion de variables
    private NoticiasViewModel noticiasViewModel;
    private RecyclerView recyclerView;
    private ArrayList<Noticia> lista;
    private Adaptador adaptador;
    private LectorRSS leer;
    // Deslizamientos horizontales
    private Paint p = new Paint();
    // Para refrescar
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        noticiasViewModel =
                ViewModelProviders.of(this).get(NoticiasViewModel.class);
        View root = inflater.inflate(R.layout.fragment_noticias, container, false);

        return root;
    }

    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        //llamamos al recycler view
        recyclerView = getView().findViewById(R.id.rvNoticias);

        //creamos el linear layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getView().getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //Introducimos un separador entre las noticias de la lista
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Iniciamos el Swiper de Recargar
        iniciarSwipeRecargar();
        // Iniciamos el Swipe Horizontal
        iniciarSwipeHorizontal();

        leer = new LectorRSS();
        leer.execute("e");

    }


    private void iniciarSwipeRecargar() {
        // Para refrescar y volver al cargar
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Cambiamos colores
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary);
                // Volvemos a cargar los datos
                leer = new LectorRSS();
                leer.execute("e");
            }
        });
    }

    private void iniciarSwipeHorizontal() {
        //inicializamos el item
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

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
                    //borramos el elemento con el siguiente metodo
                    borrarElemento(position);
                } else {
                    //Si no movemos a la derecha
                    //Meterse dentro del contenido
                    Noticia noticiaFragment = (Noticia)lista.get(position);
                    Contenido contenido = new Contenido(noticiaFragment);
                    ((MainActivity) getActivity()).setFragmentMan(getFragmentManager());
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, contenido);
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
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_entrar);
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

    private void borrarElemento(int position) {
        final Noticia deletedModel = lista.get(position);
        final int deletedPosition = position;
        adaptador.removeItem(position);
        // Mostramos la barra
        Snackbar snackbar = Snackbar.make(getView(), " eliminado de la lista!", Snackbar.LENGTH_LONG);
        snackbar.setAction("DESHACER", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                adaptador.restoreItem(deletedModel, deletedPosition);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }


    //este metodo no hace nada
    public void nada(){
        //hacer nada
    }


    @Override
    public void onListItemClick(int clickedItem) {
    }


    //creamos la clase asyntask
    public class LectorRSS extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {

            try {
                //le pasamos al lector la url de donde extraer las noticas
                String direccion= "https://futbol.as.com/rss/futbol/primera.xml";
                lista = LeerRSS(direccion);
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
            adaptador = new Adaptador(lista, getFragmentManager());
            recyclerView.setAdapter(adaptador);
            adaptador.notifyDataSetChanged();
            recyclerView.setHasFixedSize(true);
            //ocultamos el swipe
            swipeRefreshLayout.setRefreshing(false);

        }

        /***
         * Metodo encargado de leer un fichero xml mediante una url dada.
         * Se encarga de ir metiendo en un arrayList todos los elementos hijos de item, que forman la noticia
         *
         * @param url
         * @return
         */
        public ArrayList<Noticia> LeerRSS(String url)  {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            String uri = url;
            ArrayList<Noticia> noticias = new ArrayList();

            try {

                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(uri);
                NodeList items = document.getElementsByTagName("item");

                for (int i = 0; i < items.getLength(); i++) {
                    Node nodo = items.item(i);
                    Noticia noticia = new Noticia();
                    int contadorImagenes = 0;
                    for (Node n = nodo.getFirstChild(); n != null; n = n.getNextSibling()) {

                        if (n.getNodeName().equals("title")) {
                            String titulo = n.getTextContent();
                            noticia.setTitulo(titulo);
                        }
                        if (n.getNodeName().equals("link")) {
                            String enlace = n.getTextContent();
                            noticia.setLink(enlace);
                        }
                        if (n.getNodeName().equals("description")) {
                            String descripcion = n.getTextContent();
                            noticia.setDescripcion(descripcion);
                        }
                        if (n.getNodeName().equals("pubDate")) {
                            String fecha = n.getTextContent();
                            noticia.setFecha(fecha);
                        }
                        if (n.getNodeName().equals("content:encoded")) {
                            String contenido = n.getTextContent();
                            if (contenido.length()>680){
                                contenido = contenido.substring(0,600)+" ...";
                            }else{
                                contenido="No existe contenido en estos momentos";
                            }
                            noticia.setContenido(contenido);

                        }
                        if (n.getNodeName().equals("enclosure")) {
                            Element e = (Element) n;
                            String imagen = e.getAttribute("url");
                            //Controlamos que solo rescate una imagen
                            if (contadorImagenes == 0) {
                                noticia.setImagen(imagen);
                            }
                            contadorImagenes++;
                        }
                    }
                    //controlamos que solo meta en nuestro arrayList 20 noticias
                    if (noticias.size()<20){
                        noticias.add(noticia);
                    }
                }
            } catch (ParserConfigurationException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (DOMException e) {
                System.out.println(e.getMessage());
            } catch (SAXException e) {
                System.out.println(e.getMessage());
            }
            return  noticias;
        }
    }
}