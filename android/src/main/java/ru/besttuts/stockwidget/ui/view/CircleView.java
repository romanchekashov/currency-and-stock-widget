package ru.besttuts.stockwidget.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import static ru.besttuts.stockwidget.util.LogUtils.LOGD;
import static ru.besttuts.stockwidget.util.LogUtils.makeLogTag;

/**
 * Created by roman on 25.01.2015.
 */
public class CircleView extends View {

    private static final String TAG = makeLogTag(CircleView.class);

    Paint mPaint;
    private int color;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        //get half of the width and height as we are working with a circle
        int viewWidthHalf = this.getMeasuredWidth()/2;
        int viewHeightHalf = this.getMeasuredHeight()/2;

        //get the radius as half of the width or height, whichever is smaller
        //subtract ten so that it has some space around it
        int radius = 0;
        if(viewWidthHalf>viewHeightHalf) {
            radius=viewHeightHalf-10;
        } else {
            radius=viewWidthHalf-10;
        }

        mPaint.setColor(color);

        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, mPaint);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
