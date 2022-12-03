package com.example.flipflip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ModeSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);
    }

    public void goToDifficultySelect(View view){
        Intent intent = new Intent(this, DifficultySelectActivity.class);
        int player;
        switch (view.getId()){
            case R.id.onePlayer:
                player=1;
                break;
            default:
                player=2;
        }
        intent.putExtra("player",player);

        startActivity(intent);
    }
}
