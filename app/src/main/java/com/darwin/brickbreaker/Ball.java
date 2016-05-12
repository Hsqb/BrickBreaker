package com.darwin.brickbreaker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by HHAN on 2016. 5. 12..
 */
public class Ball implements DrawableItem {
    private float mX;
    private float mY;
    private float mSpeedX;
    private float mSpeedY;
    private final float mRadius;

    public  Ball(float radius, float initalX, float initalY){
        mRadius = radius;
        mSpeedX = radius/5;
        mSpeedY = -radius/5;
        mX = initalX;
        mY = initalY;
    }

    public void move(){
        mX += mSpeedX;
        mY += mSpeedY;
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mX, mY, mRadius, paint);
    }

}
