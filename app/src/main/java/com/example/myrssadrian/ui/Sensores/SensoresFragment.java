package com.example.myrssadrian.ui.Sensores;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.myrssadrian.R;

import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

public class SensoresFragment extends Fragment implements SensorEventListener {

    //Creamos las variables a utilizar
    TextView  temperatura, orientacion, acelerometro;
    ImageButton imageButtonLinterna;
    Button linterna;
    boolean linternaON= true;

    //Constructor
    public static SensoresFragment newInstance() {
        return new SensoresFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sensores_fragment, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Referenciamos las variables
        temperatura = (TextView) getView().findViewById(R.id.tvTemperatura);
        orientacion = (TextView) getView().findViewById(R.id.tvOrientacion);
        acelerometro = (TextView) getView().findViewById(R.id.tvAcelerometro);
        imageButtonLinterna = getView().findViewById(R.id.ibLinterna);

        //metodo para encender y apagar la linterna
        imageButtonLinterna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linternaON){
                    linternaON=false;
                    imageButtonLinterna.setBackgroundResource(R.drawable.ic_linterna_on);

                    CameraManager cameraManager = (CameraManager) getActivity().getSystemService(getContext().CAMERA_SERVICE);
                    try {
                        String cameraID = cameraManager.getCameraIdList()[0];
                        cameraManager.setTorchMode(cameraID,true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }else{
                    linternaON=true;
                    imageButtonLinterna.setBackgroundResource(R.drawable.ic_linterna_off);
                    CameraManager cameraManager = (CameraManager) getActivity().getSystemService(getContext().CAMERA_SERVICE);
                    try {
                        String cameraID = cameraManager.getCameraIdList()[0];
                        cameraManager.setTorchMode(cameraID,false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //Creamos el sensorManager para Listar todos los sensores incluidos en el movil
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);

        List<Sensor> listaSensores = sensorManager.
                getSensorList(Sensor.TYPE_ALL);

        //Obtenemos una lista de todos los sensores
        for (Sensor sensor : listaSensores) {

            Log.i("Salida", sensor.getName());
        }

        /**
         * Sacamos los diferentes sensores que vamos a mostrar
         */
        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (!listaSensores.isEmpty()) {

            Sensor orientationSensor = listaSensores.get(0);
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
        }
        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!listaSensores.isEmpty()) {

            Sensor acelerometerSensor = listaSensores.get(0);
            sensorManager.registerListener(this, acelerometerSensor,SensorManager.SENSOR_DELAY_UI);
        }
        listaSensores = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (!listaSensores.isEmpty()) {

            Sensor magneticSensor = listaSensores.get(0);
            sensorManager.registerListener(this, magneticSensor,SensorManager.SENSOR_DELAY_UI);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_TEMPERATURE);
        if (!listaSensores.isEmpty()) {

            Sensor temperatureSensor = listaSensores.get(0);
            sensorManager.registerListener(this, temperatureSensor,SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * Mostramos el resultado del sensor
     * @param string
     * @param i
     */
    private void mostrarResultado(String string, int i) {

        if(i==1){
            temperatura.setText(string);
        }if(i==2){
            orientacion.setText(string);
        }if(i==3){
            acelerometro.setText(string);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int precision) {}

    @Override
    public void onSensorChanged(SensorEvent evento) {

        //Cada sensor puede provocar que un thread principal pase por aquí así que sincronizamos el acceso
        synchronized (this) {

            switch (evento.sensor.getType()) {

                case Sensor.TYPE_ORIENTATION:
                    for (int i = 0; i < 3; i++) {
                        mostrarResultado("Orientación : " + evento.values[i],2);
                    }
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        mostrarResultado("Acelerómetro : " + evento.values[i],3);
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    for (int i = 0; i < 3; i++) {
                        mostrarResultado("Magnetismo  : " + evento.values[i],1);
                    }
                    break;
            }
        }
    }
}
