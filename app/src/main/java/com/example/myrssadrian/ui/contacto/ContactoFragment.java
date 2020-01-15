package com.example.myrssadrian.ui.contacto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myrssadrian.R;

public class ContactoFragment extends Fragment {

    private ContactoViewModel contactoViewModel;

    private Button btnContactoEnviar;
    private EditText etContactoUsuario, etContactoEmail;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contactoViewModel =
                ViewModelProviders.of(this).get(ContactoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_contacto, container, false);

        btnContactoEnviar = root.findViewById(R.id.btnContactoEnviar);
        etContactoEmail = root.findViewById(R.id.etContactoEmail);
        etContactoUsuario = root.findViewById(R.id.etContactoUsuario);

        btnContactoEnviar.setOnClickListener(listenerEnviar);
        return root;
    }


    private View.OnClickListener listenerEnviar = new View.OnClickListener() {

    @Override
    public void onClick(View v) {
        String[] TO = {"adrianscc11@gmail.com"}; //aquí pongo mi correo
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        // Esto es el asunto y el cuerpo del mensaje
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Calculadora");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Usuario: "+etContactoUsuario.getText().toString()+" con el correo electronico: "+etContactoEmail.getText().toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),
                    "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
        }
    }

};
}