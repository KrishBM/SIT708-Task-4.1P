package com.example.task41;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository repository;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Task>> pendingTasks;
    private LiveData<List<Task>> completedTasks;
    private ExecutorService executorService;

    public TaskViewModel(Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
        pendingTasks = repository.getPendingTasks();
        completedTasks = repository.getCompletedTasks();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Task task) {
        repository.insert(task);
    }

    public void update(Task task) {
        repository.update(task);
    }

    public void delete(Task task) {
        repository.delete(task);
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

    // Use LiveData version for observing task changes
    public LiveData<Task> getTaskById(long id) {
        return repository.getTaskByIdLiveData(id);
    }

    // For one-time task retrieval
    public void loadTaskById(long id, TaskLoadCallback callback) {
        executorService.execute(() -> {
            Task task = repository.getTaskById(id);
            callback.onTaskLoaded(task);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

    // Callback interface for task loading
    public interface TaskLoadCallback {
        void onTaskLoaded(Task task);
    }
} 