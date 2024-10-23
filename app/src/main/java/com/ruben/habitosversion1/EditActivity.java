package com.ruben.habitosversion1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ruben.habitosversion1.room.DatabaseClient;
import com.ruben.habitosversion1.room.HabitosDao;

public class EditActivity extends AppCompatActivity {

    private EditText editTextName, editTextDescription, editTextPrecio;
    private Button buttonSave;
    private HabitosDao habitosDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextName = findViewById(R.id.etNombre);
        editTextDescription = findViewById(R.id.etDescripcion);
        editTextPrecio = findViewById(R.id.etPrecio);
        buttonSave = findViewById(R.id.buttonSave);

        Intent intent = getIntent();
        int itemId = intent.getIntExtra("item_id", -1);

        if (itemId != -1) {
            loadItemData(itemId);
        }

        buttonSave.setOnClickListener(v -> {
            saveItemChanges(itemId);
        });
    }

    private void loadItemData(int itemId) {
        // Cargar los datos del ítem desde la base de datos
        Lista item = DatabaseClient.getInstance(getApplicationContext()).getHabitosDatabase().habitosDao().getItemById(itemId);

        if (item != null) {
            editTextName.setText(item.getNombre());
            editTextDescription.setText(item.getDescripcion());
            editTextPrecio.setText((item.getPrecio()));
        }
    }

    private void saveItemChanges(int itemId) {
        // Guardar los cambios en la base de datos
        String updatedName = editTextName.getText().toString();
        String updatedDescription = editTextDescription.getText().toString();
        String updatePrecio = editTextPrecio.getText().toString();

        // Cargar el ítem y actualizar sus valores
        Lista item = DatabaseClient.getInstance(getApplicationContext()).getHabitosDatabase().habitosDao().getItemById(itemId);
        if (item != null) {
            item.setNombre(updatedName);
            item.setDescripcion(updatedDescription);
            item.setPrecio(updatePrecio);

            // Actualizar el ítem en la base de datos
            //habitosDao.update(item);
            DatabaseClient.getInstance(getApplicationContext()).getHabitosDatabase().habitosDao().update(item);

            // Volver a la vista principal después de guardar
            startActivity(new Intent(this, MainActivity.class));
            finish();  // Terminar la actividad actual
        }
    }





}