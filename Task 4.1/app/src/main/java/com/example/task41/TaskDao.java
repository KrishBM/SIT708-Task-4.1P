package com.example.task41;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC")
    LiveData<List<Task>> getPendingTasks();

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY dueDate ASC")
    LiveData<List<Task>> getCompletedTasks();

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    Task getTaskById(long taskId);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    LiveData<Task> getTaskByIdLiveData(long taskId);
} 