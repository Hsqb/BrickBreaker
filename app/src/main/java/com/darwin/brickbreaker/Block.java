package com.darwin.brickbreaker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by HHAN on 2016. 5. 11..
 */
public class Block implements DrawableItem {
    private final float mTop;
    private final float mLeft;
    private final float mBottom;
    private final float mRight;
    private int   mHard;

    public Block(float top, float left, float bottom, float right){
        mTop = top;
        mBottom = bottom;
        mLeft = left;
        mRight = right;
        mHard = 1;
    }
    public void draw(Canvas canvas, Paint paint){
        if(mHard > 0){
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4f);
            canvas.drawRect(mLeft,mTop,mRight,mBottom,paint);
        }
    }
}
