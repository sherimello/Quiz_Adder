package com.example.quizadder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text_see_questions, text_question_count;
    private FloatingActionButton fab_add_question;
    private Button button_create_quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_see_questions = findViewById(R.id.text_see_questions);
        text_question_count = findViewById(R.id.text_question_count);

        fab_add_question = findViewById(R.id.fab_add_question);

        button_create_quiz = findViewById(R.id.button_create_quiz);

        fab_add_question.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == fab_add_question) {
            button_create_quiz.setEnabled(true);
            text_question_count.setVisibility(View.VISIBLE);
            text_see_questions.setVisibility(View.VISIBLE);
        }
    }
}