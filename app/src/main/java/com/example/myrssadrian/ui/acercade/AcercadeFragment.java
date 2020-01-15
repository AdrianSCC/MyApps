package com.example.myrssadrian.ui.acercade;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myrssadrian.R;

public class AcercadeFragment extends Fragment {

    private AcercadeViewModel acercadeViewModel;
    private ImageButton ibtnAcercaDeTwitter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        acercadeViewModel =
                ViewModelProviders.of(this).get(AcercadeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_acercade, container, false);

        ibtnAcercaDeTwitter = root.findViewById(R.id.ibtnAcercaDeTwitter);

        ibtnAcercaDeTwitter.setOnClickListener(listenerTwitter);

        return root;
    }

    //creamos el listener del boton que nos envia al twitter
    private View.OnClickListener listenerTwitter = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Uri uri = Uri.parse("https://twitter.com/CruzadoCerro");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    };

}