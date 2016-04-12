package com.viewsource.leef.testselfview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import com.viewsource.leef.testselfview.R;
import com.viewsource.leef.testselfview.util.Tools;

/**
 * 飞行统计圆形view
 */
public class CircleFlyView extends View {

    private float PAINTSTROKEWIDTH = 30.0f;
    private int LIGHT_WHITE_COLOR = 0xffE0F0DB;
    private int GRAY_COLOR = 0xFF9DCF8F;
    private int backgroundColor = 0xFF56aa43;

    private RectF backGroundRect;
    private RectF planeFlyRect;
    private RectF progressCircleRect;
    private TextPaint textPaint;

    private int infoY;
    private int titleY;
    private int infoSize;
    private int titleSize;
    private int bottomTextSize;
    private String info = "";

    private int sweepAngle;
    private int title;
    private String defaltTitle;

    private float percentage;

    /**
     * 圆环的起止位置 从270度开始一圈
     */
    private float startAngle = 270;
    private float endAngle = 630;

    private Bitmap bmpFlight;
    private Rect viewRect;
    private float radius;
    private float centerX;
    private float centerY;

    private Paint backgroundPaint;
    private Paint whiteThinPaint;
    private Paint scalePaint1;
    private Paint scalePaint2;
    private Paint whiteFatPaint;


    public CircleFlyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        PAINTSTROKEWIDTH = Tools.dip2px(context, 10);
        init();
    }

    public CircleFlyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public CircleFlyView(Context context) {
        this(context,null);
        init();
    }

    private void init() {
        bmpFlight = BitmapFactory.decodeResource(getResources(),
                R.drawable.white_plan_icon);

        whiteThinPaint = new Paint();
        whiteThinPaint.setStrokeWidth(PAINTSTROKEWIDTH);
        whiteThinPaint.setStrokeWidth(1);
        whiteThinPaint.setColor(LIGHT_WHITE_COLOR);
        whiteThinPaint.setStyle(Paint.Style.STROKE);

        scalePaint1 = new Paint();
        scalePaint1.setStrokeWidth(PAINTSTROKEWIDTH);
        scalePaint1.setColor(GRAY_COLOR);
        scalePaint1.setStrokeCap(Paint.Cap.SQUARE);
        scalePaint1.setStyle(Paint.Style.STROKE);

        scalePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint2.setStrokeWidth(PAINTSTROKEWIDTH);
        scalePaint2.setColor(backgroundColor);
        scalePaint2.setStrokeCap(Paint.Cap.SQUARE);
        scalePaint2.setStyle(Paint.Style.STROKE);

        whiteFatPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whiteFatPaint.setStrokeWidth(PAINTSTROKEWIDTH);
        whiteFatPaint.setColor(LIGHT_WHITE_COLOR);
        whiteFatPaint.setStrokeCap(Paint.Cap.SQUARE);
        whiteFatPaint.setStyle(Paint.Style.STROKE);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStrokeWidth(PAINTSTROKEWIDTH/3);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setStyle(Paint.Style.STROKE);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(backgroundColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }



    public int getSweepAngle() {
        return sweepAngle;
    }


    public void setSweepAngle(int sweepAngle) {
        this.sweepAngle = sweepAngle;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        radius = (int) (Math.min(getWidth(), getHeight()) - PAINTSTROKEWIDTH)/2;
        viewRect = new Rect(0, 0, getWidth(), getHeight());

        /**
         * view的最外层圆环
         */
        backGroundRect = new RectF(viewRect.centerX()-radius,
                PAINTSTROKEWIDTH/2,viewRect.centerX()+radius,PAINTSTROKEWIDTH/2+radius*2);
        float outpadding = PAINTSTROKEWIDTH * 2 + 5;
        /**
         * 小飞机飞行的圆环
         */
        planeFlyRect = new RectF(backGroundRect.left + outpadding,
                backGroundRect
                .top + outpadding, backGroundRect.right - outpadding,
                backGroundRect.bottom - outpadding);
        /**
         * 显示进度的圆环
         */
        progressCircleRect = new RectF(planeFlyRect.left + outpadding,
                planeFlyRect
                .top + outpadding, planeFlyRect.right - outpadding,
                planeFlyRect.bottom - outpadding);

        titleSize = (int) (radius/3);
        infoSize = (int) (radius/10);

        textPaint.setTextSize(titleSize);
        titleY = (int) (progressCircleRect.centerY() + getFontHeight
                (textPaint)/2);
        textPaint.setTextSize(infoSize);
        infoY = (int) (titleY + radius/5f);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drwaBackGroundCircle(canvas);

        drawLevel2Circle(canvas);
        drawLevel1CircleScale(canvas);
        drawLevel1Circle(canvas);

        drawText(canvas);
        drawPlane(canvas);
    }


    /**
     * 内部圆的刻度
     * @param canvas
     */
    private void drawLevel1CircleScale(Canvas canvas) {
        int i = 0;
        int internal  = 2;
        float beginAngle = 270;
        float angle1 = 270 ;
        float angle2 = 630;
        for (float angle = angle1 + internal; beginAngle < angle && angle <=
                angle2;
             angle
                     += internal){
            if (i % 2 == 0) {
                Log.d("CircleFlyView", "drawLevel2Circle" + "--i:" + i +
                        "--startAngel:" + beginAngle + "--angle:" + angle);
                canvas.drawArc(progressCircleRect, beginAngle, internal,
                        false,
                        scalePaint2);
            } else {
                Log.d("CircleFlyView", "drawLevel2Circle" + "--i:" + i +
                        "--beginAngle:" + beginAngle + "--angle:" + angle);
                canvas.drawArc(progressCircleRect, beginAngle, internal, false,
                        scalePaint1);
            }
            beginAngle = angle;
            i++;
        }
    }


    /**
     * 外边圆弧的小飞机
     * @param canvas
     */
    private void drawPlane(Canvas canvas) {

        centerX = planeFlyRect.centerX();
        centerY = planeFlyRect.centerY();
        radius = planeFlyRect.width() / 2;
        float degree = sweepAngle;
        Log.d("CircleView:",
                "centerX:" + centerX + "centerY:" + centerY + "radius:" +
                        radius + "sweepAngle:" + sweepAngle +
                        "degree:" +
                        degree);
        float x = centerX +  radius * (float)Math.sin(
                degree * Math.PI / 180);
        float y = centerY- radius * (float)Math.cos(degree* Math.PI / 180);
        Log.d("CircleView:", "x:" + x + "y:" + y);

        Matrix matrix = new Matrix();
        matrix.setRotate(degree, bmpFlight.getWidth()/2,
                bmpFlight.getHeight()/2);
        matrix.postTranslate(x - bmpFlight.getWidth()/2, y - bmpFlight
                .getHeight()/2);
        canvas.drawBitmap(bmpFlight, matrix, null);
    }


    public void startAnimation() {
        startProgressAnimation();
    }


    /**
     * 属性动画
     */
    private void startProgressAnimation() {
        android.animation.PropertyValuesHolder
                ph2 = android.animation.PropertyValuesHolder.ofInt(
                "sweepAngle",
                (int) (percentage * (endAngle - startAngle)));
        PropertyValuesHolder ph3 = PropertyValuesHolder.ofInt("title", 0, title);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(this,
                 ph2, ph3);
        animator.setDuration(3000);
        animator.start();
    }


    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
        invalidate();
    }


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    private void drawText(Canvas canvas) {
        textPaint.setTextSize(titleSize);
        textPaint.setColor(LIGHT_WHITE_COLOR);
        float textWidth;
        if (TextUtils.isEmpty(defaltTitle)) {
            canvas.drawText(String.valueOf(title) , progressCircleRect
                            .centerX
                            (),
                    titleY,
                    textPaint);
            textWidth = getFontWidth (textPaint, String.valueOf(title));
        } else {
            canvas.drawText(defaltTitle, progressCircleRect.centerX(), titleY,
                    textPaint);
            textWidth = getFontWidth (textPaint, defaltTitle);
        }
        textPaint.setTextSize(infoSize);
        Log.d("drawText","textWidth:" + textWidth);
        canvas.drawText("%", progressCircleRect.centerX() + textWidth - 10,
                titleY,
                textPaint);
        canvas.drawText(info, progressCircleRect.centerX(), infoY, textPaint);
    }

    private void drawLevel1Circle(Canvas canvas) {
        canvas.drawArc(progressCircleRect, startAngle, sweepAngle, false,
                whiteFatPaint);
    }

    private void drawLevel2Circle(Canvas canvas) {
        canvas.drawArc(planeFlyRect, startAngle, 360, false,
                whiteThinPaint);
    }

    private void drwaBackGroundCircle(Canvas canvas) {
        canvas.drawArc(backGroundRect, startAngle, 360, false, backgroundPaint);
    }

    public float getFontHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return Math.abs(fontMetrics.bottom + fontMetrics.ascent);
    }
    public float getFontWidth(Paint paint ,String str) {
        return paint.measureText(str);
    }


}
