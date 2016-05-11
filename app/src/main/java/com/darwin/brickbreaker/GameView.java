package com.darwin.brickbreaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by HHAN on 2016. 5. 11..
 * 게임화면을 그리기위한 텍스쳐
 */
public class GameView extends TextureView implements TextureView.SurfaceTextureListener, View.OnTouchListener{
    /**
     * 슈퍼클래스에 기본생성자가 없으므로 인수가 있는 생성자를 명시적 호출한다.
     */

    private Thread mThread;
    private ArrayList<Block> mBlockList;
    volatile private boolean mIsRunnable;
    volatile private float mTouchedX;
    volatile private float mTouchedY;

    public GameView(Context context){
        super(context);
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
    }

    public void start(){
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.BLUE);

                while(mIsRunnable){
                    Canvas canvas = lockCanvas();
                    if(canvas == null){
                        continue;
                    }
                    canvas.drawColor(Color.BLACK);
                    for(Block item : mBlockList){
                        item.draw(canvas, paint);
                    }
                    unlockCanvasAndPost(canvas);
                }
            }
        });

        mIsRunnable = true;
        mThread.start();
    }
    public void stop(){
        mIsRunnable = false;

    }

    public void readyObjects(int width, int height){
        float blockWidth = width / 10;
        float blockHeight = height / 20;
        mBlockList = new ArrayList<Block>();
        for(int i = 0 ; i < 100 ; i++){
            float blockTop = i / 10 * blockHeight;
            float blockLeft = i % 10 * blockWidth;
            float blockBottom = blockTop + blockHeight;
            float blockRight = blockLeft + blockWidth;
            mBlockList.add(new Block(blockTop,blockLeft,blockBottom,blockRight));
        }

    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        readyObjects(width,height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        readyObjects(width,height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mTouchedX = event.getX();
        mTouchedY = event.getY();
        return true;
    }
}
