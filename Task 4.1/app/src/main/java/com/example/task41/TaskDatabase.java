package com.example.task41;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Task.class}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class TaskDatabase extends RoomDatabase {
    private static TaskDatabase instance;
    public abstract TaskDao taskDao();

    public static synchronized TaskDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    TaskDatabase.class,
                    "task_database"
            ).fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
} 