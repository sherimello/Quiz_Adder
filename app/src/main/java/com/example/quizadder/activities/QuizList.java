package com.example.quizadder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizadder.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class QuizList extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab_add_a_sQUIZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        fab_add_a_sQUIZ = findViewById(R.id.fab_add_a_sQUIZ);

        fab_add_a_sQUIZ.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == fab_add_a_sQUIZ) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}