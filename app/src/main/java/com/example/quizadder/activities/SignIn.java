package com.example.quizadder.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.quizadder.R;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private CardView card1, card2, card3;
    private Button button_signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);

        button_signIn = findViewById(R.id.button_signIn);

        button_signIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        animateCards();
    }

    private void animateCards() {

        card1.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        card2.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        card3.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        card1.animate().setDuration(701).translationX(-(card1.getMeasuredWidth() + getResources().getDimension(R.dimen._margin_for_splash))).setInterpolator(new OvershootInterpolator());
        card2.animate().setDuration(701).translationX(card1.getMeasuredWidth() + getResources().getDimension(R.dimen._margin_for_splash)).setInterpolator(new OvershootInterpolator()).setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                card1.setLayerType(View.LAYER_TYPE_NONE, null);
                card2.setLayerType(View.LAYER_TYPE_NONE, null);
                card3.setLayerType(View.LAYER_TYPE_NONE, null);

                changeActivity();
            }
        });
    }

    private void changeActivity() {
        Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.continuous_rotation);
        rotation.setInterpolator(new LinearInterpolator());
        card3.startAnimation(rotation);
        new Handler().postDelayed(() -> {
            card1.setLayerType(View.LAYER_TYPE_NONE, null);
            card2.setLayerType(View.LAYER_TYPE_NONE, null);
            card3.setLayerType(View.LAYER_TYPE_NONE, null);
            startActivity(new Intent(getApplicationContext(), QuizList.class));
            finish();
        }, 3500);

    }
}