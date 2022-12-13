package com.example.flipflip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class GameActivity extends AppCompatActivity {

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
        final TableLayout tl = (TableLayout)findViewById(R.id.tableLayout);
//        float len = tl.getWidth();
        for (int i=0; i<difficulty; i++){
            TableRow tRow = new TableRow(this);
            for (int j=0; j<difficulty; j++){
                ImageView img = new ImageView(this);
                img.setImageResource(R.drawable.ic_launcher_foreground);

                //이미지 크기 조절


                tRow.addView(img);
            }
            tl.addView(tRow);
        }

//        final TableLayout layout = (TableLayout)findViewById(R.id.tableLayout);
//        TableRow row = new TableRow(this);
//        TextView column1 = new TextView(this);
//        column1.setText("컬럼1");
//        row.addView(column1);
//        TextView column2 = new TextView(this);
//        column2.setText("컬럼2");
//        row.addView(column2);
//        layout.addView(row);
//
//        for(int i = 0; i < 3; i++) {
//            TableRow row2 = new TableRow(this);
//            for(int j = 0 ; j < 2; j++) {
//                TextView textView = new TextView(this);
//                textView.setText(i + " x " + j);
//                row2.addView(textView);
//            }
//            layout.addView(row2);
//        }
    }
}
