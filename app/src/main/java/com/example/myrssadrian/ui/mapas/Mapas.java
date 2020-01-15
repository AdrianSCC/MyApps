package com.example.myrssadrian.ui.mapas;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.myrssadrian.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Mapas extends Fragment  implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    //Variables a utilizar
    GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private FloatingActionButton fabStart;
    private FloatingActionButton fabMisRutas;
    private TextView textViewTiempo;
    private TextView textViewDistancia;
    private boolean rutaStart = false;
    private boolean pulsadoStart = false;
    private boolean pulsadoMisRutas = false;
    MisRutas misRutas;
    private FusedLocationProviderClient mPosicion;
    private Location ultimaLocalizacion;
    private LatLng posicionActual;
    private ArrayList<LatLng> listaMovimiento = new ArrayList<>();
    private Marker markerInicio;
    private Marker markerFin;
    private int horas=0,minutos= 0, segundos=0;
    private double distanciaTotal = 0;
    StringBuffer stringBufferTiempo;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mapas_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //referenciamos los textView
        textViewDistancia = getView().findViewById(R.id.tvRecorridoRuta);
        textViewTiempo = getView().findViewById(R.id.tvTiempoRuta);

        //pintamos el mapa
        mPosicion = LocationServices.getFusedLocationProviderClient(getActivity());
        FragmentManager fm = getChildFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);

        //referenciamos y damos funcionalidad al boton de inicio (START)
        this.fabStart = getView().findViewById(R.id.fabStart);
        this.fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //al pulsarlo por primera vez empieza la ruta, y se pone en rojo el floating action button
                if (!pulsadoStart){
                    pulsadoStart=true;
                    fabStart.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRojo)));
                    //comienza la ruta
                    ruta();
                }else{//finalizamos la ruta y ponemos el floating action button con el color inicial
                    pulsadoStart= false;

                    //mediante esta variable finalizamos la ruta para que no siga trazando la ruta en el mapa
                    rutaStart=false;

                    //cambiamos el color al button
                    fabStart.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

                    //Colocamos el marcador del final de la ruta recogiendo el ultimo valor de la ruta.
                    setmarkerFin(listaMovimiento.get(listaMovimiento.size() - 1));

                    //Cogemos la lista con todos los movimientos registrados en el arraylist y creamos el fichero .xml con los damos GPX
                    try {
                        escritorGPX(listaMovimiento);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    distanciaTotal = 0;

                    textViewDistancia.setText("Recorrido de la Ruta");

                }
            }
        });


        //Referenciamos y damos funcionalidad al boton de mis rutas
        this.fabMisRutas = getView().findViewById(R.id.fabMisRutas);
        this.fabMisRutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //A continuacion controlaremos para mostrar o no el fragment de mis rutas

                if (!pulsadoMisRutas){//para mostrarlo
                    pulsadoMisRutas=true;

                    //creamos un nuevo objeto, al que le pasamos este mismo fragment
                    misRutas = new MisRutas(obtenerRutas());

                    //pintamos el fragment MisRutas
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.listaRutasMapas, misRutas)
                            .addToBackStack(null).commit();


                }else{//para ocultarlo
                    pulsadoMisRutas=false;

                    //eliminamos el fragment
                    getFragmentManager().
                            beginTransaction().
                            remove(misRutas).
                            commit();
                }
            }
        });
    }

    //metodo para enviar este fragment al fragment MisRutas
    public Mapas obtenerRutas() {
        return this;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


        //Ajustamos el mapa y obtenemos nuestra posicion
        map.setMinZoomPreference(17.0f);
        map.setOnMarkerClickListener(this);
        map.setMyLocationEnabled(true);
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setMapToolbarEnabled(true);

        obtenerPosicion();
    }

    /**
     * Obtenemos nuestra posicion
     */
    public void obtenerPosicion() {
        try {

            Task<Location> local = mPosicion.getLastLocation();
            local.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        ultimaLocalizacion = task.getResult();
                        if (ultimaLocalizacion != null) {
                            posicionActual = new LatLng(ultimaLocalizacion.getLatitude(),ultimaLocalizacion.getLongitude());
                            map.moveCamera(CameraUpdateFactory.newLatLng(posicionActual));
                            listaMovimiento.add(posicionActual);//Añadimos la posición actual a la lista
                        } else {
                            Toast.makeText(getContext(), "No puede obternerse la situacion actual",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d("GPS", "No se encuetra la última posición.");
                        Log.e("GPS", "Exception: %s", task.getException());
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    /**
     * Metodo para iniciar la ruta en el mapa y pintar la trayectoria de la misma
     */
    public void ruta (){

        //primero limpiamos la lista y el mapa, por si hubiera otra ruta anterior
        listaMovimiento.clear();
        map.clear();

        //Iniciamos la ruta
        rutaStart=true;

        //Borramos las marcas de inicio y fin de ruta, si las hubiera
        if (markerFin!= null && markerInicio!=null){
            markerFin.remove();
            markerInicio.remove();
            Toast.makeText(getContext(),"Marcas borradas",Toast.LENGTH_LONG).show();
        }

        //iniciamos el contador del tiempo de la ruta
        iniciarContadorRuta();

        //marcamos la posicion de inicio de la ruta
        setmarkerInicio(posicionActual);

        //pintamos la ruta con el siguiente metodo
        trazarRuta();
    }


    /**
     * Iniciamos el contador de la ruta
     */
    public void iniciarContadorRuta(){

        //creamos un timer que lleve el contador
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask timerTaskTiempo = new TimerTask() {

            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        //Creamos un StringBuffer para almacenar el tiempo
                        stringBufferTiempo = new StringBuffer("");

                        //controlamos que la ruta este iniciada
                        if (rutaStart) {

                            //Vamos sumando el tiempo
                            if (segundos > 60) {
                                segundos = 0;

                                if (minutos > 60) {
                                    minutos = 0;
                                    horas++;
                                } else {
                                    minutos++;
                                }
                            } else {
                                segundos++;
                            }
                        } else {
                            segundos = 0;
                            minutos = 0;
                            horas = 0;
                        }

                        //Añadimos las variables al StringBuffer
                        stringBufferTiempo.append(horas);
                        stringBufferTiempo.append(" : ");
                        stringBufferTiempo.append(minutos);
                        stringBufferTiempo.append(" : ");
                        stringBufferTiempo.append(segundos);

                        //Pintamos el resultado en el textView del tiempo
                        textViewTiempo.setText(stringBufferTiempo.toString());
                    }
                });
            }
        };
        //Esperamos un segundo para volver a repetirlo
        timer.schedule(timerTaskTiempo, 0, 1000);
    }

    /**
     * Trazamos la ruta en el mapa
     */
    public void trazarRuta(){

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask timerTaskDibujar = new TimerTask() {

            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        //controlamos que la ruta este iniciada
                        if (rutaStart) {
                            try {
                                //Obtenemos nuestra localizacion la cual se introducira en la lista
                                obtenerPosicion();

                                //pintamos la linea y le damos el color azul
                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.BLUE);
                                for (int i = 0; i < listaMovimiento.size(); i++) {
                                    polylineOptions.add(listaMovimiento.get(i));
                                }
                                distancia();
                                map.addPolyline(polylineOptions);

                            } catch (Exception e) {
                                Log.e("TIMER", "Error: " + e.getMessage());
                            }
                        }
                    }
                });
            }
        };
        //esperamos 2 segundos para volver a repetir
        timer.schedule(timerTaskDibujar, 0, 2000);
    }

    /**
     * Metodo con el cual añadimos una marca en el mapa, el cual sera el inicio de la ruta
     *
     * Le pasaremos la posicion actual para situar la marca
     * @param pos
     */
    public void setmarkerInicio (LatLng pos) {
        markerInicio = map.addMarker(new MarkerOptions()
                .position(pos)
                .title("Start")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        );
    }

    /**
     * Metodo con el cual añadimos una marca en el mapa, el cual sera el fin de la ruta
     *
     * Le pasaremos la posicion actual para situar la marca
     * @param pos
     */
    public void setmarkerFin(LatLng pos) {
        markerFin = map.addMarker(new MarkerOptions()
                .position(pos)
                .title("End")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        );
    }




    /**
     * Metodo con el cual obtenemos la distancia y la vamos pintando en el textView
     */
    private void distancia() {

        //controlamos que haya mas de una posicion para calcular la distancia
        if(listaMovimiento.size() > 1) {
            distanciaTotal = distanciaTotal + calculoDistancia(
                    listaMovimiento.get(listaMovimiento.size()-2).latitude,
                    listaMovimiento.get(listaMovimiento.size()-2).longitude,
                    listaMovimiento.get(listaMovimiento.size()-1).latitude,
                    listaMovimiento.get(listaMovimiento.size()-1).longitude);

            //pintamos la distancia formateada para sacarla en kilometros
            textViewDistancia.setText(String.format("%3f", distanciaTotal)+" km");
        }
    }

    /**
     * Metodo para calcular la distancia entre dos puntos del mapa
     *
     * Le pasamos al metodo la latitud y la longitud de los dos puntos del mapa
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     *
     * Este metodo devuelve la distancia entre estos dos puntos
     * @return
     */
    public double calculoDistancia(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double distancia = Math.sin(gradosToRadianes(lat1))
                * Math.sin(gradosToRadianes(lat2))
                + Math.cos(gradosToRadianes(lat1))
                * Math.cos(gradosToRadianes(lat2))
                * Math.cos(gradosToRadianes(theta));
        distancia = Math.acos(distancia);
        distancia = radianesToGrados(distancia);
        distancia = distancia * 60 * 1.1515;
        return distancia;
    }

    /**
     * Metodo que pasa de grados a radianes
     * @param deg
     * @return
     */
    public double gradosToRadianes(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * Metodo que pasa de radianes a grados
     * @param rad
     * @return
     */
    public double radianesToGrados(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    /**
     * Metodo que escribe un XML en una ruta del directerio
     * El fichero XML seguira las etiquetas GPX
     * Recibira la lista con las localizaciones de la ruta
     *
     * @param lista
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public void escritorGPX(ArrayList<LatLng> lista) throws IOException, ParserConfigurationException, TransformerException {


        File file = new File (Environment.getExternalStorageDirectory()+"/misMapas/rutasGPX");

        //creamos el directorio si no existe
        if  (!file.exists()){
            file.mkdirs();
        }

        File fileGPX = new File(Environment.getExternalStorageDirectory()+"/misMapas/rutasGPX", System.currentTimeMillis()+".xml");

        /**CREAMOS LA ESTRUCTURA FICHERO GPX, E INTRODUCIMOS LOS DATOS DE LA LISTA*/
        fileGPX.createNewFile();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element element = document.createElement("gpx");
        element.setAttribute("xmlns","http://www.topografix.com/GPX/1/1");
        element.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
        element.setAttribute("creator","adrian");
        element.setAttribute("version","1.1");
        element.setAttribute("xsi:schemaLocation","http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
        document.appendChild(element);
        Element trk = document.createElement("trk");
        element.appendChild(trk);
        Element trkseg = document.createElement("trkseg");
        trk.appendChild(trkseg);
        for (int i=0;i<lista.size();i++){
            Element trkpt = document.createElement("trkpt");
            trkpt.setAttribute("lat",String.valueOf(lista.get(i).latitude));
            trkpt.setAttribute("lon",String.valueOf(lista.get(i).longitude));
            trkseg.appendChild(trkpt);
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(fileGPX.getPath());
        transformer.transform(source, result);
    }

    /**
     * Metodo encargado de Leer un fichero dado
     * Este metodo devolvera un arraylist, el cual incluye la ruta para pintarla en el mapa
     *
     * @param file
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public ArrayList<LatLng> lectorGPX(File file) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        NodeList itemes = document.getElementsByTagName("trkpt");
        ArrayList<LatLng> listaRutas = new ArrayList<>();

        //RECORREMOS EL FICHERO AÑADIENDO EN EL ARRAYLIST EL CONTENIDO DE ESTE
        for (int i=0;i<itemes.getLength();i++){
            Node node = itemes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element)node;
                Double lat = Double.parseDouble(element.getAttribute("lat"));
                Double lon = Double.parseDouble(element.getAttribute("lon"));
                LatLng latLng = new LatLng(lat,lon);
                listaRutas.add(latLng);
            }
        }
        //DEVOLVEMOS LA LISTA
        return  listaRutas;

    }

    public GoogleMap obtenerMapa() {return map;}


}
