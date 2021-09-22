package com.example.quizadder.recycleradapterclasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizadder.R;
import com.example.quizadder.classes.QuizData;

import java.util.ArrayList;

public class QuestionListRecyclerAdapter extends RecyclerView.Adapter<QuestionListRecyclerAdapter.QuestionListRecyclerAdapterViewHolder> {

    private final ArrayList<QuizData> quizData;

    public QuestionListRecyclerAdapter(ArrayList<QuizData> quizData) {
        this.quizData = quizData;
    }

    @NonNull
    @Override
    public QuestionListRecyclerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuestionListRecyclerAdapterViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_quiz_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionListRecyclerAdapterViewHolder holder, int position) {

        QuizData currentItem = quizData.get(position);

        holder.text_question_count.setText(String.valueOf(position + 1));
        holder.text_question.setText(currentItem.getQue());
        holder.text_option1.setText(currentItem.get_1());
        holder.text_option2.setText(currentItem.get_2());
        holder.text_option3.setText(currentItem.get_3());
        holder.text_option4.setText(currentItem.get_4());

    }

    @Override
    public int getItemCount() {
        return quizData.size();
    }

    public static class QuestionListRecyclerAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView text_question_count, text_question, text_option1, text_option2, text_option3, text_option4;

        public QuestionListRecyclerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            text_question_count = itemView.findViewById(R.id.text_question_count);
            text_question = itemView.findViewById(R.id.text_question);
            text_option1 = itemView.findViewById(R.id.text_option1);
            text_option2 = itemView.findViewById(R.id.text_option2);
            text_option3 = itemView.findViewById(R.id.text_option3);
            text_option4 = itemView.findViewById(R.id.text_option4);

        }
    }
}