package com.ruben.habitosversion1.room;
import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;


public class DatabaseClient {
    private Context context;
    private static DatabaseClient instance;
    private HabitosDatabase habitosDatabase;

    private DatabaseClient(Context context) {
        this.context = context;
        habitosDatabase = Room.databaseBuilder(context, HabitosDatabase.class, "HabitosDB")
                .allowMainThreadQueries()
                .build();
    }
    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public HabitosDatabase getHabitosDatabase() {
        return habitosDatabase;
    }
}
