package com.example.quizapp_task31c;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;

public class ScoreActivity extends AppCompatActivity {
    String username;
    int questionArrayLength = 0;
    int currentScore = 0;


    TextView congratulationsTextView;
    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        congratulationsTextView = findViewById(R.id.congratulationsTextView);
        score = findViewById(R.id.score);

        Intent intent = getIntent();
        questionArrayLength = intent.getIntExtra(QuestionActivity.QUESTION_ARRAY_LENGTH, 0);
        currentScore = intent.getIntExtra(QuestionActivity.SCORE,0);
        username = intent.getStringExtra(MainActivity.USERNAME);

        if(currentScore > (float)questionArrayLength/2.0){
            congratulationsTextView.setText(getString(R.string.congratulations, username));
        }else{
            congratulationsTextView.setText(getString(R.string.do_better, username));
        }

        score.setText(getString(R.string.score, currentScore, questionArrayLength));


    }

    public void resetQuiz(View view) {
        //go to last activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.USERNAME, username);
        startActivity(intent);
        finish();
    }

    public void closeApp(View view) {
        finish();
        System.exit(0);
    }
}