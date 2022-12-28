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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TableLayout tl;
    private TableRow tRow;
    private ImageView img;
    private pictureImg[][] cardList;
    private int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        int player = intent.getIntExtra("player",0);
        difficulty = intent.getIntExtra("difficulty",0);
        int imgIndex = intent.getIntExtra("imgIndex",0);
        int startIndex = 0;
        cardList = new pictureImg[difficulty][];
        for (int i = 0; i < difficulty; i++){
            cardList[i] = new pictureImg[difficulty];
        }
        int[] integers = getResources().getIntArray(R.array.img_index);
        for (int i =0;i<imgIndex;i++){
            startIndex += integers[i];
        }

        generateImgIndex(startIndex+1,startIndex+integers[imgIndex]);

        appendTableRow(startIndex+1,startIndex+integers[imgIndex]);

        //1인용일 때
        if(player==1){
            soloGame();
        //2인용일 때
        }else{
            duoGame();
        }

    }

//    이차원 객체 배열에 랜덤 이미지 인덱스 설정
    private void generateImgIndex(int start, int end) {
        Integer[] randomList = new Integer[difficulty*difficulty/2];
        Random rand = new Random();
        for (int i = 0; i < difficulty*difficulty/2; i++){
            randomList[i] = (Integer)rand.nextInt(end)+start;
            for(int j = 0; j<i; j++){
                if (randomList[i] == randomList[j]){
                    i--;
                    break;
                }
            }
        }
        List<Integer> tempList1 = new ArrayList(Arrays.asList(randomList));
        List<Integer> tempList2 = new ArrayList(Arrays.asList(randomList));
        tempList1.addAll(tempList2);
        Collections.shuffle(tempList1);
        Collections.shuffle(tempList1);
        Integer res[] = tempList1.toArray(new Integer[tempList1.size()]);

        for (int i =0; i < difficulty; i++){
            for (int j =0; j < difficulty; j++){
                cardList[i][j] = new pictureImg(false, res[i*difficulty+j]);
            }
        }
    }

    public void appendTableRow(int start, int end){
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
                TableRow.LayoutParams params = new TableRow.LayoutParams(size.x/difficulty, size.x/difficulty*618/423);
                img.setLayoutParams(params);
                img.setPadding(10,10,10,10);
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
