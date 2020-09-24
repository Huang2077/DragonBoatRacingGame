package com.example.a27560.the_final_test;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.example.a27560.the_final_test.Modul.Background;
import com.example.a27560.the_final_test.Modul.Dragonboat;
import com.example.a27560.the_final_test.Modul.MyObject;
import com.example.a27560.the_final_test.Modul.Shrub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class InternetGame extends AppCompatActivity {
    private GameView gameView;
    private Handler handler;
    private Runnable runnable;
    private int screenWidth ;
    private int screenHight;


    private int riverWidth = 420;//河道宽
    private int gettedscroe = 3;//初始得分不设置为0为了有更好的体验
    private int othersscroe = 3;
    private int speed = 5;//移动速度


    private float volume;//音量
   // private SoundPool soundPool;
   // private HashMap<Integer,Integer> soundMap = new HashMap<>();

    private Background background ;
    private Dragonboat dragonboat;
    private Dragonboat dragonboat_other;

    private int ObjectWidth = 85;//石头和粽子宽度
    private ArrayList<MyObject> zongzis = new ArrayList<>();//粽子
    private int amount_zongzi = 6;//粽子总数
    private ArrayList<MyObject> stones = new ArrayList<>();//石头
    private int amount_stone ;
    private ArrayList<Point> points = new ArrayList<>();//我要合理安排石头和粽子的位置

    private int amount_shrub = 5;//灌木
    private int Shrub_Width = 100;
    private int Shrub_Hight = 124;
    private ArrayList<Shrub> shrubs = new ArrayList<>();

    private Dialog dialog = null;//提示框

    //网络相关
    private Socket s ;
    private BufferedReader br ;
    private OutputStream os;
    private Handler InternetHandler;
    private Runnable InternetRunnable;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        gameView = new GameView(this);
        setContentView(gameView);
        //获得难度等级与音量
        Intent intent = getIntent();
        amount_stone = (Integer) intent.getSerializableExtra("level");
        volume = (float)intent.getSerializableExtra("volume");
        //初始化
        //初始化客户端服务器
        initCilent();
        initAll();
        //触摸事件监听
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventaction = event.getAction();
                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN: // 移动龙舟
                        int downX = (int) event.getRawX();
                        dragonboat.Move(15,screenWidth,downX,riverWidth);
                        //发送消息给服务器
                        sendMessageToServer(downX);
                        break;
                }
                return false;
            }
        });
        //循环器,每0.1s做的操作
        new Thread( new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        //做背景移动操作
                        background.Move(speed);
                        //移动物体
                        MoveObject();
                        //碰撞检测
                        CrashTest();
                        //判断游戏是否结束
                        IsGameOver();
                        //如果结束提示框不为空则显示
                        if(dialog != null)
                            dialog.show();
                        //每次都刷新一下UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gameView.invalidate();
                            }
                        });
                        handler.postDelayed(this, 100);
                    }

                };
                handler.post(runnable);
                Looper.loop();
            }
        }).start();

        //接受从服务器传来的信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                InternetHandler = new Handler();
                receiveMessageFromServer();
                InternetHandler.post(InternetRunnable);
                Looper.loop();
            }
        }).start();
    }

    //初始化
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initAll()
    {
        //获取屏幕属性
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHight = metrics.heightPixels;
        /*
        //初始化soundplayer
        AudioAttributes attr = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        soundPool = new SoundPool.Builder().setAudioAttributes(attr)
                .setMaxStreams(10)
                .build();
        soundMap.put(1,soundPool.load(this,R.raw.eat,1));
        soundMap.put(2,soundPool.load(this,R.raw.crash,1));
        */

        //创建各种对象
        //背景图
        Bitmap back = BitmapFactory.decodeResource(this.getResources(), R.drawable.river);
        //我这里的背景图是两张一样的图拼接在一起所以长度为屏幕两倍
        Bitmap suofangMap = Bitmap.createScaledBitmap(back, screenWidth, 2*screenHight,false) ;
        background = new Background(suofangMap , screenWidth ,screenHight ,screenHight);
        //龙舟 宽160 高540
        int boat_width = 45;
        int boat_hight = 240;
        dragonboat = new Dragonboat(BitmapFactory.decodeResource(this.getResources(), R.drawable.dragonboat),
                screenWidth/2-boat_width/2,
                screenHight-boat_hight-30,
                boat_width,
                boat_hight);
        dragonboat_other = new Dragonboat(BitmapFactory.decodeResource(this.getResources(), R.drawable.boat_black),
                screenWidth/2-boat_width/2,
                screenHight-boat_hight-30,
                boat_width,
                boat_hight);

        //开始设置粽子和石头
        //合理设置粽子和石头的位置
        setPointsOfZongziAndStone(ObjectWidth,amount_stone+amount_zongzi);
        //粽子
        for(int i=0; i<amount_zongzi; i++)
        {
            int x = points.get(i).x;
            int y = points.get(i).y;
            MyObject zongzi = new MyObject(BitmapFactory.decodeResource(this.getResources(), R.drawable.zongzi),x,y,ObjectWidth,ObjectWidth,2);
            zongzis.add(zongzi);
        }
        //石头
        for(int j=0; j<amount_stone; j++)
        {
            int x = points.get(amount_zongzi+j).x;
            int y = points.get(amount_zongzi+j).y;
            MyObject stone = new MyObject(BitmapFactory.decodeResource(this.getResources(), R.drawable.stone),x,y,ObjectWidth,ObjectWidth,-1);
            stones.add(stone);
        }

        //灌木
        //灌木的位置手动设置
        ArrayList<Point> shrub_points = new ArrayList<>();
        Point point1 = new Point(0,-20);
        shrub_points.add(point1);
        Point point2 = new Point(40,640);
        shrub_points.add(point2);
        Point point3 = new Point(620,20);
        shrub_points.add(point3);
        Point point4 = new Point(640,690);
        shrub_points.add(point4);
        Point point5 = new Point(630,1000);
        shrub_points.add(point5);
        //每个灌木的闪动时间间隔不同
        int[] times = {7,9,6,5,9};
        for(int k=0; k<amount_shrub; k++)
        {
            int x = shrub_points.get(k).x;
            int y = shrub_points.get(k).y;
            Shrub shrub = new Shrub(BitmapFactory.decodeResource(this.getResources(), R.drawable.firstshrub),
                    BitmapFactory.decodeResource(this.getResources(), R.drawable.secondshrub),
                    x,y,Shrub_Width,Shrub_Hight,times[k]);
            shrubs.add(shrub);
        }

    }

    //移动物体
    public void MoveObject()
    {
        //粽子移动
        for(int i = 0; i < amount_zongzi; i++)
        {
            zongzis.get(i).Move(speed,screenHight);
        }
        //石头移动
        for(int k = 0; k < amount_stone; k++)
        {
            stones.get(k).Move(speed,screenHight);
        }
        //灌木移动
        for(int z=0; z<amount_shrub; z++)
        {
            shrubs.get(z).Move(speed,screenHight);
        }
    }
    //碰撞检测
    public void CrashTest()
    {
        //龙舟1
        //粽子碰撞检测
        for(int j =0; j< amount_zongzi; j++)
        {
            if(dragonboat.iscrash(zongzis.get(j)))
            {
                //放音乐
               // soundPool.play(soundMap.get(1), volume, volume, 1, 0, 1);
                gettedscroe += zongzis.get(j).getScore();
                //重置粽子位置
                zongzis.get(j).ReSet(screenHight);
            }
        }
        //石头碰撞检测
        for(int l =0; l< amount_stone; l++)
        {
            if(dragonboat.iscrash(stones.get(l)))
            {
                //soundPool.play(soundMap.get(2), volume, volume, 1, 0, 1);
                gettedscroe += stones.get(l).getScore();
                //重置石头位置
                stones.get(l).ReSet(screenHight);
            }
        }
        //龙舟2
        //粽子碰撞检测
        for(int i =0; i< amount_zongzi; i++)
        {
            if(dragonboat_other.iscrash(zongzis.get(i)))
            {
                othersscroe += zongzis.get(i).getScore();
                //重置粽子位置
                zongzis.get(i).ReSet(screenHight);
            }
        }
        //石头碰撞检测
        for(int z =0; z< amount_stone; z++)
        {
            if(dragonboat_other.iscrash(stones.get(z)))
            {
                othersscroe += stones.get(z).getScore();
                //重置石头位置
                stones.get(z).ReSet(screenHight);
            }
        }
    }
    //判断游戏结束函数
    public void IsGameOver()
    {
        if((gettedscroe>=100 || gettedscroe<0 || othersscroe<0 || othersscroe>=100) && dialog == null)
        {
            String result = null;
            if(gettedscroe>=100 || othersscroe<0)
                result = "      你赢了！";
            if(gettedscroe<0 || othersscroe>=100)
                result = "      你输了！";
            //弹出对话框
            //创建一个对话框
            dialog = new AlertDialog.Builder(InternetGame.this)
                    //设置信息
                    .setMessage(result)
                    //设置对话框的按钮
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           // soundPool.release();
                            //转到主页面
                            Intent intent = new Intent(InternetGame.this,MainActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    }).create();;
        }
    }
    //返回按键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("结束该活动");
        //释放资源
        //soundPool.release();
    }

    //位置产生器
    public void setPointsOfZongziAndStone(int width,int amount)
    {
        Random random;
        int x,y;
        //先初始化
        for(int i = 0; i < amount; i++)
        {
            random = new Random();
            x = (screenWidth-riverWidth)/2 + random.nextInt(riverWidth-width);
            y = random.nextInt(screenHight-dragonboat.getHeight()-10);
            Point point = new Point(x,y);
            points.add(point);
        }
        //暴力判断,直到所有的粽子和石头之间不发生碰撞
        for(int j = 0 ; j < amount; j++)
        {
            while (true)
            {
                int k;
                //每一个物体的坐标都要与其他物体坐标进行碰撞检测
                for ( k = 0 ; k < amount ; k++)
                {
                    if (points.get(k) != points.get(j))
                    {
                        if (!
                                (points.get(k).x > points.get(j).x + width ||
                                        points.get(k).x + width < points.get(j).x ||
                                        points.get(k).y + width < points.get(j).y ||
                                        points.get(k).y > points.get(j).y + width)
                                )//只要发生碰撞就不安全
                            break;
                    }
                }
                if(k == amount)//和是所有人都不碰撞
                    break;

                else //如果发生碰撞则重新设置该物体的坐标
                {
                    random = new Random();
                    points.get(j).x = (screenWidth-riverWidth)/2 + random.nextInt(riverWidth-width);
                    points.get(j).y = random.nextInt(screenHight-dragonboat.getHeight()-10);
                }
            }
        }
    }


    //绘制
    class GameView extends View {
        private Paint paint;
        public GameView(Context context) {
            super(context);
            setFocusable(true);
            paint = new Paint();
        }
        //绘画函数
        @Override
        public void onDraw(Canvas canvas) {
            //画背景
            canvas.drawBitmap(background.GetNewBack(), 0, 0, null);
            //画龙舟
            canvas.drawBitmap(dragonboat.getBitmap(),dragonboat.getX(),dragonboat.getY(),null);
            canvas.drawBitmap(dragonboat_other.getBitmap(),dragonboat_other.getX(),dragonboat_other.getY(),null);
            //画粽子
            for(MyObject zongzi : zongzis)
            {
                canvas.drawBitmap(zongzi.getBitmap(),zongzi.getX(),zongzi.getY(),null);
            }
            //画石头
            for(MyObject stone : stones)
            {
                canvas.drawBitmap(stone.getBitmap(),stone.getX(),stone.getY(),null);
            }
            //画灌木
            for(int i = 0; i < amount_shrub; i++)
            {
                canvas.drawBitmap(shrubs.get(i).getNowbitmap(),shrubs.get(i).getX(),shrubs.get(i).getY(),null);
            }
            //画分数
            paint.setColor(Color.rgb(255,255,0));
            paint.setTextSize(50);
            canvas.drawText("分数:"+ Integer.toString(gettedscroe),screenWidth-200,70,paint);
            canvas.drawText("对手分数:"+ Integer.toString(othersscroe),5,70,paint);
        }

    }

    //网络相关
    //初始化客户端
    private void initCilent(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("尝试链接服务器");
                    s = new Socket("115.151.117.211", 5001);
                    System.out.println("连接成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    br =new BufferedReader(new InputStreamReader(s.getInputStream()));
                    os =s.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void receiveMessageFromServer()
    {
        InternetRunnable = new Runnable() {
            @Override
            public void run() {
                String message = null;
                System.out.println("执行了run");
                try {
                    if((message = br.readLine()) != null)
                    {
                        //如果接受到了消息
                        //解析该消息,并对船2进行移动
                        if(message.equals("left"))
                            dragonboat_other.Move(speed,screenWidth,0,riverWidth);
                        if(message.equals("right"))
                            dragonboat_other.Move(speed,screenWidth,screenWidth,riverWidth);

                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
                InternetHandler.postDelayed(this, 100);
            }
        };
    }

    private void sendMessageToServer(final int downX)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String message = null;
                    if(downX<dragonboat.getX())
                        message = "left";
                    if(downX>dragonboat.getX()+dragonboat.getWidth())
                        message = "right";
                    os.write((message + "\n")
                            .getBytes("utf-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}

