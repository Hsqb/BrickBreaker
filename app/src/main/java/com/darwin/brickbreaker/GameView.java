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

    static final int BLOCK_COUNT = 100;

    private Thread mThread;
    private ArrayList<DrawableItem> mBlockList;

    private Pad mPad;
    private float mPadHalfWidth;

    private Ball mBall;
    private float mBallRadius;

    private float mBlockWidth;
    private float mBlockHeight;

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

                while(true){
                    long startTime = System.currentTimeMillis();
                    synchronized (GameView.this) {
                        if(!mIsRunnable){
                            break;
                        }
                        Canvas canvas = lockCanvas();
                        if (canvas == null) {
                            continue;
                        }
                        canvas.drawColor(Color.BLACK);

                        float padLeft = mTouchedX - mPadHalfWidth;
                        float padRight = mTouchedX + mPadHalfWidth;
                        mPad.setLeftRight(padLeft, padRight);

                        mBall.move();
                        float ballTop = mBall.getY() - mBallRadius;
                        float ballLeft = mBall.getX() - mBallRadius;
                        float ballBottom = mBall.getY() + mBallRadius;
                        float ballRight = mBall.getX() + mBallRadius;
                        if(ballLeft <= 0 && mBall.getSpeedX() < 0 || ballRight >= getWidth() && mBall.getSpeedX() > 0){
                            mBall.setSpeedX(-mBall.getSpeedX());
                        }
                        if(ballTop < 0 ||ballBottom > getHeight()){
                            mBall.setSpeedY(-mBall.getSpeedY());
                        }

                        Block leftBlock   = getBlock(ballLeft, mBall.getY());
                        Block topBlock    = getBlock(mBall.getX(), ballTop);
                        Block rightBlock  = getBlock(ballRight, mBall.getY());
                        Block bottomBlock = getBlock(mBall.getX(), ballBottom);

                        if(leftBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            leftBlock.collision();
                        }
                        if(topBlock != null){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            topBlock.collision();
                        }
                        if(rightBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            rightBlock.collision();
                        }
                        if(bottomBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            bottomBlock.collision();
                        }

                        float padTop = mPad.getTop();
                        float ballSpeedY = mBall.getSpeedY();

                        if( ballBottom > padTop && (ballBottom - ballSpeedY) < padTop && padLeft < ballRight && padRight > ballLeft){
                            if(ballSpeedY < mBlockHeight / 3){
                                ballSpeedY *= -1.05f;
                            }else {
                                ballSpeedY = -ballSpeedY;
                            }
                            float ballSpeedX = mBall.getSpeedX() + ((mBall.getX() - mTouchedX) / 10);
                            if(ballSpeedX > mBlockWidth / 5){
                                ballSpeedX = mBlockWidth / 5;
                            }
                            mBall.setSpeedY(ballSpeedY);
                            mBall.setSpeedX(ballSpeedX);
                        }




                        for (DrawableItem item : mBlockList) {
                            item.draw(canvas, paint);
                        }
                        unlockCanvasAndPost(canvas);
                    }
                    long sleepTime = 16 - (System.currentTimeMillis()) + startTime;
                    if(sleepTime > 0){
                        try{
                            Thread.sleep(sleepTime);
                        }catch(InterruptedException e){

                        }
                    }
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

        mPad = new Pad(height * 0.8f, height * 0.85f);
        mPadHalfWidth = width / 10;

        mBlockWidth = width / 10;
        mBlockHeight = height / 20;

        mBallRadius = width < height ? width/40 : height/40;
        mBall = new Ball(mBallRadius, width/2, height/2);

        mBlockList = new ArrayList<DrawableItem>();
        for(int i = 0 ; i < BLOCK_COUNT ; i++){
            float blockTop = i / 10 * mBlockHeight;
            float blockLeft = i % 10 * mBlockWidth;
            float blockBottom = blockTop + mBlockHeight;
            float blockRight = blockLeft + mBlockWidth;
            mBlockList.add(new Block(blockTop,blockLeft,blockBottom,blockRight));
        }
        mBlockList.add(mPad);
        mBlockList.add(mBall);

    }
    public Block getBlock(float x , float y){
        int index = (int) (x / mBlockWidth) + (int) (y/mBlockHeight) * 10;
        if( 0 <= index && index < BLOCK_COUNT){
            Block block = (Block) mBlockList.get(index);
            if(block.isExist()){
                return block;
            }
        }
        return null;
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
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        synchronized (this) {
            return true;
        }
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
