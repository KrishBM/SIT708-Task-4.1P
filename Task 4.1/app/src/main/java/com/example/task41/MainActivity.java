package com.example.task41;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.example.task41.TaskAdapter;
import com.example.task41.DatabaseHelper;
import com.example.task41.Task;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TaskAdapter.OnTaskListener {
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper databaseHelper;
    private TaskDatabase taskDatabase;
    private TaskDao taskDao;
    private TaskViewModel taskViewModel;
    private int currentViewType = VIEW_TYPE_ALL; // Track current view type
    
    // Constants for view types
    private static final int VIEW_TYPE_ALL = 0;
    private static final int VIEW_TYPE_PENDING = 1;
    private static final int VIEW_TYPE_COMPLETED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Room database
        taskDatabase = TaskDatabase.getInstance(this);
        taskDao = taskDatabase.taskDao();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this);
        recyclerView.setAdapter(taskAdapter);

        // Setup ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Setup Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        // Initial load of tasks
        refreshTasks();
    }

    private void refreshTasks() {
        // Remove existing observers
        if (taskViewModel.getAllTasks().hasObservers()) {
            taskViewModel.getAllTasks().removeObservers(this);
        }
        if (taskViewModel.getPendingTasks().hasObservers()) {
            taskViewModel.getPendingTasks().removeObservers(this);
        }
        if (taskViewModel.getCompletedTasks().hasObservers()) {
            taskViewModel.getCompletedTasks().removeObservers(this);
        }

        // Observe based on current view type
        switch (currentViewType) {
            case VIEW_TYPE_PENDING:
                taskViewModel.getPendingTasks().observe(this, tasks -> {
                    taskAdapter.setTasks(tasks);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("Pending Tasks");
                    }
                });
                break;
            case VIEW_TYPE_COMPLETED:
                taskViewModel.getCompletedTasks().observe(this, tasks -> {
                    taskAdapter.setTasks(tasks);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("Completed Tasks");
                    }
                });
                break;
            default: // VIEW_TYPE_ALL
                taskViewModel.getAllTasks().observe(this, tasks -> {
                    taskAdapter.setTasks(tasks);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("All Tasks");
                    }
                });
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_all_tasks) {
            currentViewType = VIEW_TYPE_ALL;
        } else if (id == R.id.menu_pending_tasks) {
            currentViewType = VIEW_TYPE_PENDING;
        } else if (id == R.id.menu_completed_tasks) {
            currentViewType = VIEW_TYPE_COMPLETED;
        } else if (id == R.id.menu_settings) {
            // Implement settings
            // startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.menu_about) {
            // Implement about
            // startActivity(new Intent(this, AboutActivity.class));
        }

        refreshTasks();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDeleteClick(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    taskViewModel.delete(task);
                    refreshTasks(); // Refresh after deletion
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(this, AddEditTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
    }

    @Override
    public void onTaskCompleted(Task task, boolean isCompleted) {
        task.setCompleted(isCompleted);
        taskViewModel.update(task);
        refreshTasks(); // Refresh after task completion status change
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTasks(); // Refresh when returning to activity
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}