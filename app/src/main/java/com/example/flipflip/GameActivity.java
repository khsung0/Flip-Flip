package com.example.flipflip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TableLayout tl;
    private TableRow tRow;
    private ImageView img;
    private int idIndex;
    private pictureImg[][] cardList;
    private int difficulty;
    private SoundPool soundPool;
    private int flipSound;
    private int[] openedIndex = {-1, -1};
    private int clickCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //        효과음
        soundPool = new SoundPool.Builder().build();
        flipSound = soundPool.load(this, R.raw.cardflip, 0);

        clickCnt = 0;
    }

    @Override
    protected void onStart(){
        super.onStart();
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
            randomList[i] = rand.nextInt(end)+start;
            for(int j = 0; j<i; j++){
                if (randomList[i].equals(randomList[j])){
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
        Integer[] res = tempList1.toArray(new Integer[tempList1.size()]);

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

        test.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        System.out.println(test.getMeasuredHeight());
        System.out.println(test.getMeasuredWidth());
        View test1 = findViewById(R.id.ad_test);
        test1.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        System.out.println(test1.getMeasuredHeight());

        size.y=size.y-test.getMeasuredHeight()-test1.getMeasuredHeight();


        idIndex=0;
        for (int i=0; i<difficulty; i++){
            tRow = new TableRow(this);
            for (int j=0; j<difficulty; j++){
                img = new ImageView(this);
                img.setId(idIndex);
                img.setImageResource(R.drawable.card_img);
                System.out.println(size.y+"|"+test.getMeasuredHeight()+"|"+test1.getMeasuredHeight());
                //이미지 크기 조절 (임시 설정)
                TableRow.LayoutParams params = new TableRow.LayoutParams(size.x/difficulty, size.y/difficulty);
                img.setLayoutParams(params);
                img.setPadding(10,10,10,10);
                img.setOnClickListener(new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i =0; i<idIndex;i++){
                            findViewById(i).setClickable(false);
                        }
                        if(soundPool!=null){
                            soundPool.play(flipSound, 5, 5, 0, 0, 1);
                            flipSound=soundPool.load(GameActivity.super.getApplicationContext(), R.raw.cardflip, 0);
                        }
                        ObjectAnimator animator = ObjectAnimator.ofFloat(v,"rotationY",0,180);
                        animator.setDuration(500);
                        animator.start();
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                for (int i =0; i<idIndex;i++){
                                    findViewById(i).setClickable(true);
                                }
                            }
                        });
                    }
                });
                //Table Row에 이미지 붙이기
                tRow.addView(img);
                idIndex++;
            }
            //TableView에 Table Row붙이기
            tl.addView(tRow);
        }

    }

    private void startAllOpen(){
        ObjectAnimator animator;
        StartThread sleepThread = new StartThread();

        for (int i = 0; i < idIndex; i++){
            animator = ObjectAnimator.ofFloat(findViewById(i),"rotationY",0,180);
            animator.start();
        }
//        스레드 기다리면서 렌더링이 늦어짐
//        Thread test = new Thread(sleepThread);
//        test.start();
//
//        try {
//            test.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        for (int i = 0; i < idIndex; i++){
            animator = ObjectAnimator.ofFloat(findViewById(i),"rotationY",0,180);
            animator.start();
        }
    }

    private void soloGame() {
        startAllOpen();
        //        타이머 스레드 실행
        SoloTimerThread th = new SoloTimerThread();
        Thread t = new Thread(th);
        t.start();
    }

    private void duoGame() {
        startAllOpen();
        DuoTimerThread th = new DuoTimerThread();
        Thread t = new Thread(th);
        t.start();

    }

    private class StartThread implements Runnable {
        private StartThread(){
        }

        public void run(){
            try {
                // 스레드에게 수행시킬 동작들 구현
                Thread.sleep(5000); // 1초간 Thread를 잠재운다
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class SoloTimerThread implements Runnable {
        TextView timer;
        private SoloTimerThread(){
            timer = findViewById(R.id.timer);
        }

        public void run(){
            int second = 0;
            while (true){
                timer.setText(String.format("%03d", second));
                try {
                    // 스레드에게 수행시킬 동작들 구현
                    Thread.sleep(1000); // 1초간 Thread를 잠재운다
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                second++;
            }
        }
    }

    private class DuoTimerThread implements Runnable {
        TextView timer;
        private DuoTimerThread(){
            timer = findViewById(R.id.timer);
        }

        public void run(){
            int second = 10;
            while (second>=0){
                timer.setText(Integer.toString(second));
                try {
                    // 스레드에게 수행시킬 동작들 구현
                    Thread.sleep(1000); // 1초간 Thread를 잠재운다
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                second--;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }
}
