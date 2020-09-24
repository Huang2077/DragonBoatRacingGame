package com.example.a27560.the_final_test.Modul;

import android.graphics.Bitmap;
import android.graphics.Matrix;

//背景图片类
public class Background {
    private Bitmap _OldBack;//原背景图
    private  int _WIDTH ;//摄像机宽度
    private  int _HEIGHT;//摄像机高度
    private int _StartY ; //开始拍摄位置

    public Background(Bitmap _OldBack, int _WIDTH, int _HEIGHT, int _StartY) {
        this._OldBack = _OldBack;
        this._WIDTH = _WIDTH;
        this._HEIGHT = _HEIGHT;
        this._StartY = _StartY;
    }

    public Bitmap GetNewBack()
    {
        Bitmap NewBack = Bitmap.createBitmap(_OldBack,0, _StartY, _WIDTH, _HEIGHT);
        return NewBack;
    }
    public void Move(int speed)
    {
        if (_StartY < speed) {
            _StartY = _HEIGHT  ;
        } else
            _StartY -= speed;
    }
}
