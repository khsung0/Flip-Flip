package com.example.flipflip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ModeSelectActivity extends AppCompatActivity {

    String[] items = {"강아지", "고양이"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);

        //스피너 설정
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item , items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void goToDifficultySelect(View view){
        Intent intent = new Intent(this, DifficultySelectActivity.class);

        //스피너 값 가져와서 다음 액티비티로 전달
        Spinner spinner = findViewById(R.id.spinner);
        String jenre = spinner.getSelectedItem().toString();
        intent.putExtra("jenre",jenre);

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
