package com.example.quizapp_task31c;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class QuestionActivity extends AppCompatActivity {
    public static final String PROGRESS = "com.example.quizapp_task31c.PROGRESS";
    public static final String QUESTION_ARRAY_LENGTH = "com.example.quizapp_task31c.QUESTION_ARRAY_LENGTH";
    public static final String SCORE = "com.example.quizapp_task31c.SCORE";
    //passed by intent extras.
    String username;
    JSONArray questionArray;
    int currentScore;

    // reference to xml views.
    TextView welcomeMessage;
    LinearLayout choiceButtonContainer;
    TextView questionTextView;
    TextView questionTitle;
    Button submitButton;
    ProgressBar progressBar;
    TextView stageText;
    // store the current question
    JSONObject questionObject;

    // store the correctAnswer so we know if they got it right.
    String correctAnswer;

    // store the selected answer.
    String selectedAnswer;

    // lock selecting answer if answer is submitted.
    Boolean submittedAnswer = false;

    //store the current question
    int stage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Intent intent = getIntent();
        username = intent.getStringExtra(MainActivity.USERNAME);
        welcomeMessage = findViewById(R.id.welcomeTextView);
        welcomeMessage.setText(getString(R.string.welcome_message, username));
        choiceButtonContainer = findViewById(R.id.choicesContainer);
        questionTextView = findViewById(R.id.questionText);
        questionTitle = findViewById(R.id.questionTitle);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);
        stageText = findViewById(R.id.progressLabel);
        if(intent.getIntExtra(QuestionActivity.PROGRESS,0) > 0){
            stage = intent.getIntExtra(QuestionActivity.PROGRESS,0);
        }
        String serialisedJsonArray = intent.getStringExtra(MainActivity.QUESTION_ARRAY);
        try{
            questionArray = new JSONArray(serialisedJsonArray);
            questionObject = questionArray.getJSONObject(stage - 1);
            stageText.setText(getString(R.string.progress_label,stage,questionArray.length()));
            SetQuestionText();
            CreateChoiceButtons();
            Log.d("QuestionArray", questionArray.toString());
        }catch(JSONException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(this.getApplicationContext(), "Sorry something went wrong. Try restarting the application.", Toast.LENGTH_LONG);
            toast.show();
        }
        progressBar.setMax(questionArray.length());
        progressBar.setProgress(stage);
        currentScore = intent.getIntExtra(QuestionActivity.SCORE, 0);

    }
    private void SetQuestionText(){
        try {
            questionTitle.setText(java.net.URLDecoder.decode(questionObject.optString("category"), StandardCharsets.UTF_8.name()));
            questionTextView.setText(java.net.URLDecoder.decode(questionObject.optString("question"), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    private void  CreateChoiceButtons() {
        choiceButtonContainer.removeAllViews();



        JSONArray incorrectJSONAnswers = null;
        try {
            incorrectJSONAnswers = questionObject.getJSONArray("incorrect_answers");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String> possibleAnswers = new ArrayList<String>();//leave room for the correct answer
        for(int i=0; i<incorrectJSONAnswers.length(); ++i) {
            try {
                possibleAnswers.add(java.net.URLDecoder.decode(incorrectJSONAnswers.optString(i), StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            correctAnswer = java.net.URLDecoder.decode(questionObject.optString("correct_answer"), StandardCharsets.UTF_8.name());
            possibleAnswers.add(correctAnswer);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //sort the strings by alphabetical orders, so the correct answer isn't always last.
        Collections.sort(possibleAnswers);

        for(int i = 0; i < possibleAnswers.size(); ++i) {
            Button button = new Button(this);
            button.setId(i);
            button.setText(possibleAnswers.get(i));
            Context ctx = this;
            button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    if(!submittedAnswer) {
                        Button defbtn = new Button(ctx);
                        for (int i = 0; i < choiceButtonContainer.getChildCount(); i++) {
                            Button choiceButton = (Button) choiceButtonContainer.getChildAt(i);
                            choiceButton.setBackground(defbtn.getBackground());
                            choiceButton.setTextColor(defbtn.getTextColors());
                        }
                        Button selectedButton = (Button) view;
                        selectedAnswer = selectedButton.getText().toString();
                        selectedButton.setBackgroundColor(getResources().getColor(R.color.purple_500));
                        selectedButton.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            });
            choiceButtonContainer.addView(button);
        }
    }

    public void onSubmit(View view) {
        if(submittedAnswer) {
            if (stage < questionArray.length()){
                Intent intent = new Intent(this, QuestionActivity.class);
                intent.putExtra(MainActivity.USERNAME, username);
                intent.putExtra(MainActivity.QUESTION_ARRAY, questionArray.toString());
                intent.putExtra(QuestionActivity.PROGRESS, stage + 1);
                intent.putExtra(QuestionActivity.SCORE, currentScore);
                startActivity(intent);
            } else {
                //go to last activity
                Intent intent = new Intent(this, ScoreActivity.class);
                intent.putExtra(MainActivity.USERNAME, username);
                intent.putExtra(QuestionActivity.QUESTION_ARRAY_LENGTH, questionArray.length());
                intent.putExtra(QuestionActivity.SCORE, currentScore);
                startActivity(intent);
            }
            finish();
        }else{
            submittedAnswer = true;
            if(correctAnswer.equals(selectedAnswer)){
                currentScore++;
            }
            for (int i = 0; i < choiceButtonContainer.getChildCount(); i++) {
                Button choiceButton = (Button) choiceButtonContainer.getChildAt(i);
                if(choiceButton.getText().toString().equals(correctAnswer)){
                    choiceButton.setBackgroundColor(getResources().getColor(R.color.success));
                }else if(choiceButton.getText().toString().equals(selectedAnswer)){
                    choiceButton.setBackgroundColor(getResources().getColor(R.color.danger));
                }
            }
            submitButton.setText(getString(R.string.next_text));

        }



    }
}


