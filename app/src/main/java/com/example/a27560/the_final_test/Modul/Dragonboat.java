package com.example.a27560.the_final_test.Modul;

import android.graphics.Bitmap;

public class Dragonboat {
    private Bitmap bitmap;
    private int x;//图片位置
    private int y;
    private int width;//图片宽度
    private int height;

    public Dragonboat(Bitmap bitmap, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        //要根据原图进行缩放
        this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height,false) ;;
    }

    //龙舟的移动函数,行参有横向速度，手机屏幕宽度，手触摸屏幕位置，河道宽度
    public void Move(int speed_x,int screenWidth,int downX,int riverWidth)
    {
        if(downX<x && x>(screenWidth-riverWidth)/2)
            x -= speed_x;
        if(downX>x+width && x+width<(screenWidth+riverWidth)/2)
            x += speed_x;
    }

    //碰撞检测函数
    public boolean iscrash(MyObject myObject)
    {
        int x2 = myObject.getX();
        int y2 = myObject.getY();
        int width2 = myObject.getWidth();
        int height2 = myObject.getHeight();
     if(x2>x+width || x2+width2<x || y2+height2<y || y2>y+height )
         return false;
         else
             return true;
    }
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

}
