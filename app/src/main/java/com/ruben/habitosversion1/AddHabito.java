package com.ruben.habitosversion1;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ruben.habitosversion1.room.DatabaseClient;

import java.sql.Time;
import java.util.Calendar;
import java.util.TimeZone;

public class AddHabito  extends AppCompatActivity {
    private EditText editTextName, editTextType, editTextLevel;
    private Button buttonSave;

    private static final int REQUEST_CODE = 1; // Código de solicitud para permisos
    private Button createNotificationButton; // Botón para crear la notificación
    private static final String TAG = "Notify";

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

        // Inicializa el botón y establece su listener de clic
        createNotificationButton = findViewById(R.id.create_notification_button);
        createNotificationButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 y superior
                if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"}, REQUEST_CODE);
                } else {
                    createNotificationChannel(); // Crea el canal de notificaciones
                    scheduleDailyNotification(); // Programa una notificación diaria
                }
            } else {
                createNotificationChannel(); // Crea el canal de notificaciones
                scheduleDailyNotification(); // Programa una notificación diaria
            }
        });

    }

    // Método para crear el canal de notificaciones necesario para Android 8.0 y superior
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Solo para Android 8.0 y superior
            CharSequence name = "DailyReminderChannel";
            String description = "Channel for Daily Reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("daily_reminder_channel",
                    name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Canal de notificaciones creado");
        }
    }

    // Método para programar una notificación diaria a una hora específica
    private void scheduleDailyNotification() {

        TimePicker timePicker = findViewById(R.id.timePicker);

        int hora = timePicker.getHour();
        int minuto = timePicker.getMinute();

        // Obtiene una instancia del calendario
        Calendar calendar = Calendar.getInstance();

        // Establece la zona horaria predeterminada del dispositivo
        calendar.setTimeZone(TimeZone.getDefault());

        // Establece la hora actual
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Configura la hora de la notificación (9:00:00 en este ejemplo)
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
//        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.SECOND, 0);

        // Si la hora configurada ya pasó, programa para el siguiente día
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        String name = editTextName.getText().toString();
        String type = editTextType.getText().toString();
        String level = editTextLevel.getText().toString();

        // Crea un intent para el receptor de la notificación
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("editTextName",name);
        intent.putExtra("editTextType", type);
        intent.putExtra("editTextLevel", level);


        // Agrega el flag FLAG_IMMUTABLE al PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Programa la notificación diaria utilizando AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.d(TAG, "Notificación diaria programada a las " + calendar.getTime());
    }

    // Manejo de los resultados de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createNotificationChannel(); // Crea el canal de notificaciones
                scheduleDailyNotification(); // Programa una notificación diaria
            }
        }
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
