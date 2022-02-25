package com.mactrixapp.www.stranger;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class myCustomView extends android.support.v7.widget.AppCompatImageView {


    private int circlecol, labelcol;
    private String circletext;
    private Paint circlepaint;
    RectF oval;
    Path path;
    int height;
    int width;

    public myCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        circlepaint = new Paint();
        oval= new RectF(5f,2f,6f,3f);
        path = new Path();

        // get the attributes specified in attrs.xml using  the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.myCustomView,0,0);

        try {
            // get the text and color specified using the names in attrs.xml
            circletext = a.getString(R.styleable.myCustomView_circleLabel);
            circlecol = a.getInteger(R.styleable.myCustomView_circleColor,0);
            labelcol = a.getInteger(R.styleable.myCustomView_labelColor,0);
        }finally {
            a.recycle();
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // get half of the width and height as we are working with circle
        int viewwidthhalf = getMeasuredWidth()/2;
        int viewheighthalf = getMeasuredHeight()/2;

        int radius = 0;
        if(viewwidthhalf > viewheighthalf){
            radius = viewheighthalf -10;

        }else{
            radius = viewwidthhalf - 10;
        }

        circlepaint.setAntiAlias(true);

        canvas.drawCircle(viewwidthhalf,viewheighthalf,radius,circlepaint);


       /**Draw a complex chat box using Path*/


       /* path.moveTo(2f*50,2f*50);
        path.lineTo(5f*50,2f*50);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            path.arcTo(5f*50,2f*50,6f*50,3f*50,45f,45f,true);
        }else{

            path.arcTo(oval,45f,45f);
        }

        path.lineTo(7f*50,4f*50);
        path.lineTo(2f*50,4f*50);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            path.arcTo(1f*50,3f*50,2f*50,4f*50,45f,45f,true);
            path.arcTo(1f*50,3f*50,2f*50,2f*50,45f,45f,true);
        }



        circlepaint.setStyle(Paint.Style.STROKE);
        circlepaint.setAntiAlias(true);

        circlepaint.setColor(circlecol);
        canvas.drawPath(path,circlepaint);*/



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        height = getMeasuredHeight();
        width = getMeasuredWidth();





    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    public int getCirclecol() {
        return circlecol;
    }

    public void setCirclecol(int circlecol) {
        // Update the instance variable
        this.circlecol = circlecol;

        // redraw the view
        invalidate();
        requestLayout();
    }

    public int getLabelcol() {
        return labelcol;
    }

    public void setLabelcol(int labelcol) {
        this.labelcol = labelcol;
        // redraw the view
        invalidate();
        requestLayout();
    }

    public String getCircletext() {
        return circletext;
    }

    public void setCircletext(String circletext) {
        this.circletext = circletext;

        // redraw the view
        invalidate();
        requestLayout();
    }
}
