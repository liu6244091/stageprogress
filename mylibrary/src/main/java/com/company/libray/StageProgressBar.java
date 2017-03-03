package com.company.libray;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 阶段进度条
 * 作者：liuyuanqi on 17/3/2 18:57
 * 邮箱：liuyuanqi@eims.com.cn
 */

public class StageProgressBar extends View{

    //分成5等分
    private int count = 5;

    //画笔
    private Paint paint;

    //颜色
    private int color = Color.parseColor("#444444");

    //圆半径
    private float radius = dip2px(getContext(), 10);

    //当前进度
    private int curProgress = 0;


    public StageProgressBar(Context context) {
        super(context);
    }

    public StageProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StageProgressBar(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置最大空格数
     * @param maxCount
     */
    public void setMaxSpaceCount(int maxCount){
        count = maxCount;
        //最小1个单位
        if(count <= 0) {
            count = 1;
        }
        invalidate();
    }

    //设置当前进度
    public void setCurProgress(int progress){
        curProgress = progress;

        //限制最小边界
        if(curProgress < 0){
            curProgress = 0;
        }

        //设置最大边界
        if(curProgress > count){
            curProgress = count;
        }

        //刷新
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {

        //初始化画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        //画线
        drawLine(canvas);

        //画等分线
        drawCountLine(canvas);

        //画圆圈
        if(action == MotionEvent.ACTION_UP) {

            //画固定圈
            drawCirle(canvas);
        }else{

            //画移动圈
            drawMoveCircle(canvas);

        }

    }

    private float downX,downY, mTouchX, mTouchY;
    private int action = MotionEvent.ACTION_UP;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mTouchX = event.getX();
        mTouchY = event.getY();
        action = event.getAction();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = mTouchX;
                downY = mTouchY;

                //重新刷新
                invalidate();

                break;
            case MotionEvent.ACTION_MOVE:

                //重新刷新
                invalidate();

                break;
            case MotionEvent.ACTION_UP:

                //重新判断坐标点
                checkPoint(mTouchX, mTouchY);
                invalidate();

                //通知进度改变
                if(null != progressChanged){
                    progressChanged.onChanged(curProgress);
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                return false;
            default:
                break;
        }
        return true;
    }


    /**
     * 判断触摸点所在的坐标位置
     * @param x
     * @param y
     */
    private void checkPoint(float x, float y){

        float utils = (getWidth()-2*slideWidth)/count;
        float centerX, centerY;
        float pointX0 = getX(0);
        float pointY0 = getHeight()/2;

        if(mTouchX <= pointX0){
            //在最左边
            curProgress = 0;
            return;
        }


        for(int i=0; i<=count; i++){

            //圆心的位置
            centerX = (pointX0 + (i*utils));

            if(mTouchX >= centerX ){

                //触摸点在 坐标点右边
                curProgress = i;
            }else{

                //触摸点在 坐标点左边
                float halfUtils = utils/2;
                if(mTouchX > (centerX-halfUtils)){
                    //大过一半
                    curProgress = i;
                }

                //跳出循环
                break;
            }
        }

    }

    float drawX,drawY;
    /**
     * 画移动圈
     * @param canvas
     */
    private void drawMoveCircle(Canvas canvas){

        float pointX0 = getX(0);
        float pointXMax = getX(x_max);
        if(mTouchX >= pointX0 && mTouchX <= pointXMax){

            //沿着线，画圈
            float pointY0 = getHeight()/2;
            canvas.drawCircle(mTouchX, pointY0, radius, paint);
            drawX = mTouchX;
            drawY = pointY0;
        }else{

            canvas.drawCircle(drawX, drawY, radius, paint);
        }
    }


    /**
     * 画圆圈
     * @param canvas
     */
    private void drawCirle(Canvas canvas){

        float utils = (getWidth()-2*slideWidth)/count;
        float pointX0 = getX(0);
        float pointY0 = getHeight()/2;

        //每个单元格的长度
        float progress = curProgress;
        float x = (pointX0 + (progress*utils));
        float y = pointY0;

        canvas.drawCircle(x, y, radius, paint);


    }

    /**
     * 画分段线
     * @param canvas
     */
    private void drawCountLine(Canvas canvas){

        // 初始化画笔
        paint.setStrokeWidth(dip2px(getContext(), 1f));

        //每个单元格的长度
        float utils = (getWidth()-2*slideWidth)/count;
        float x1,y1,x2,y2;
        float pointX0 = getX(0);
        float pointY0 = getHeight()/2;
        for(int i=0; i<=count; i++){

            x1 = (pointX0 + (i*utils));
            y1 = pointY0-dip2px(getContext(), 3);
            x2 = x1;
            y2 = pointY0+dip2px(getContext(), 3);
            canvas.drawLine(x1, y1, x2, y2, paint);

        }

    }

    /**
     * 画线
     * @param canvas
     */
    private void drawLine(Canvas canvas){

        // 初始化画笔
        paint.setStrokeWidth(dip2px(getContext(), 1f));

        float x1 = getX(0);
        float y1 = getHeight()/2;
        float x2 = getX(x_max);
        float y2 = getHeight()/2;
        canvas.drawLine(x1, y1, x2, y2, paint);

    }

    /**
     * 获取x轴px坐标值
     * @param value
     * @return
     */
    private float getX(float value){

        //计算单位px
        if(xUtilsPx == 0){
            caculateUnit();
        }

        //得到之际的长度px值
        value = value*xUtilsPx;

        //长度加上x left margin
        value += slideWidth;

        return value;
    }



    private int slideWidth = 20;
    private int x_max = 100;
    private float xUtilsPx;

    /**
     * 计算长度单位值
     */
    private void caculateUnit(){
        int width = getWidth();
        int height = getHeight();

        //x轴，y轴值部分，像素最大值
        int xMax = width - (2*slideWidth);
        int yMax = height;

        //x轴，y轴物理单位最大值(比如， 24，300)
        int xValueMax = x_max;

        //x轴，y轴物理单位像素值
        xUtilsPx = (float)xMax / xValueMax;
    }

    public int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    //触发回调
    private IProgressChanged progressChanged;
    public void setProgressChanged(IProgressChanged progressChanged){
        this.progressChanged = progressChanged;
    }
    public interface IProgressChanged{
        public void onChanged(int progress);
    }



}
