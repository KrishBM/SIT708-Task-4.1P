package com.example.task41;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private Context context;
    private List<Task> taskList;
    private OnTaskClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        
        // If the context implements OnTaskClickListener, set it as the listener
        if (context instanceof OnTaskClickListener) {
            this.listener = (OnTaskClickListener) context;
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        
        holder.textViewTitle.setText(task.getTitle());
        holder.textViewDescription.setText(task.getDescription());
        holder.textViewDueDate.setText(dateFormat.format(task.getDueDate()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewDueDate;

        TaskViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.task_title);
            textViewDescription = view.findViewById(R.id.task_description);
            textViewDueDate = view.findViewById(R.id.task_due_date);
        }
    }
} 