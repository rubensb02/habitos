package com.ruben.habitosversion1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ruben.habitosversion1.room.DatabaseClient;

public class AddHabito  extends AppCompatActivity {
    private EditText editTextName, editTextType, editTextLevel;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_habito);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a las vistas
        editTextName = findViewById(R.id.etNombre);
        editTextType = findViewById(R.id.etDescripcion);
        editTextLevel = findViewById(R.id.etPrecio);
        buttonSave = findViewById(R.id.buttonSave);

        // Acción del botón para guardar el habito
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHabito();
            }
        });

    }

    private void saveHabito() {
        String name = editTextName.getText().toString();
        String type = editTextType.getText().toString();
        String level = editTextLevel.getText().toString();

        // Validar que los campos no estén vacíos
        if (name.isEmpty() || type.isEmpty() || level.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto habito
        Lista habito = new Lista(name, type, level);

        // Insertar habito en la base de datos usando Room
        DatabaseClient.getInstance(getApplicationContext()).getHabitosDatabase().habitosDao().insert(habito);
        // Mostrar mensaje de éxito
        Toast.makeText(this, "habito añadido", Toast.LENGTH_SHORT).show();

        // Finalizar la actividad y volver a la actividad principal
        startActivity(new Intent(this, MainActivity.class));
    }
}
