package com.zhuoxin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.zhuoxin.R;

import java.util.Timer;
import java.util.TimerTask;

public class CleanCircleView extends View {
    int width;
    int height;
    int currentAngle = 65;
    RectF oval;
    boolean isBack;
    public static boolean isRunning = false;

    public CleanCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > height) {
            width = height;
        } else {
            height = width;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画笔
        Paint paint = new Paint();
        //颜色
        paint.setColor(getResources().getColor(R.color.darkBluePrimaryColor, null));
        //抗锯齿
        paint.setAntiAlias(true);
        oval = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawArc(oval, -90, currentAngle, true, paint);
    }

    public void setTargetAngle(final int targetAngle) {

        isBack = true;
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (isBack) {
                    if (currentAngle > 0) {
                        currentAngle -= 4;
                    } else {
                        currentAngle = 0;
                        isBack = false;
                    }
                    postInvalidate();
                } else {
                    if (currentAngle < targetAngle) {
                        currentAngle += 4;
                    } else {
                        currentAngle = targetAngle;
                        isRunning = false;
                        timer.cancel();
                    }
                    postInvalidate();
                }
            }
        };
        timer.schedule(timerTask, 40, 40);
    }
}
