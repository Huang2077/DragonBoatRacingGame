package com.example.a27560.the_final_test.Modul;

import android.graphics.Bitmap;

/*灌木类*/
public class Shrub {
    private Bitmap first_bitmap;
    private Bitmap second_bitmap;
    private Bitmap return_bitmap;
    private  int x;
    private  int y;
    private int width;//图片宽度
    private int height;
    private int flag = 0;
    private int time;

    public Shrub(Bitmap first_bitmap, Bitmap second_bitmap, int x, int y, int width, int height,int time) {
        this.first_bitmap = Bitmap.createScaledBitmap(first_bitmap, width, height,false) ;
        this.second_bitmap = Bitmap.createScaledBitmap(second_bitmap, width, height,false) ;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.return_bitmap = first_bitmap;
        this.time = time;
    }
    //移动函数
    public void Move( int speed_y, int screenHight)
    {
        if(y >= screenHight-3)
        {
            y = -height;
        }
        y += speed_y;
    }

    public Bitmap getNowbitmap() {
        if(flag % time == 0)//每隔一次time换一次图片
        {
            if(return_bitmap != first_bitmap)
                return_bitmap = first_bitmap;
            else if(return_bitmap != second_bitmap)
                return_bitmap = second_bitmap;
        }
        flag++;

        return return_bitmap;
    }

    public int getX() {
        return x;
    }
    public int getY(){
        return y;
    }
}

