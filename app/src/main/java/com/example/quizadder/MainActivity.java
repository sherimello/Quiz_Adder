package com.example.quizadder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.quizadder.classes.QuizData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text_see_questions, text_question_count;
    private EditText edit_question, edit_option1, edit_option2, edit_option3, edit_option4;
    private FloatingActionButton fab_add_question;
    private Button button_create_quiz;
    private ConstraintLayout constraintLayout_2;
    private int question_count = 0;
    private ArrayList<QuizData> quizData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_see_questions = findViewById(R.id.text_see_questions);
        text_question_count = findViewById(R.id.text_question_count);

        edit_question = findViewById(R.id.edit_question);
        edit_option1 = findViewById(R.id.edit_option1);
        edit_option2 = findViewById(R.id.edit_option2);
        edit_option3 = findViewById(R.id.edit_option3);
        edit_option4 = findViewById(R.id.edit_option4);

        fab_add_question = findViewById(R.id.fab_add_question);

        button_create_quiz = findViewById(R.id.button_create_quiz);

        constraintLayout_2 = findViewById(R.id.constraintLayout_2);

        quizData = new ArrayList<>();

        fab_add_question.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == fab_add_question) {
            if (checkIfFieldIsEmpty(edit_question)
                    || checkIfFieldIsEmpty(edit_option1)
                    || checkIfFieldIsEmpty(edit_option2)
                    || checkIfFieldIsEmpty(edit_option3)
                    || checkIfFieldIsEmpty(edit_option4)) {
                Snackbar.make(edit_option1, "field(s) empty!", Snackbar.LENGTH_SHORT).show();
                return;
            }

            button_create_quiz.setEnabled(true);

            question_count++;

            animateInputLayout();
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getMarginOftext_see_questions() {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) text_see_questions.getLayoutParams();
        return lp.leftMargin;
    }

    public boolean checkIfFieldIsEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private void animateInputLayout() {

        constraintLayout_2.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        text_question_count.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        constraintLayout_2.setPivotX(constraintLayout_2.getMeasuredWidth() / 2f);
        constraintLayout_2.setPivotY(constraintLayout_2.getMeasuredHeight() / 2f);

        constraintLayout_2.animate().translationX(-getScreenWidth() / 2f + text_question_count.getMeasuredWidth() / 2f + getMarginOftext_see_questions()).translationY(getScreenHeight()).scaleX(0).scaleY(0).alpha(0).setDuration(601).setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                text_see_questions.setVisibility(View.VISIBLE);
                text_question_count.setVisibility(View.VISIBLE);
                constraintLayout_2.setLayerType(View.LAYER_TYPE_NONE, null);
                constraintLayout_2.setTranslationX(0);
                constraintLayout_2.setTranslationY(0);
                constraintLayout_2.setScaleX(1);
                constraintLayout_2.setScaleY(1);
                text_question_count.setText(String.valueOf(question_count));
                constraintLayout_2.animate().alpha(1).setDuration(300);
                text_question_count.animate().scaleX(1).scaleY(1).setDuration(300).setInterpolator(new OvershootInterpolator()).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        text_question_count.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                });
            }
        });
        text_question_count.animate().scaleX(0).scaleY(0).setDuration(601);

    }
}