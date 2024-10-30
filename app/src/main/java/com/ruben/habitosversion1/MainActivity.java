package com.ruben.habitosversion1;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ruben.habitosversion1.room.DatabaseClient;
import com.ruben.habitosversion1.room.HabitosDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private ListaAdapter listaAdapter;
    private ArrayList<Lista> listaList;
    private RecyclerView recyclerView;
    private Button add_habito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_main);

        // Configurar el Switch y su estado inicial
        Switch themeSwitch = findViewById(R.id.switch_theme);
        themeSwitch.setChecked(isDarkMode);

        // Cambiar tema cuando el Switch es activado o desactivado
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Guardar la preferencia de tema
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isDarkMode", isChecked);
            editor.apply();
        });


        recyclerView = findViewById(R.id.recyclerViewHabitos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadHabitos();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(listaAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Configura el SwipeToEditCallback
        ItemTouchHelper swipeToEditCallback = new ItemTouchHelper(new SwipeToEditCallback(listaAdapter, this));
        swipeToEditCallback.attachToRecyclerView(recyclerView);

        add_habito = findViewById(R.id.addHabito);
        add_habito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                add_habito();
            }
        });
    }

    private void loadHabitos() {
        listaList = (ArrayList<Lista>) DatabaseClient.getInstance(getApplicationContext()).getHabitosDatabase().habitosDao().getAllLista();
        HabitosDao habitosDao = DatabaseClient.getInstance(getApplicationContext()).getHabitosDatabase().habitosDao();
        listaAdapter = new ListaAdapter(listaList, habitosDao, getApplicationContext());
        recyclerView.setAdapter(listaAdapter);
    }
    private void add_habito() {
        Intent intentAddHabito = new Intent(this, AddHabito.class);
        startActivity(intentAddHabito);
    }

}