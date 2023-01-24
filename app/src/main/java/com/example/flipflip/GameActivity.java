package com.example.flipflip;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Dimension;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Display;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameActivity extends AppCompatActivity {

    private Intent intent;
    private TableLayout tl;
    private TableRow tRow;
//    private ImageView img;
    private CardView img;
    private int idIndex;
    private int second = 0;
    private pictureImg[][] cardList;
    private int difficulty, imgIndex;
    private SoundPool soundPool;
    private int flipSound;
    private Class<R.drawable> drawable = R.drawable.class;
    private Field field;
    private int player;
    private int[] soloData, duoData;             //1인용 데이터, 2인용 데이터
    private TextView timer;             //타이머
    private Thread t;                   //스레드
    private SoloTimerThread stt;        //1인용 스레드
    private DuoTimerThread dtt;         //2인용 스레드
    private ObjectAnimator animator;
    private List<Integer> blockImgList;
    private AlertDialog.Builder alertDialog;
    private DifficultySelectActivity dsa;
    private ModeSelectActivity msa;
    private String packageName;
    private TextView score_1, score_2;
    private final static Logger log = Logger.getGlobal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        packageName = this.getPackageName();

        //        효과음
        soundPool = new SoundPool.Builder().build();
        flipSound = soundPool.load(this, R.raw.cardflip, 0);

//                   오픈한 카드 개수, 클릭 회수, 열린 카드 아이디(-1은 열린 카드 없음), Score
        soloData = new int[]{0, 0, -1, 0};
//                   레드팀 Score, 블루팀 Scrore, 팀 Turn, 오픈한 카드 개수, 열린 카드 아이디(-1은 열린 카드 없음)
        duoData = new int[]{0, 0, 0, 0, -1};

        alertDialog = new AlertDialog.Builder(GameActivity.this);

        //액티비티 종료를 위한 변수
        dsa = (DifficultySelectActivity)DifficultySelectActivity.difficultySelectActivity;
        msa = (ModeSelectActivity)ModeSelectActivity.modeSelectActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onStart(){
        super.onStart();
        intent = getIntent();
        player = intent.getIntExtra("player",0);
        difficulty = intent.getIntExtra("difficulty",0);
        imgIndex = intent.getIntExtra("imgIndex",0);
        int startIndex = 0;

        cardList = new pictureImg[difficulty][];
        for (int i = 0; i < difficulty; i++){
            cardList[i] = new pictureImg[difficulty];
        }
        int[] integers = getResources().getIntArray(R.array.img_index);
        for (int i =0;i<imgIndex;i++){
            startIndex += integers[i];
        }
        blockImgList = new ArrayList<>();
        generateImgIndex(startIndex+1,startIndex+integers[imgIndex]);
        appendTableRow(startIndex+1,startIndex+integers[imgIndex]);

        for (pictureImg[] card: cardList) {
            for (pictureImg eachCard:card) {
                System.out.print(eachCard.getImgIndex()+"-");
            }
            System.out.println();
        }
        score_1 = findViewById(R.id.score_1);
        score_2 = findViewById(R.id.score_2);
        startAllOpen();
    }

    //    이차원 객체 배열에 랜덤 이미지 인덱스 설정
    private void generateImgIndex(int start, int end) {
        Integer[] randomList = new Integer[difficulty*difficulty/2];
        Random rand = new Random();

//        중복 없이 카드 인덱스 뽑기
        for (int i = 0; i < difficulty*difficulty/2; i++){
            randomList[i] = rand.nextInt(end)+start;
            for(int j = 0; j<i; j++){
                if (randomList[i].equals(randomList[j])){
                    i--;
                    break;
                }
            }
        }

//        카드 셔플
        List<Integer> tempList1 = new ArrayList(Arrays.asList(randomList));
        List<Integer> tempList2 = new ArrayList(Arrays.asList(randomList));
        tempList1.addAll(tempList2);
        Collections.shuffle(tempList1);
        Collections.shuffle(tempList1);
        Integer[] res = tempList1.toArray(new Integer[tempList1.size()]);

//        카드 데이터 세팅
        for (int i =0; i < difficulty; i++){
            for (int j =0; j < difficulty; j++){
                cardList[i][j] = new pictureImg(false, res[i*difficulty+j]);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
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

        size.y = size.y-test.getMeasuredHeight()-test1.getMeasuredHeight();


        idIndex = 0;
        for (int i=0; i<difficulty; i++){
            tRow = new TableRow(this);
            for (int j=0; j<difficulty; j++){
//                img = new ImageView(this);
                img = new CardView(this);
                img.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0,0,view.getWidth(),view.getHeight(),28);
                    }
                });
                img.setId(idIndex);
                img.setPadding(10,10,10,10);
//                img.setBackgroundResource(R.drawable.card_img_120);


//                img.setBackgroundResource(this.getResources().getIdentifier(getString(R.string.test_img)+".jpg", "drawable", this.getPackageName()));


//                img.setBackgroundResource(R.drawable.test1);
//                img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                try {
                    img.setBackgroundResource(getResources().getIdentifier( "@drawable/test"+cardList[i][j].getImgIndex(), "drawable", this.getPackageName()));
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"오류가 발생했습니다.",Toast.LENGTH_LONG).show();
                }

//                변수 이용해서 이미지 설정
//                img.setImageResource(R.drawable.card_img_423);
//                img.setImageResource(R.drawable.test1);
                System.out.println(size.y+"|"+test.getMeasuredHeight()+"|"+test1.getMeasuredHeight());
                //이미지 크기 조절 (임시 설정)
                TableRow.LayoutParams params = new TableRow.LayoutParams(size.x/difficulty, size.y/difficulty);
                img.setLayoutParams(params);
                System.out.println("Img Size = "+size.x/difficulty+","+size.y/difficulty);
                img.setPadding(10,10,10,10);
                img.setOnClickListener(new  View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        clickControl(false);
                        if(soundPool!=null){
                            soundPool.play(flipSound, 5, 5, 0, 0, 1);
                            flipSound = soundPool.load(GameActivity.super.getApplicationContext(), R.raw.cardflip, 0);
                        }

                        animator = ObjectAnimator.ofFloat(v,"rotationY",0,180);
                        animator.setDuration(500);
                        animator.start();
//                        animator.addListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                super.onAnimationEnd(animation);
//                                clickControl(true);
//                            }
//                        });

                        if(player == 1 ){
                            soloData[1]++;
                            // 처음 카드 오픈할 때
                            if(soloData[2] == -1){
                                soloData[0]++;
                                soloData[2] = v.getId();
                                reverseCard(v, false, true);
                                blockImgList.add(v.getId());
                                clickControl(true);
                            }else{
                                //두 카드가 일치할 경우
                                if(cardList[soloData[2]/(difficulty)][soloData[2]%(difficulty)].getImgIndex() == cardList[v.getId()/(difficulty)][v.getId()%(difficulty)].getImgIndex()){
                                    findViewById(v.getId()).setClickable(false);
                                    reverseCard(v, false,true);
                                    blockImgList.add(v.getId());
                                    soloData[0]++;
                                    soloData[2] = -1;
                                    soloData[3] += 2;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            score_1.setText(String.format("%02d", soloData[3]));
                                            clickControl(true);
                                        }
                                    }, 250);

                                //두 카드가 불 일치할 경우 계속 클릭 시 오류 발생
                                }else{
                                    findViewById(v.getId()).setClickable(false);
                                    findViewById(soloData[2]).setClickable(true);
                                    reverseCard(v, false,true);
                                    //잘못 열린 두 카드 원래 상태로 변경
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            animator = ObjectAnimator.ofFloat(findViewById(v.getId()),"rotationY",0,-180);
                                            animator.setDuration(500);
                                            animator.start();
                                            reverseCard(v, true,true);
                                            animator = ObjectAnimator.ofFloat(findViewById(soloData[2]),"rotationY",0,-180);
                                            animator.start();
                                            reverseCard(findViewById(soloData[2]), true,true);
                                            blockImgList.remove((Integer) soloData[2]);
//                                            findViewById(v.getId()).setClickable(true);
//                                            findViewById(soloData[2]).setClickable(true);
                                            soloData[0]--;
                                            soloData[2] = -1;
                                            clickControl(true);
                                        }
                                    }, 850);
                                }
                            }

//                            게임 종료
                            if(soloData[0] == difficulty*difficulty){
                                t.interrupt();
                                alertDialog.setTitle("결과 창");
                                alertDialog.setMessage("성공했습니다.\n"+Integer.parseInt(timer.getText().toString())+"초 걸렸습니다.");
                                alertDialog.setPositiveButton("다시 하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        startActivity(intent);
                                    }
                                });
                                alertDialog.setNegativeButton("난이도 선택", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        dsa.finish();
                                        intent = new Intent(GameActivity.this, DifficultySelectActivity.class);
                                        intent.putExtra("imgIndex",imgIndex);
                                        intent.putExtra("player",player);
                                        startActivity(intent);
                                    }
                                });
                                alertDialog.setNeutralButton("처음 메뉴로", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        dsa.finish();
                                        msa.finish();
                                        startActivity(new Intent(GameActivity.this, ModeSelectActivity.class));
                                    }
                                });
                                alertDialog.create().show();
                            }

//                            레드팀 Score, 블루팀 Scrore, 팀 Turn, 오픈한 카드 개수, 열린 카드 아이디(-1은 열린 카드 없음)
//                            duoData = new int[]{0, 0, 0, 0, -1};
                        }else{
//                            카드 처음 오픈
                            if(duoData[4] == -1){
                                findViewById(v.getId()).setClickable(false);
                                reverseCard(v, false,false);
                                duoData[4] = v.getId();
                                blockImgList.add(duoData[4]);
                                clickControl(true);
                            }else{
//                                오픈한 두 카드가 일치할 경우
                                if(cardList[duoData[4]/(difficulty)][duoData[4]%(difficulty)].getImgIndex() == cardList[v.getId()/(difficulty)][v.getId()%(difficulty)].getImgIndex()){
                                    findViewById(v.getId()).setClickable(false);
                                    duoData[4] = -1;
                                    reverseCard(v, false,false);
                                    blockImgList.add(v.getId());
                                    duoData[duoData[2]] += 2;
                                    duoData[3] += 2;
                                    clickControl(true);
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            try{
                                                t.interrupt();
                                            }catch (Exception e){
                                                log.setLevel(Level.ALL);
                                                log.info("게임 종료");
                                            }

                                            if(duoData[2]==0){
                                                score_1.setText(String.format("%02d", duoData[duoData[2]]));
                                            }else{
                                                score_2.setText(String.format("%02d", duoData[duoData[2]]));
                                            }

                                        }
                                    }, 250);
                                }else{
//                                    findViewById(duoData[4]).setClickable(true);
                                    reverseCard(v, false,false);
                                    //잘못 열린 두 카드 원래 상태로 변경
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            animator = ObjectAnimator.ofFloat(findViewById(v.getId()),"rotationY",0,-180);
                                            animator.setDuration(500);
                                            animator.start();
                                            reverseCard(v, true,false);
                                            animator = ObjectAnimator.ofFloat(findViewById(duoData[4]),"rotationY",0,-180);
                                            animator.start();
                                            reverseCard(findViewById(duoData[4]), true,false);
                                            blockImgList.remove((Integer) duoData[4]);
                                            duoData[4] = -1;
                                            t.interrupt();
                                            duoData[2] = (duoData[2]+1)%2;
                                            clickControl(true);
                                        }
                                    }, 850);
                                }
                            }

//                            게임 종료
                            if(duoData[3] == difficulty*difficulty){
                                t.interrupt();
                                t = null;
                                String msg;
                                if(duoData[0] > duoData[1]){
                                    msg = "레드팀이 이겼습니다!!";
                                }else if (duoData[0] < duoData[1]){
                                    msg = "블루팀이 이겼습니다!!";
                                }else{
                                    msg = "비겼습니다!!";
                                }

                                alertDialog.setTitle("결과 창");
                                alertDialog.setMessage(msg);
                                alertDialog.setPositiveButton("다시 하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        startActivity(intent);
                                    }
                                });
                                alertDialog.setNegativeButton("난이도 선택", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        dsa.finish();
                                        intent = new Intent(GameActivity.this, DifficultySelectActivity.class);
                                        intent.putExtra("imgIndex",imgIndex);
                                        intent.putExtra("player",player);
                                        startActivity(intent);
                                    }
                                });
                                alertDialog.setNeutralButton("처음 메뉴로", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        dsa.finish();
                                        msa.finish();
                                        startActivity(new Intent(GameActivity.this, ModeSelectActivity.class));
                                    }
                                });
                                alertDialog.create().show();
                            }
                        }
//                        clickControl(true);

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
        TextView timer = findViewById(R.id.timer);
        score_1.setText(String.format("%02d", 0));
        if(player == 1){
            timer.setTextSize(Dimension.SP, 55);
            timer.setText(String.format("%02d:%02d", 0,0));
        }else{
            timer.setTextSize(Dimension.SP, 60);
            timer.setText(String.format("%02d", 0));
            score_2.setText(String.format("%02d", 0));
        }
        clickControl(false);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < idIndex; i++){
                    animator = ObjectAnimator.ofFloat(findViewById(i),"rotationY",0,180);
                    animator.start();
                    reverseCard(findViewById(i), true,false);
                }
                clickControl(true);
                if(player == 1){
                    stt = new SoloTimerThread();
                    t = new Thread(stt);
                }else{
                    dtt = new DuoTimerThread();
                    t = new Thread(dtt);
                }
                t.start();
            }
        }, 5000);
    }

    private void clickControl(boolean bool) {
        if(bool){
            for (int i =0; i<idIndex;i++){
                if(!blockImgList.contains(i)) {
                    findViewById(i).setClickable(true);
                }
            }
        }else{
            for (int i =0; i<idIndex;i++){
                findViewById(i).setClickable(false);
            }
        }
    }

    private class SoloTimerThread implements Runnable {
        private SoloTimerThread(){
            timer = findViewById(R.id.timer);
        }

        public void run(){
            while (true){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timer.setText(String.format("%02d:%02d", (int)second/60,second%60));
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                second++;
            }
        }
    }

    private class DuoTimerThread implements Runnable {
        private DuoTimerThread(){
            timer = findViewById(R.id.timer);
        }

        public void run(){
            int second = 10;
            while (second>=0){
                timer.setText(String.format("%02d", second));
                try {
                    // 스레드에게 수행시킬 동작들 구현
                    Thread.sleep(1000); // 1초간 Thread를 잠재운다
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                second--;
            }
            if(t != null){
                turnChange();
            }
        }
    }

    private void turnChange() {
        t.interrupt();
        t = null;
        t = new Thread(dtt);
        t.start();
    }

    private void reverseCard(final View v, boolean isopend, final boolean isSolo) {
        String img_name;
        final int resource;

        try {
            if(isopend){
                field = drawable.getField(getString(R.string.test_img));
                resource = field.getInt(null);
            }else{
                resource = getResources().getIdentifier( "@drawable/test"+cardList[v.getId()/(difficulty)][v.getId()%(difficulty)].getImgIndex(), "drawable", packageName);
            }
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    findViewById(v.getId()).setBackgroundResource(resource);

                    //2인용일 때 턴 처리
                    if(!isSolo){

                    }
                }
            }, 250);

        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
        stt = null;
        dtt = null;
        t = null;
    }
}
