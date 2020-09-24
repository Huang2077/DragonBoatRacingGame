package com.example.a27560.the_final_test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Button Main_Button1;
    private Button Main_Button2;
    private Button Main_Button3;
    private Button Main_Exit;
    private Button Main_info;
    private Button Main_help;
    private Button Main_internet;
    private SeekBar Main_seekBar;
    private MediaPlayer mediaPlayer;
    private float volume = 1;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //播放音乐
        mediaPlayer = MediaPlayer.create(this,R.raw.mainbgm);
        mediaPlayer.start();
        //监听音频播放完的代码，实现音频的自动循环播放
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });

        //改变音量大小
         Main_seekBar = (SeekBar)findViewById(R.id.Main_seekBar);
         Main_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                 volume = (float)(progress)/50;
                 mediaPlayer.setVolume(volume,volume);
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {
               mediaPlayer.setVolume(volume,volume);
             }
         });

        //难度1
        Main_Button1 = (Button)findViewById(R.id.Main_min);
        Main_Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Game.class);
                Bundle data = new Bundle();
                data.putSerializable("level",4);
                data.putSerializable("volume",volume);
                intent.putExtras(data);
                startActivity(intent);
                //finish();
            }
        });
        //难度2
        Main_Button2 = (Button)findViewById(R.id.Main_mid);
        Main_Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Game.class);
                Bundle data = new Bundle();
                data.putSerializable("level",8);
                data.putSerializable("volume",volume);
                intent.putExtras(data);
                startActivity(intent);
               // finish();
            }
        });
        //难度3
        Main_Button3 = (Button)findViewById(R.id.Main_max);
        Main_Button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Game.class);
                Bundle data = new Bundle();
                data.putSerializable("level",12);
                data.putSerializable("volume",volume);
                intent.putExtras(data);
                startActivity(intent);
               // finish();
            }
        });
        //网络对战
        Main_internet = (Button)findViewById(R.id.Main_internet);
        Main_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,InternetGame.class);
                Bundle data = new Bundle();
                data.putSerializable("level",8);
                data.putSerializable("volume",volume);
                intent.putExtras(data);
                startActivity(intent);
                // finish();
            }
        });
        //退出游戏
        Main_Exit = (Button)findViewById(R.id.Main_exit);
        Main_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.release();
                finish();
            }
        });

        //游戏帮助
        Main_help = (Button)findViewById(R.id.Main_help);
        Main_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个对话框
                Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                        //设置信息
                          .setTitle("                            游戏帮助")
                        .setMessage("1: 点击左右屏幕控制龙舟移动\n"+
                                    "2: 吃到粽子+2分\n"+
                                    "3: 碰到石头-1分\n"+
                                    "4: 获得100分胜利,少于0分失败\n"+
                                    "5: 网络版谁先获得100分胜利或者谁先少于0分失\n    败")
                        //设置对话框的按钮
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        });
        Main_info = (Button)findViewById(R.id.Main_info);
        Main_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个对话框
                Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                        //设置信息
                        .setTitle("                            开发者信息")
                        .setMessage("1.姓名: 黄敬滔\n"+
                                "2.班级: 数字媒体技术151班\n"+
                                "3.学号: 6103315001\n")
                        //设置对话框的按钮
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
