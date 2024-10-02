package com.ruben.habitosversion1.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ruben.habitosversion1.Lista;

@Database(entities = {Lista.class}, version = 1)
public abstract class HabitosDatabase extends RoomDatabase {
    public abstract HabitosDao habitosDao();
}
