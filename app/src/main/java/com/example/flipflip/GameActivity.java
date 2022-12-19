package com.example.flipflip;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class GameActivity extends AppCompatActivity {

    private TableLayout tl;
    private TableRow tRow;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        int player = intent.getIntExtra("player",0);
        int difficulty = intent.getIntExtra("difficulty",0);
        System.out.println(difficulty);
        appendTableRow(difficulty);
    }

    public void appendTableRow(int difficulty){
        tl = findViewById(R.id.tableLayout);
        tl.setGravity(1);

        for (int i=0; i<difficulty; i++){
            tRow = new TableRow(this);
            for (int j=0; j<difficulty; j++){
                img = new ImageView(this);
                img.setImageResource(R.drawable.test);

                //이미지 크기 조절
                TableRow.LayoutParams params = new TableRow.LayoutParams(100,150);
                img.setLayoutParams(params);

                //Table Row에 이미지 붙이기
                tRow.addView(img);
            }
            //TableView에 Table Row붙이기
            tl.addView(tRow);
        }

    }
}
