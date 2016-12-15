package com.zhuoxin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.zhuoxin.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/11/14.
 */

public class PiechartView extends View {
    int backgroundColor;
    int color;
    int angle;

    public PiechartView(Context context, AttributeSet attrs) {//属性集合
        super(context, attrs);
        //取出自定义属性
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PiechartView, 0, 0);//类型列表=上下文.获得主题.获得主题属性（从attrs中获取，从自定义属性列表中获取，0，0）
        //循环取出列表中的数据
        int count = typedArray.getIndexCount();//获得列表索引长度
        for (int i = 0; i < count; i++) {
            int indexName = typedArray.getIndex(i);
            //判断当前的indexName属性是否和自定义属性比配
            switch (indexName) {
                case R.styleable.PiechartView_piechartBackgroundColor://找到自定义的背景颜色
                    backgroundColor = typedArray.getColor(indexName, Color.GREEN);//找到对应索引的颜色赋值背景颜色，若没有则默认为绿色
                    break;
                case R.styleable.PiechartView_piechartColor:
                    color = typedArray.getColor(indexName, Color.BLUE);
                    break;
                case R.styleable.PiechartView_piechartAngle:
                    angle = typedArray.getInt(indexName, 0);
                    break;
            }
        }
    }

    //2.测量该View的大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//包含类型（2位）数据（30位）
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获得真正的宽高数据，MeasureSpec测量规范
        int width = MeasureSpec.getSize(widthMeasureSpec);//获得宽度的具体数据（30位）
        int height = MeasureSpec.getSize(heightMeasureSpec);//获得高度的具体数据（30位）
        //因为是圆形的，所以把他的宽高约束为相等
        if (width > height) {
            width = height;
        } else {
            height = width;
        }
        //将获得的真正数据传递给系统
        setMeasuredDimension(width, height);//设置测量尺寸（宽度，高度）
    }

    //3.绘图（画布，画笔，画的区域）
    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        //1.定义画笔
        Paint paint = new Paint();//画笔
        paint.setColor(backgroundColor);
        paint.setAntiAlias(true);//设置了抗锯齿（边界显示圆滑）
        //2、定义绘画的区域
        RectF oval = new RectF(0, 0, getWidth(), getHeight());//设置矩形的大小（左，上，右，下）
        //3.绘画
        canvas.drawArc(oval, 0, 360, true, paint);//绘画背景，在画布中画画（形状大小，开始位置，扫过位置，用扫过的扇形true或半圆形false，传入画笔）
        paint.setColor(color);//设置前景颜色
        canvas.drawArc(oval, -90, angle, true, paint);//绘画前景
    }

    //新建一个显示饼状图动画的方法
    public void showPiechart(final int targetAngle) {
        angle = 0;//旋转角度从0开始
        final Timer timer = new Timer();//定时器
        TimerTask timerTask = new TimerTask() {//定时器任务栈
            @Override
            public void run() {
                angle += 4;//角度每次加4度
                if (angle > targetAngle) {
                    angle = targetAngle;
                    postInvalidate();//画面刷新
                    timer.cancel();//旋转结束，关闭定时器
                }
                postInvalidate();
            }
        };
        timer.schedule(timerTask, 40, 40);//开启定时器（定时器任务栈，动画持续时间，每次动画循环时间）
    }
}
