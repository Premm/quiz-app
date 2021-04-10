package com.example.quizapp_task31c;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String USERNAME = "com.example.quizapp_task31c.USERNAME";
    public static final String QUESTION_ARRAY = "com.example.quizapp_task31c.QUESTION_ARRAY";
    Button startButton;
    EditText nameEditText;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = findViewById(R.id.startButton);
        nameEditText = findViewById(R.id.nameEditText);
        Intent intent = getIntent();
        username = intent.getStringExtra(MainActivity.USERNAME);
        nameEditText.setText(username);
    }

    void startQuiz(JSONArray questions) {
        String name = nameEditText.getText().toString();
        if(name.trim().length() > 0){
            Intent intent = new Intent(this, QuestionActivity.class);
            intent.putExtra(USERNAME, name);
            intent.putExtra(QUESTION_ARRAY, questions.toString());
            startActivity(intent);
            finish();
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "You must enter a name", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void GenerateQuestionsAndStartQuiz(View view) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, "https://opentdb.com/api.php?amount=5&category=9&difficulty=easy&type=multiple&encode=url3986", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray res = response.getJSONArray("results");
                            for(int i = 0; i < res.length() - 1; ++i){
                                Log.d("JSONObject", res.getJSONObject(i).toString());
                            }
                            startQuiz(res);
                        } catch (JSONException e) {
                            //Failed to get results.
                            //e.printStackTrace();
                           Log.d("Error", response.toString());
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        QuestionGenerator.getInstance(this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}