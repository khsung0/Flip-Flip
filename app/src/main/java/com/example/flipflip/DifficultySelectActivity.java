package com.example.flipflip;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DifficultySelectActivity extends AppCompatActivity {

    public static Activity difficultySelectActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        difficultySelectActivity = DifficultySelectActivity.this;
        setContentView(R.layout.activity_difficulty_select);
    }

    public void goToGame(View view){
        Intent intent = getIntent();
        int player = intent.getIntExtra("player",0);
        int imgIndex = intent.getIntExtra("imgIndex",0);
        intent = new Intent(this, GameActivity.class);
        int difficulty;
        switch (view.getId()){
            case R.id.low:
                difficulty=4;
                break;
            case R.id.mid:
                difficulty=6;
                break;
            case R.id.high:
                difficulty=8;
                break;
            default:
                difficulty=0;
        }
        intent.putExtra("player",player);
        intent.putExtra("difficulty",difficulty);
        intent.putExtra("imgIndex",imgIndex);

        startActivity(intent);
    }
}
