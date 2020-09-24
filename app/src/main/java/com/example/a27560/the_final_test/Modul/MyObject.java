package com.example.a27560.the_final_test.Modul;
import android.graphics.Bitmap;
import java.util.Random;
/*粽子和石头类*/
public class MyObject {
    private  Bitmap bitmap;
    private  int x;
    private  int y;
    private int width;//图片宽度
    private int height;
    private  int score;//每个物体所代表的分数

    public MyObject(Bitmap bitmap, int x, int y, int width, int height, int score) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.score = score;
        //要根据原图进行缩放
        this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height,false) ;
    }

    //移动函数，形参有纵向速度，和屏幕宽度
    public void Move( int speed_y, int screenHight)
    {
        if(y >= screenHight-3)
        {
            y = -height;
        }
        y += speed_y;
    }
    public void ReSet(int screenHight)
    {
        y = -(height+(screenHight-y));
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


}
