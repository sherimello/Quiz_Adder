package com.example.quizadder.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.quizadder.R;


public class Splash extends AppCompatActivity {

    private CardView card1, card2, card3;
    private RelativeLayout relative_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);

        relative_main = findViewById(R.id.relative_main);

        relative_main.setAlpha(0);

        new Handler().postDelayed(this::animateLogo, 500);
    }

    private void animateLogo() {
        card1.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        card2.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        card3.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        relative_main.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        relative_main.animate().alpha(1).setDuration(701).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                card1.animate().translationX(card1.getMeasuredWidth() + getResources().getDimensionPixelSize(R.dimen._margin_for_splash)).setDuration(500).setInterpolator(new OvershootInterpolator());
                card2.animate().translationX(-(card2.getMeasuredWidth() + getResources().getDimensionPixelSize(R.dimen._margin_for_splash))).setDuration(500).setInterpolator(new OvershootInterpolator()).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        new Handler().postDelayed(Splash.this::reverseAnimation, 601);
                    }
                });
                card3.animate().rotation(45).setDuration(500).setStartDelay(100).setInterpolator(new OvershootInterpolator());

            }
        });
    }

    private void reverseAnimation() {
        card1.animate().translationX(0).setInterpolator(new DecelerateInterpolator());
        card2.animate().translationX(0).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                card1.setLayerType(View.LAYER_TYPE_NONE, null);
                card2.setLayerType(View.LAYER_TYPE_NONE, null);
                card3.setLayerType(View.LAYER_TYPE_NONE, null);

                changeActivity();
            }
        });
        card3.animate().rotation(0).setDuration(500).setInterpolator(new OvershootInterpolator());
    }

    private void changeActivity() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), QuizList.class));
            finish();
        }, 700);
    }
}