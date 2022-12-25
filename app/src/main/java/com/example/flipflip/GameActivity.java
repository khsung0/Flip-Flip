package com.example.flipflip;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
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
        int imgIndex = intent.getIntExtra("imgIndex",0);
        int startIndex = 0;
        int[] integers = getResources().getIntArray(R.array.img_index);
        for (int i =0;i<imgIndex;i++){
            startIndex += integers[i];
        }


        appendTableRow(difficulty,startIndex+1,startIndex+integers[imgIndex]);

        //1인용일 때
        if(player==1){
            soloGame();
        //2인용일 때
        }else{
            duoGame();
        }

    }

    public void appendTableRow(int difficulty, int start, int end){
        tl = findViewById(R.id.tableLayout);
        tl.setGravity(1);

        //디스플레이 길이 구하기
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        System.out.println(size.x+" : "+size.y);

        //뷰 크기 구하기 테스트
        View test = findViewById(R.id.head_row);

        test.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);;
        System.out.println(test.getMeasuredHeight());
        System.out.println(test.getMeasuredWidth());
        View test1 = findViewById(R.id.ad_test);
        test1.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);;
        System.out.println(test1.getMeasuredHeight());

        int lid;



        int temp=1;
        for (int i=0; i<difficulty; i++){
            tRow = new TableRow(this);
            for (int j=0; j<difficulty; j++){
                img = new ImageView(this);
                img.setId(temp);
                img.setImageResource(R.drawable.card_img);
                System.out.println(size.y+"|"+test.getMeasuredHeight()+"|"+test1.getMeasuredHeight());
                //이미지 크기 조절 (임시 설정)
                TableRow.LayoutParams params = new TableRow.LayoutParams(size.x/difficulty,size.y/difficulty/2);
                img.setLayoutParams(params);
                img.setOnClickListener(new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        v.setClickable(false);
                        ObjectAnimator animator = ObjectAnimator.ofFloat(v,"rotationY",0,180);
                        animator.setDuration(500);
                        animator.start();
                    }
                });
                //Table Row에 이미지 붙이기
                tRow.addView(img);

                temp++;
            }
            //TableView에 Table Row붙이기
            tl.addView(tRow);
        }

    }

    private void soloGame() {
    }

    private void duoGame() {
    }
}
