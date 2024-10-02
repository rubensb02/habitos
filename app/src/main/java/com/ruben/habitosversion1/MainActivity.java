package com.ruben.habitosversion1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ruben.habitosversion1.room.DatabaseClient;
import com.ruben.habitosversion1.room.HabitosDao;

import java.util.ArrayList;

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

        recyclerView = findViewById(R.id.recyclerViewHabitos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadHabitos();

        //ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(ListaAdapter));
        //itemTouchHelper.attachToRecyclerView(recyclerView);

        add_habito = findViewById(R.id.add_habito);
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