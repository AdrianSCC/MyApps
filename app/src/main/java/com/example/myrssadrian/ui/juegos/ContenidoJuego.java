package com.example.myrssadrian.ui.juegos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myrssadrian.MainActivity;
import com.example.myrssadrian.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

public class ContenidoJuego extends Fragment {

    // Constantes
    private static final int GALERIA = 1 ;
    private static final int CAMARA = 2 ;

    // Si vamos a operar en modo público o privado
    private static final boolean PRIVADO = true;

    // Directorio para salvar las cosas
    private static final String IMAGE_DIRECTORY = "/imagenesjuegos";
    Uri photoURI;

    //variables
    private Juego juego;
    TextView textViewTitulo;
    private Button btnFecha, btnGuardar, btnActualizar, btnFoto;
    private TextView textViewFecha;
    private int dia, mes, ano, tipo;
    private EditText editTextPrecio;
    FloatingActionButton fab;
    Spinner plataformas;
    ImageView fotoJuego;
    private String path;

    //constructores
    public ContenidoJuego() {
        this.juego = juego;
    }
    public ContenidoJuego(int tipo) {
        this.juego = juego;
        this.tipo = tipo;
    }
    public ContenidoJuego(Juego juego) {
        this.juego = juego;
    }
    public ContenidoJuego(Juego juego, int tipo) {
        this.juego = juego;
        this.tipo = tipo;
    }
    public static ContenidoJuego newInstance(Juego juego) {
        return new ContenidoJuego(juego);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.contenido_juego_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //referenciamos los objjetos creados
        textViewTitulo = (TextView) getView().findViewById(R.id.etTituloContenidoJuego);
        btnFecha = getView().findViewById(R.id.btnFechaContenidoJuego);
        textViewFecha = getView().findViewById(R.id.tvFechaResultadoContenidoJuego);
        plataformas = (Spinner) getView().findViewById(R.id.spinnerPlataforma);
        editTextPrecio = getView().findViewById(R.id.etPrecioJuegoContenido);
        btnActualizar = getView().findViewById(R.id.btnActualizar);
        btnFoto = getView().findViewById(R.id.btnFoto);
        fotoJuego = getView().findViewById(R.id.ivContenidoJuego);
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoFoto();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.plataformas, android.R.layout.simple_spinner_item);
        plataformas.setAdapter(adapter);
        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                dia = calendar.get(Calendar.DAY_OF_MONTH);
                mes = calendar.get(Calendar.MONTH);
                ano = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        textViewFecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, dia, mes, ano);
                datePickerDialog.show();
            }
        });
        //damos funcionalidad al Floating action Button
        fab = getView().findViewById(R.id.fabContenidoJuegos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarJuego();

                JuegosFragment juegosFragment = new JuegosFragment();
                ((MainActivity) getActivity()).setFragmentMan(getFragmentManager());
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.nav_host_fragment, juegosFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        btnGuardar = getView().findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //registrarJuego();
                registrarJuegoSql();

                JuegosFragment juegosFragment = new JuegosFragment();
                ((MainActivity) getActivity()).setFragmentMan(getFragmentManager());
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.nav_host_fragment, juegosFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarJuegoSql();

                JuegosFragment juegosFragment = new JuegosFragment();
                ((MainActivity) getActivity()).setFragmentMan(getFragmentManager());
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.nav_host_fragment, juegosFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //METEMOS LOS DATOS DEL JUEGO EN TODAS LAS VISTAS MENOS EN LA DE AÑADIR
        if (tipo != 0) {
            //metemos el contenido del juego
            textViewTitulo.setText(juego.getNombre());
            seleccionarPlataformaItem();
            editTextPrecio.setText(Float.toString(juego.getPrecio()));
            Bitmap bitmapJuego = base64ToBitmap(juego.getImagen());
            textViewFecha.setText(juego.getFecha());
            fotoJuego.setImageBitmap(bitmapJuego);
        }
        if (tipo == 0) {//boton de agregar a la base de datos AGREGAR -> FAB
            fab.hide();
            btnActualizar.setVisibility(View.INVISIBLE);
        }
        if (tipo == 1) {//boton pulsar en el que se podra modificar los elementos EDITAR -> HACIA LA DER
            btnGuardar.setVisibility(View.INVISIBLE);
            fab.hide();

        }
        if (tipo == 2) {//boton para borrar de la base de datos BORRAR DATOS -> HACIA LA IZQ
            textViewTitulo.setEnabled(false);
            plataformas.setEnabled(false);
            textViewFecha.setEnabled(false);
            btnFecha.setVisibility(View.INVISIBLE);
            editTextPrecio.setEnabled(false);
            btnGuardar.setVisibility(View.INVISIBLE);
            btnActualizar.setVisibility(View.INVISIBLE);
            btnFoto.setVisibility(View.INVISIBLE);
        }
        if (tipo == 3) {//boton para solo ver los datos SOLO VER DATOS->PULSAR
            textViewTitulo.setEnabled(false);
            plataformas.setEnabled(false);
            textViewFecha.setEnabled(false);
            btnFecha.setVisibility(View.INVISIBLE);
            editTextPrecio.setEnabled(false);
            btnGuardar.setVisibility(View.INVISIBLE);
            fab.hide();
            btnActualizar.setVisibility(View.INVISIBLE);
            btnFoto.setVisibility(View.INVISIBLE);
        }

        // Pedimos los permisos
        pedirMultiplesPermisos();
    }



    private void mostrarDialogoFoto(){
        AlertDialog.Builder fotoDialogo= new AlertDialog.Builder(getContext());
        fotoDialogo.setTitle("Seleccionar Acción");
        String[] fotoDialogoItems = {
                "Seleccionar fotografía de galería",
                "Capturar fotografía desde la cámara" };
        fotoDialogo.setItems(fotoDialogoItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                elegirFotoGaleria();
                                break;
                            case 1:
                                tomarFotoCamara();
                                break;
                        }
                    }
                });
        fotoDialogo.show();
    }

    // Llamamos al intent de la galeria
    public void elegirFotoGaleria() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALERIA);
    }

    //Llamamos al intent de la camara
    private void tomarFotoCamara() {
        // Eso para alta o baja
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Esto para alta y baja
        startActivityForResult(intent, CAMARA);
    }

    // Siempre se ejecuta al realizar las accion
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("FOTO", "Opción::--->" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {return;}
        if (requestCode == GALERIA) {
            Log.d("FOTO", "Entramos en Galería");
            if (data != null) {
                // Obtenemos su URI con su dirección temporal
                Uri contentURI = data.getData();
                try {
                    // Obtenemos el bitmap de su almacenamiento externo
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                    path = salvarImagen(bitmap);
                    Toast.makeText(getContext(), "¡Foto salvada!", Toast.LENGTH_SHORT).show();
                    fotoJuego.setImageBitmap(bitmap);
                } catch (IOException e) {e.printStackTrace();
                    Toast.makeText(getContext(), "¡Fallo Galeria!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMARA) {
            Bitmap thumbnail = null;
            try {
                thumbnail = (Bitmap) data.getExtras().get("data");
                // salvamos
                path = salvarImagen(thumbnail);
                this.fotoJuego.setImageBitmap(thumbnail);
                Toast.makeText(getContext(), "¡Foto Salvada!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "¡Fallo Camara!", Toast.LENGTH_SHORT).show();
            }
        }
        if(PRIVADO){
            // Copiamos de la publica a la privada
            File ficheroDestino =  new File(getContext().getFilesDir() ,crearNombreFichero());
            File ficheroOrigen = new File(path);
            Log.d("FOTO", "Copiamos los ficheros");
            try {
                copiarFicheros(ficheroOrigen,ficheroDestino);// Copiamos
                borrarFichero(path);// Borramos
                path = ficheroDestino.getPath();// Ponemos el nuevo path
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "¡Fallo al pasar a memoria interna!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File crearFichero(){
        // Nombre del fichero
        String nombre = crearNombreFichero();
        return salvarFicheroPublico(nombre);
    }

    private String crearNombreFichero() {
        return Calendar.getInstance().getTimeInMillis() + ".jpg";
    }

    private File salvarFicheroPublico(String nombre) {
        // Vamos a obtener los datos de almacenamiento externo
        File dirFotos = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // Si no existe el directorio, lo creamos solo si es publico
        if (!dirFotos.exists()) {
            dirFotos.mkdirs();
        }

        // Vamos a crear el fichero con la fecha
        try {
            File f = new File(dirFotos, nombre);
            f.createNewFile();
            return f;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    // Función para salvar una imagem
    public String salvarImagen(Bitmap myBitmap) {
        // Comprimimos la imagen
        ByteArrayOutputStream bytes = comprimirImagen(myBitmap);
        File f = crearFichero();// Creamos el nombre del fichero
        FileOutputStream fo = null;// Escribimos en el el Bitmat en jpg creado
        try {
            fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            // Devolvemos el path
            return f.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "¡Fallo Salvar Fichero!", Toast.LENGTH_SHORT).show();
        }
        return "";
    }
    private ByteArrayOutputStream comprimirImagen(Bitmap myBitmap) {
        // Stream de binario
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Seleccionamos la calidad y la trasformamos y comprimimos
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        return bytes;
    }
    private void borrarFichero(String path) {
        // Borramos la foto de alta calidad
        File fdelete = new File(path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d("FOTO", "Foto borrada::--->" + path);
            } else {
                Log.d("FOTO", "Foto NO borrada::--->" +path);
            }
        }
    }

    private boolean copiarFicheros(File origen,File destino)throws IOException{
        if(origen.getAbsolutePath().toString().equals(destino.getAbsolutePath().toString())){

            return true;

        }else{
            InputStream is=new FileInputStream(origen);
            OutputStream os=new FileOutputStream(destino);
            byte[] buff=new byte[1024];
            int len;
            while((len=is.read(buff))>0){
                os.write(buff,0,len);
            }
            is.close();
            os.close();
        }
        return true;
    }



    /**/
    /**/
    /*                      BASE DE DATOS                         */
    /**/
    /**/

    public void registrarJuegoSql() {
        JuegosDBController conexion = new JuegosDBController(getContext(), "juegos", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();
        String insert = "INSERT INTO " + JuegosDBController.TABLA
                + "(" + JuegosDBController.ID + ","
                + JuegosDBController.NOMBRE + ","
                + JuegosDBController.PLATAFORMA + ","
                + JuegosDBController.IMAGEN + ","
                + JuegosDBController.FECHA + ","
                + JuegosDBController.PRECIO + ") VALUES ("
                + null + ",'"
                + textViewTitulo.getText().toString() + "','"
                + plataformas.getSelectedItem().toString() + "','"
                + insertarImagen()+ "','"
                + textViewFecha.getText().toString() + "','"
                + editTextPrecio.getText().toString() + "')";

        db.execSQL(insert);
        Toast.makeText(getContext(), "Juego Insertado", Toast.LENGTH_LONG).show();
        db.close();
    }

    public void actualizarJuegoSql() {

        JuegosDBController conexion = new JuegosDBController(getContext(), "juegos", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        String[] parametros = {Integer.toString(juego.getId())};
        ContentValues values = new ContentValues();
        values.put(JuegosDBController.NOMBRE, textViewTitulo.getText().toString());
        values.put(JuegosDBController.PLATAFORMA, plataformas.getSelectedItem().toString());
        values.put(JuegosDBController.IMAGEN, insertarImagen());
        values.put(JuegosDBController.FECHA, textViewFecha.getText().toString());
        values.put(JuegosDBController.PRECIO, editTextPrecio.getText().toString());

        db.update(JuegosDBController.TABLA, values, JuegosDBController.ID + "=?", parametros);
        Toast.makeText(getContext(), "Juego Actualizado", Toast.LENGTH_LONG).show();
        db.close();

    }

    public void eliminarJuego() {

        JuegosDBController conexion = new JuegosDBController(getContext(), "juegos", null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        String[] parametros = {Integer.toString(juego.getId())};

        db.delete(JuegosDBController.TABLA, JuegosDBController.ID + "=?", parametros);

        Toast.makeText(getContext(), "Juego Eliminado", Toast.LENGTH_LONG).show();
        db.close();
    }

    //METODO PARA SELECCIONAR EL ITEM DEL SPINNER SEGUN EL JUEGO AL QUE ACCEDEMOS
    public void seleccionarPlataformaItem() {
        switch (juego.getPlataforma()) {

            case "Nintendo Switch":
                plataformas.setSelection(0);
                break;
            case "Sony PS4":
                plataformas.setSelection(1);
                break;
            case "XBOX360":
                plataformas.setSelection(2);
                break;
            case "PC":
                plataformas.setSelection(3);
                break;
            case "Digital":
                plataformas.setSelection(4);
                break;
        }
    }

    // Funcion para programar los permisos usando Dexter
    private void pedirMultiplesPermisos(){
        // Indicamos el permisos y el manejador de eventos de los mismos
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // ccomprbamos si tenemos los permisos de todos ellos
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getContext(), "¡Todos los permisos concedidos!", Toast.LENGTH_SHORT).show();
                        }

                        // comprobamos si hay un permiso que no tenemos concedido ya sea temporal o permanentemente
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // abrimos un diálogo a los permisos
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getContext(), "Existe errores! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    /*___________________________________Metodos para escribir la imagen en la Base de Datos_________________________________*/

    /*Metodos para las fotos*/

    //https://www.thepolyglotdeveloper.com/2015/06/from-bitmap-to-base64-and-back-with-native-android/
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public String insertarImagen(){

        String resultado="22";

        try {
            int alto = 100;
            int ancho = 100;

            Bitmap bitmap = ((BitmapDrawable)fotoJuego.getDrawable()).getBitmap();

            bitmap = Bitmap.createScaledBitmap(bitmap, alto, ancho, true);

            resultado = bitmapToBase64(bitmap);

            return resultado;
        }catch (Exception e){
            Toast.makeText(getContext(), "¡Todos los permisos concedidos!", Toast.LENGTH_SHORT).show();
        }

        return resultado;

    }




}//fin del Fragment ContenidoJuego

