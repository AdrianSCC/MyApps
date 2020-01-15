package com.example.myrssadrian;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myrssadrian.ui.noticias.Contenido;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    public Menu menuMain;
    private FragmentManager fragmentMan;


    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_noticias, R.id.nav_juegos,R.id.nav_fotos,R.id.nav_musica,R.id.nav_video,R.id.nav_sensores, R.id.nav_contacto, R.id.nav_acercade)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menuMain = menu;
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_compartir:

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Contenido.link());
                startActivity(Intent.createChooser(intent, "Abrir con"));

                return true;
            case R.id.action_volver:

                onBackPressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //controlamos la accion de volver atras
    @Override
    public void onBackPressed() {
        try {
            if (fragmentMan!=null){
                if (fragmentMan.getBackStackEntryCount()>0){
                    fragmentMan.popBackStackImmediate();
                }else{
                    super.onBackPressed();
                }
            }else {
                super.onBackPressed();
            }
        }catch (Exception ex){
            super.onBackPressed();
        }

    }

    public FragmentManager getFragmentMan() {
        return fragmentMan;
    }

    public void setFragmentMan(FragmentManager fragmentMan) {
        this.fragmentMan = fragmentMan;
    }
}
