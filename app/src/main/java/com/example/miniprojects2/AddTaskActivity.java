package com.example.miniprojects2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTaskActivity extends AppCompatActivity {

    private EditText taskNameEditText, taskDescriptionEditText;
    private Button saveTaskButton;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TaskPrefs";
    private static final String KEY_NAME = "task_name";
    private static final String KEY_DESCRIPTION = "task_description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskNameEditText = findViewById(R.id.taskNameEditText);
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText);
        saveTaskButton = findViewById(R.id.saveTaskButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        saveTaskButton.setOnClickListener(v -> saveTask());

        loadTaskDetails();
    }

    private void saveTask() {
        String name = taskNameEditText.getText().toString().trim();
        String description = taskDescriptionEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description)) {
            String id = databaseReference.push().getKey();
            Task newTask = new Task(id, name, description);
            databaseReference.child(id).setValue(newTask).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddTaskActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                    saveTaskDetails(name, description);
                    finish();
                } else {
                    Toast.makeText(AddTaskActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please enter both name and description", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTaskDetails(String name, String description) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_DESCRIPTION, description);
        editor.apply();
    }

    private void loadTaskDetails() {
        String name = sharedPreferences.getString(KEY_NAME, "");
        String description = sharedPreferences.getString(KEY_DESCRIPTION, "");

        taskNameEditText.setText(name);
        taskDescriptionEditText.setText(description);
    }
}
