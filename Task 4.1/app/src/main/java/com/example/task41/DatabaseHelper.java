package com.example.task41;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TaskManager";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TASKS = "tasks";
    
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DUE_DATE = "due_date";
    private static final String KEY_COMPLETED = "completed";
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT NOT NULL,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DUE_DATE + " TEXT,"
                + KEY_COMPLETED + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // Add a new task
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_DUE_DATE, dateFormat.format(task.getDueDate()));
        values.put(KEY_COMPLETED, task.isCompleted() ? 1 : 0);

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    // Get all tasks sorted by due date
    @SuppressLint("Range")
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " ORDER BY " + KEY_DUE_DATE + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                task.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                task.setCompleted(cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 1);
                
                try {
                    String dateStr = cursor.getString(cursor.getColumnIndex(KEY_DUE_DATE));
                    task.setDueDate(dateFormat.parse(dateStr));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    // Get pending tasks
    @SuppressLint("Range")
    public List<Task> getPendingTasks() {
        List<Task> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + 
                           " WHERE " + KEY_COMPLETED + " = 0" +
                           " ORDER BY " + KEY_DUE_DATE + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                task.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                task.setCompleted(false);
                
                try {
                    String dateStr = cursor.getString(cursor.getColumnIndex(KEY_DUE_DATE));
                    task.setDueDate(dateFormat.parse(dateStr));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    // Get completed tasks
    @SuppressLint("Range")
    public List<Task> getCompletedTasks() {
        List<Task> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + 
                           " WHERE " + KEY_COMPLETED + " = 1" +
                           " ORDER BY " + KEY_DUE_DATE + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                task.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                task.setCompleted(true);
                
                try {
                    String dateStr = cursor.getString(cursor.getColumnIndex(KEY_DUE_DATE));
                    task.setDueDate(dateFormat.parse(dateStr));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    // Update a task
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_DUE_DATE, dateFormat.format(task.getDueDate()));
        values.put(KEY_COMPLETED, task.isCompleted() ? 1 : 0);

        int result = db.update(TABLE_TASKS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
        return result;
    }

    // Delete a task
    public void deleteTask(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    // Get a single task
    @SuppressLint("Range")
    public Task getTask(long taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, KEY_ID + " = ?",
                new String[]{String.valueOf(taskId)}, null, null, null);

        Task task = null;
        if (cursor != null && cursor.moveToFirst()) {
            task = new Task();
            task.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            task.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
            task.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
            task.setCompleted(cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED)) == 1);
            
            try {
                String dateStr = cursor.getString(cursor.getColumnIndex(KEY_DUE_DATE));
                task.setDueDate(dateFormat.parse(dateStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
            cursor.close();
        }
        db.close();
        return task;
    }
} 