package com.example.task41;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Task>> pendingTasks;
    private LiveData<List<Task>> completedTasks;
    private ExecutorService executorService;

    public TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
        pendingTasks = taskDao.getPendingTasks();
        completedTasks = taskDao.getCompletedTasks();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Task task) {
        executorService.execute(() -> {
            taskDao.insert(task);
        });
    }

    public void update(Task task) {
        executorService.execute(() -> {
            taskDao.update(task);
        });
    }

    public void delete(Task task) {
        executorService.execute(() -> {
            taskDao.delete(task);
        });
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> getPendingTasks() {
        return pendingTasks;
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return completedTasks;
    }

    // Synchronous method - should be called from background thread
    public Task getTaskById(long id) {
        return taskDao.getTaskById(id);
    }

    // Asynchronous method with LiveData
    public LiveData<Task> getTaskByIdLiveData(long id) {
        return taskDao.getTaskByIdLiveData(id);
    }
} 