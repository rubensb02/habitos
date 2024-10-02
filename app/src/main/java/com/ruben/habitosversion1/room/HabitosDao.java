package com.ruben.habitosversion1.room;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ruben.habitosversion1.Lista;

import java.util.List;

@Dao
public interface HabitosDao {

    @Insert
    void insert(Lista lista);

    @Delete
    void delete(Lista lista);

    @Query("SELECT * FROM Lista")
    List<Lista> getAllLista();


}
