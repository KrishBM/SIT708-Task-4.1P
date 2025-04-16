package com.example.task41;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TaskAdapter.OnTaskClickListener {
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper databaseHelper;
    private TaskDatabase taskDatabase;
    private TaskDao taskDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Room database
        taskDatabase = TaskDatabase.getInstance(this);
        taskDao = taskDatabase.taskDao();

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(taskAdapter);

        // Observe tasks
        loadTasks();

        // Setup Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                startActivity(intent);
            }
        });

        // Setup Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_all_tasks) {
            loadTasks();
        } else if (id == R.id.menu_pending_tasks) {
            loadPendingTasks();
        } else if (id == R.id.menu_completed_tasks) {
            loadCompletedTasks();
        } else if (id == R.id.menu_settings) {
            openSettings();
        } else if (id == R.id.menu_about) {
            openAbout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadPendingTasks() {
        taskDao.getPendingTasks().observe(this, tasks -> {
            taskAdapter.setTasks(tasks);
        });
    }

    private void loadCompletedTasks() {
        taskDao.getCompletedTasks().observe(this, tasks -> {
            taskAdapter.setTasks(tasks);
        });
    }

    private void openSettings() {
        // TODO: Implement settings screen
    }

    private void openAbout() {
        // TODO: Implement about screen
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(this, AddEditTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
    }

    private void loadTasks() {
        taskDao.getAllTasks().observe(this, tasks -> {
            taskAdapter.setTasks(tasks);
        });
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