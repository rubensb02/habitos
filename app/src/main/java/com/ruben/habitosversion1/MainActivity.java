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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    private static final int REQUEST_CODE = 1; // Código de solicitud para permisos
    private Button createNotificationButton; // Botón para crear la notificación
    private static final String TAG = "Notify";

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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(listaAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        add_habito = findViewById(R.id.addHabito);
        add_habito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                add_habito();
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
        // Obtiene una instancia del calendario
        Calendar calendar = Calendar.getInstance();
        // Establece la zona horaria predeterminada del dispositivo
        calendar.setTimeZone(TimeZone.getDefault());
        // Establece la hora actual
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Configura la hora de la notificación (9:00:00 en este ejemplo)
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Si la hora configurada ya pasó, programa para el siguiente día
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Crea un intent para el receptor de la notificación
        Intent intent = new Intent(this, NotificationReceiver.class);

        // Agrega el flag FLAG_IMMUTABLE al PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Programa la notificación diaria utilizando AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
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