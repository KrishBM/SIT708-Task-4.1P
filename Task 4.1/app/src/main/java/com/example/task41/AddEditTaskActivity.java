package com.example.task41;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditTaskActivity extends AppCompatActivity {
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextDueDate;
    private TaskViewModel taskViewModel;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private long taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }

        // Initialize views
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextDueDate = findViewById(R.id.edit_text_due_date);

        // Initialize calendar and date format
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        // Setup date picker
        editTextDueDate.setOnClickListener(v -> showDatePicker());

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Check if editing existing task
        if (getIntent().hasExtra("task_id")) {
            taskId = getIntent().getLongExtra("task_id", -1);
            setTitle("Edit Task");
            loadTask();
        } else {
            setTitle("Add Task");
        }
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            editTextDueDate.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
           calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadTask() {
        if (taskId != -1) {
            taskViewModel.getTaskById(taskId).observe(this, task -> {
                if (task != null) {
                    editTextTitle.setText(task.getTitle());
                    editTextDescription.setText(task.getDescription());
                    calendar.setTime(task.getDueDate());
                    editTextDueDate.setText(dateFormat.format(task.getDueDate()));
                }
            });
        }
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String dueDateStr = editTextDueDate.getText().toString().trim();

        if (title.isEmpty()) {
            editTextTitle.setError("Title is required");
            return;
        }

        if (dueDateStr.isEmpty()) {
            editTextDueDate.setError("Due date is required");
            return;
        }

        Task task = new Task(title, description, calendar.getTime());
        if (taskId != -1) {
            task.setId(taskId);
            taskViewModel.update(task);
        } else {
            taskViewModel.insert(task);
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.save_task) {
            saveTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 