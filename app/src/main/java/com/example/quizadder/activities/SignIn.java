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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.quizadder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private CardView card1, card2, card3;
    private Button button_signIn;
    private GoogleSignInClient mGoogleSignInClient;
    private String token, default_web_client_ID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);

        button_signIn = findViewById(R.id.button_signIn);

        button_signIn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        animateCards();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Integer.parseInt(token)) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                clearRotationAnimation();
                Snackbar.make(card1, "activity result error: " + e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
            }
        }
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

                getToken();
            }
        });
    }

    private void getToken() {
        Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.continuous_rotation);
        rotation.setInterpolator(new LinearInterpolator());
        card3.startAnimation(rotation);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Sign In Request Token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                token = Objects.requireNonNull(snapshot.getValue()).toString();
                Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                init_GSO();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Snackbar.make(card1, "Token retrieving error: " + error.getMessage(), Snackbar.LENGTH_SHORT).show();

            }
        });
    }

    private void init_GSO() {

        // Configure Google Sign In
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Default Web Client ID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                default_web_client_ID = Objects.requireNonNull(snapshot.getValue()).toString();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(default_web_client_ID)
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(SignIn.this, gso);

                init_Gmail_Prompt();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Snackbar.make(card1, error.getMessage(), Snackbar.LENGTH_SHORT).show();
                clearRotationAnimation();

            }
        });

    }

    private void init_Gmail_Prompt() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Integer.parseInt(token));
//        launchSomeActivity.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Toast.makeText(getApplicationContext(), idToken, Toast.LENGTH_SHORT).show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser()) {
                                if (user != null) {
                                    //You can her set data user in Fire store
                                    //Ex: Go to RegisterPage()
                                }

                            } else {

                                //Ex: Go to HomePage()
                            }
                            // Sign in success, update UI with the signed-in user's information
                            changeActivity();

                        } else {
                            Snackbar.make(card1, task.getResult().describeContents(), Snackbar.LENGTH_SHORT).show();
                            clearRotationAnimation();
                            // If sign in fails, display a message to the user.
                        }
                    }
                });
    }

    private void clearRotationAnimation() {
        card3.clearAnimation();
        card3.setRotation(0);
    }

    private void changeActivity() {

        new Handler().postDelayed(() -> {
            card1.setLayerType(View.LAYER_TYPE_NONE, null);
            card2.setLayerType(View.LAYER_TYPE_NONE, null);
            card3.setLayerType(View.LAYER_TYPE_NONE, null);
            startActivity(new Intent(getApplicationContext(), QuizList.class));
            finish();
        }, 3500);

    }
}