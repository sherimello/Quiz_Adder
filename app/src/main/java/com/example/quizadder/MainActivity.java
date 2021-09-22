package com.example.quizadder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizadder.classes.CryptoHandler;
import com.example.quizadder.classes.QuizData;
import com.example.quizadder.recycleradapterclasses.QuestionListRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView text_see_questions, text_question_count;
    private EditText edit_question, edit_option1, edit_option2, edit_option3, edit_option4, edit_quizID, edit_temp;
    private FloatingActionButton fab_add_question;
    private Button button_create_quiz, button_create;
    private CardView card_questions_list, card_quizID;
    private RecyclerView recyclerView;
    private View view_dark_transparent_bg;
    private ScrollView scroll_add_questions;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox_temp;
    private int question_count = 0;
    private ArrayList<QuizData> quizData;
    private ArrayList<String> answers;
    private String chosen_answer = "", SK, IV;

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
        edit_quizID = findViewById(R.id.edit_quizID);

        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);

        recyclerView = findViewById(R.id.recycler);

        scroll_add_questions = findViewById(R.id.scroll_add_questions);

        card_questions_list = findViewById(R.id.card_questions_list);
        card_quizID = findViewById(R.id.card_quizID);

        fab_add_question = findViewById(R.id.fab_add_question);

        button_create_quiz = findViewById(R.id.button_create_quiz);
        button_create = findViewById(R.id.button_create);

        view_dark_transparent_bg = findViewById(R.id.view_dark_transparent_bg);

        quizData = new ArrayList<>();
        answers = new ArrayList<>();

        setListenerToEdittext(edit_option1);
        setListenerToEdittext(edit_option2);
        setListenerToEdittext(edit_option3);
        setListenerToEdittext(edit_option4);

        checkIfCheckBoxIsChecked(checkBox1);
        checkIfCheckBoxIsChecked(checkBox2);
        checkIfCheckBoxIsChecked(checkBox3);
        checkIfCheckBoxIsChecked(checkBox4);

        fab_add_question.setOnClickListener(this);
        text_see_questions.setOnClickListener(this);
        button_create_quiz.setOnClickListener(this);
        button_create.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == button_create) {
            if (checkIfFieldIsEmpty(edit_quizID)) {
                Toast.makeText(getApplicationContext(), "add a valid quiz ID", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadQuiz();
        }

        if (v == button_create_quiz) {
            changeVisibilityOfQuizIDAddingUI(View.VISIBLE);
        }

        if (v == text_see_questions) {
            if (card_questions_list.getVisibility() == View.VISIBLE) {
                return;
            }
            showQuestionsList();
        }

        if (v == fab_add_question) {

            assert fab_add_question != null;
            if (fab_add_question.getRotation() == 45) {
                card_questions_list.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                fab_add_question.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                fab_add_question.animate().rotation(0).setDuration(500).setInterpolator(new OvershootInterpolator());
                card_questions_list.animate().translationX(-getScreenWidth() / 2f + text_question_count.getMeasuredWidth() / 2f + getMarginOftext_see_questions()).translationY(getScreenHeight()).scaleX(0).scaleY(0).alpha(0).setDuration(601).setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        card_questions_list.setVisibility(View.GONE);
                        card_questions_list.setLayerType(View.LAYER_TYPE_NONE, null);
                        fab_add_question.setLayerType(View.LAYER_TYPE_NONE, null);

                    }
                });
                return;
            }

            if (checkIfFieldIsEmpty(edit_question)
                    || checkIfFieldIsEmpty(edit_option1)
                    || checkIfFieldIsEmpty(edit_option2)
                    || checkIfFieldIsEmpty(edit_option3)
                    || checkIfFieldIsEmpty(edit_option4)) {
                Snackbar.make(edit_option1, "field(s) empty!", Snackbar.LENGTH_SHORT).show();
                return;
            }


            if (noAnswerSelected()) {
                Snackbar.make(edit_option1, "no answer chosen...", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (question_count == 0)
                button_create_quiz.setEnabled(true);

            text_see_questions.setVisibility(View.VISIBLE);
            addDataToquizDataArrayList();
            addDataToanswersArrayList(chosen_answer);
            question_count++;

            animateInputLayout();
        }
    }

    private boolean noAnswerSelected() {
        return !checkBox1.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()
                && !checkBox4.isChecked();
    }

    private void onCheckBoxChecked(EditText editText, CheckBox checkBox, int ID) {
        chosen_answer = editText.getText().toString().trim();
        Toast.makeText(getApplicationContext(), chosen_answer, Toast.LENGTH_SHORT).show();

        editText.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rounded_bg_for_edittext));
        editText.setTextColor(AppCompatResources.getColorStateList(getApplicationContext(), R.color.white));
        editText.setCompoundDrawablesWithIntrinsicBounds(ID, 0, 0, 0);
        if (edit_temp != null && edit_temp != editText) {
            edit_temp.setBackground(null);
            checkBox_temp.setChecked(false);
        }

        edit_temp = editText;
        checkBox_temp = checkBox;
    }

    private void checkIfCheckBoxIsChecked(CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (edit_temp != null) {
                if (edit_temp == edit_option1) {
                    edit_temp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.option_1, 0, 0, 0);
                }
                if (edit_temp == edit_option2) {
                    edit_temp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.option_2, 0, 0, 0);
                }
                if (edit_temp == edit_option3) {
                    edit_temp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.option_3, 0, 0, 0);
                }
                if (edit_temp == edit_option4) {
                    edit_temp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.option_4, 0, 0, 0);
                }
            }

            if (checkBox == checkBox1) {
                if (isChecked) {
                    onCheckBoxChecked(edit_option1, checkBox, R.drawable.option_1_white);
                    return;
                }

                edit_option1.setBackground(null);
                edit_option1.setTextColor(AppCompatResources.getColorStateList(getApplicationContext(), R.color.black));
                checkBox_temp = checkBox;

            }
            if (checkBox == checkBox2) {
                if (isChecked) {
                    onCheckBoxChecked(edit_option2, checkBox, R.drawable.option_2_white);
                    return;
                }

                edit_option2.setBackground(null);
                edit_option2.setTextColor(AppCompatResources.getColorStateList(getApplicationContext(), R.color.black));
                checkBox_temp = checkBox;
            }
            if (checkBox == checkBox3) {
                if (isChecked) {
                    onCheckBoxChecked(edit_option3, checkBox, R.drawable.option_3_white);
                    return;
                }
                Toast.makeText(getApplicationContext(), "unchecked", Toast.LENGTH_SHORT).show();

                edit_option3.setBackground(null);
                edit_option3.setTextColor(AppCompatResources.getColorStateList(getApplicationContext(), R.color.black));
                checkBox_temp = checkBox;
            }
            if (checkBox == checkBox4) {
                if (isChecked) {
                    onCheckBoxChecked(edit_option4, checkBox, R.drawable.option_4_white);
                    return;
                }

                edit_option4.setTextColor(AppCompatResources.getColorStateList(getApplicationContext(), R.color.black));
                edit_option4.setBackground(null);
                checkBox_temp = checkBox;
            }
        });


    }

    private void setListenerToEdittext(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editText == edit_option1) {
                    if (s.length() > 0) {
                        checkBox1.setEnabled(true);
                        return;
                    }
                    checkBox1.setEnabled(false);
                }
                if (editText == edit_option2) {
                    if (s.length() > 0) {
                        checkBox2.setEnabled(true);
                        return;
                    }
                    checkBox2.setEnabled(false);
                }
                if (editText == edit_option3) {
                    if (s.length() > 0) {
                        checkBox3.setEnabled(true);
                        return;
                    }
                    checkBox3.setEnabled(false);
                }
                if (editText == edit_option4) {
                    if (s.length() > 0) {
                        checkBox4.setEnabled(true);
                        return;
                    }
                    checkBox4.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void changeVisibilityOfQuizIDAddingUI(int i) {
        card_quizID.setVisibility(i);
        view_dark_transparent_bg.setVisibility(i);
    }

    private void animateFabToRedCross() {
        fab_add_question.animate().rotation(45).setDuration(500).setInterpolator(new OvershootInterpolator());
    }

    private String getTextFromEditText(EditText editText) {
        return editText.getText().toString().trim();
    }

    private void addDataToanswersArrayList(String s) {
        answers.add(s);

    }

    private void addDataToquizDataArrayList() {
        quizData.add(new QuizData(
                getTextFromEditText(edit_question),
                getTextFromEditText(edit_option1),
                getTextFromEditText(edit_option2),
                getTextFromEditText(edit_option3),
                getTextFromEditText(edit_option4)));
    }

    private void showQuestionsList() {
        animateFabToRedCross();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
//        RecyclerView.Adapter adapter = new QuestionListRecyclerAdapter(quizData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new QuestionListRecyclerAdapter(quizData));

        card_questions_list.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        card_questions_list.setTranslationX(-getScreenWidth() / 2f + text_question_count.getMeasuredWidth() / 2f + getMarginOftext_see_questions());
        card_questions_list.setTranslationY(getScreenHeight());
        card_questions_list.setScaleX(0);
        card_questions_list.setScaleY(0);
        card_questions_list.setAlpha(0);
        card_questions_list.setVisibility(View.VISIBLE);
        card_questions_list.animate().translationX(0).translationY(0).scaleY(1).scaleX(1).alpha(1).setDuration(601).setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        card_questions_list.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                });
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

    private QuizData getDataOffOfquizData(int index) {
        return new QuizData(quizData.get(index).getQue(),
                quizData.get(index).get_1(), quizData.get(index).get_2(),
                quizData.get(index).get_3(), quizData.get(index).get_4());
    }

    private void uploadQuiz() {
        ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setCancelable(false);
        pd.setMessage("uploading sQUIZ...");
        pd.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String key = databaseReference.push().getKey();
        databaseReference.child("QuizID").child(Objects.requireNonNull(key)).setValue(getTextFromEditText(edit_quizID)).addOnCompleteListener(task -> {

            Snackbar.make(edit_quizID, "sQUIZ added...", Snackbar.LENGTH_SHORT).show();
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Questions").child(key);
            for (int i = 0; i < quizData.size(); i++) {
                databaseReference1.child(String.valueOf((i + 1))).setValue(getDataOffOfquizData(i));
            }

            DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Encryption Data");
            databaseReference3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    SK = Objects.requireNonNull(snapshot.child("SK").getValue()).toString();
                    IV = Objects.requireNonNull(snapshot.child("IV").getValue()).toString();

                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance()
                            .getReference().child("Answers").child(key);

                    for (int i = 0; i < answers.size(); i++) {
                        try {
                            databaseReference2.child(String.valueOf((i + 1))).setValue(new CryptoHandler(SK, IV).encrypt(answers.get(i)));
                        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | UnsupportedEncodingException | InvalidKeyException | BadPaddingException e) {
                            e.printStackTrace();
                            Snackbar.make(edit_option1, Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    pd.dismiss();
                    quizData.clear();
                    answers.clear();
                    question_count = 0;
                    text_question_count.setText("0");
                    text_see_questions.setVisibility(View.GONE);
                    text_question_count.setVisibility(View.GONE);
                    changeVisibilityOfQuizIDAddingUI(View.GONE);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Snackbar.make(edit_option1, error.getMessage(), Snackbar.LENGTH_SHORT).show();

                }
            });

        }).addOnCanceledListener(() -> Snackbar.make(edit_quizID, "sQUIZ adding failed...", Snackbar.LENGTH_SHORT).show()).addOnFailureListener(e -> Snackbar.make(edit_quizID, Objects.requireNonNull(e.getLocalizedMessage()), Snackbar.LENGTH_SHORT).show());
    }

    private void animateInputLayout() {

        scroll_add_questions.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        text_question_count.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        scroll_add_questions.setPivotX(scroll_add_questions.getMeasuredWidth() / 2f);
        scroll_add_questions.setPivotY(scroll_add_questions.getMeasuredHeight() / 2f);

        scroll_add_questions.animate().translationX(-getScreenWidth() / 2f + text_question_count.getMeasuredWidth() / 2f + getMarginOftext_see_questions()).translationY(getScreenHeight()).scaleX(0).scaleY(0).alpha(0).setDuration(601).setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                resetAllFields();
                text_see_questions.setVisibility(View.VISIBLE);
                text_question_count.setVisibility(View.VISIBLE);
                scroll_add_questions.setLayerType(View.LAYER_TYPE_NONE, null);
                scroll_add_questions.setTranslationX(0);
                scroll_add_questions.setTranslationY(0);
                scroll_add_questions.setScaleX(1);
                scroll_add_questions.setScaleY(1);
                text_question_count.setText(String.valueOf(question_count));
                scroll_add_questions.animate().alpha(1).setDuration(300);
                text_question_count.animate().scaleX(1).scaleY(1).setDuration(300).setInterpolator(new OvershootInterpolator()).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        scroll_add_questions.animate().setListener(null);

                        text_question_count.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                });
            }
        });
        text_question_count.animate().scaleX(0).scaleY(0).setDuration(601);

    }

    private void resetAllFields() {
        checkBox1.setChecked(false);
        checkBox2.setChecked(false);
        checkBox3.setChecked(false);
        checkBox4.setChecked(false);

        edit_question.setText("");
        edit_option1.setText("");
        edit_option2.setText("");
        edit_option3.setText("");
        edit_option4.setText("");
    }

    @Override
    public void onBackPressed() {
        if (view_dark_transparent_bg.getVisibility() == View.VISIBLE) {
            changeVisibilityOfQuizIDAddingUI(View.GONE);
        }
    }
}