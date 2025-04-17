package com.example.task41;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private final OnTaskListener listener;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public interface OnTaskListener {
        void onTaskClick(Task task);
        void onTaskCompleted(Task task, boolean isCompleted);
        void onDeleteClick(Task task);
    }

    public TaskAdapter(OnTaskListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.bind(currentTask);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final TextView textViewDueDate;
        private final CheckBox checkBoxCompleted;
        private final ImageButton buttonDelete;
        private final OnTaskListener listener;
        private Task currentTask;

        TaskViewHolder(View itemView, OnTaskListener listener) {
            super(itemView);
            this.listener = listener;
            textViewTitle = itemView.findViewById(R.id.task_title);
            textViewDescription = itemView.findViewById(R.id.task_description);
            textViewDueDate = itemView.findViewById(R.id.task_due_date);
            checkBoxCompleted = itemView.findViewById(R.id.checkbox_completed);
            buttonDelete = itemView.findViewById(R.id.button_delete);

            itemView.setOnClickListener(v -> {
                if (listener != null && currentTask != null) {
                    listener.onTaskClick(currentTask);
                }
            });

            buttonDelete.setOnClickListener(v -> {
                if (listener != null && currentTask != null) {
                    listener.onDeleteClick(currentTask);
                }
            });

            checkBoxCompleted.setOnClickListener(v -> {
                if (listener != null && currentTask != null) {
                    listener.onTaskCompleted(currentTask, checkBoxCompleted.isChecked());
                }
            });
        }

        void bind(Task task) {
            this.currentTask = task;
            textViewTitle.setText(task.getTitle());
            textViewDescription.setText(task.getDescription());
            textViewDueDate.setText(DATE_FORMAT.format(task.getDueDate()));
            
            checkBoxCompleted.setOnCheckedChangeListener(null);
            checkBoxCompleted.setChecked(task.isCompleted());
        }
    }
} 