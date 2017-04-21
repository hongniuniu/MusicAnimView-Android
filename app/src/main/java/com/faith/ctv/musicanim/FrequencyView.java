package com.faith.ctv.musicanim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页直播跳频动画
 */
public class FrequencyView extends View implements View.OnClickListener{

    private static final String TAG = "FrequencyView";
    private static final int LINE_ANIM_COUNT = 4; // 参与动画的竖线总数
    private int mShortLineHgt; // 最短的线的高度
    private int mLintToLineW; // 线和线之间的距离值
    private int mFrameNum; // 每帧增加的高度
    private static final int ANIM_DURTION = 120; // 动画刷新频率
    private Paint mPaint = new Paint();
    private int mStartX; // 线的起始横坐标
    private int mStartY; // 线的起始纵坐标
    private float mMaxLineStartY; // 最长线的起始y坐标
    private int mEndY; // 最底部y坐标
    private int mLeveNum = 0; // 当前频率等级

    // 申明集合用于存储直线对象
    private List<Line> mLines = new ArrayList<>();

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public FrequencyView(Context context) {
        this(context,null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p>
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public FrequencyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void logT(String log){
        Log.d(TAG,"震动频率-->" + log);
    }

    public int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void init(){
        mPaint.setColor(Color.parseColor("#FFF44336"));
        mPaint.setAntiAlias(true); // 抗锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(dp2px(2f)); // 画笔的粗细

        mLintToLineW = dp2px(10);
        mFrameNum = dp2px(9);
        mShortLineHgt = dp2px(20);

        setOnClickListener(this);
        initData();
    }

    private void initData() {
        logT("view的宽 = " + getWidth() + "-->view的高 = " + getHeight() + "-->leftPadding = " + getPaddingLeft() + "-->topPadding = "
                + getPaddingTop() + "-->rightPadding = " + getPaddingRight() + "-->bottomPadding = " + getPaddingBottom());
        mStartX = getPaddingLeft();
        mStartY = getPaddingTop();

        List<Line> lines = new ArrayList<>();
        mEndY = mStartY + mShortLineHgt;
        for (int i = 0; i < LINE_ANIM_COUNT; i++) { // 绘制首次的频率画面
            float startY = mEndY - mShortLineHgt * (i + 1) * 1 / 2;
            float startX = mStartX + i * mLintToLineW;
            Line line = new Line(startX, startY, mEndY);
            lines.add(line);
            logT("测试y = " + startY);
        }

        mMaxLineStartY = lines.get(3).startY;

        // 打乱集合顺序
        Line line1 = new Line();
        line1.x = lines.get(0).x;
        line1.startY = lines.get(2).startY;
        line1.startYs = new float[]{line1.startY + mFrameNum * 2, line1.startY + mFrameNum, line1.startY}; // 由小到大
        line1.stopY = lines.get(0).stopY;
        mLines.add(line1);

        Line line2 = new Line();
        line2.x = lines.get(1).x;
        line2.startY = lines.get(1).startY;
        line2.startYs = new float[]{line2.startY - mFrameNum * 2, line2.startY - mFrameNum, line2.startY}; // 由大到小
        line2.stopY = lines.get(1).stopY;
        mLines.add(line2);

        Line line3 = new Line();
        line3.x = lines.get(2).x;
        line3.startY = lines.get(3).startY;
        line3.startYs = new float[]{line3.startY + mFrameNum + mFrameNum * 1 / 2, line3.startY + mFrameNum + mFrameNum * 1 / 4, line3.startY}; // 由小到大
        line3.stopY = lines.get(2).stopY;
        mLines.add(line3);

        Line line4 = new Line();
        line4.x = lines.get(3).x;
        line4.startY = lines.get(0).startY;
        line4.startYs = new float[]{line4.startY - mFrameNum * 2, line4.startY - mFrameNum, line4.startY}; // 由大到小
        line4.stopY = lines.get(3).stopY;
        mLines.add(line4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getSize(true,widthMeasureSpec),getSize(false,heightMeasureSpec));
    }

    /**
     * 重新测量view的宽高
     * @param measureSpec
     * @return
     */
    private int getSize(boolean isWidth, int measureSpec) {
        int result = 0;
        // 获得测量模式
        int specMode = MeasureSpec.getMode(measureSpec);
        // 获得测量大小
        int specSize = MeasureSpec.getSize(measureSpec);
        // 重写模式执行逻辑
        switch (specMode) {
            case MeasureSpec.EXACTLY: // 精确值模式，手动指定了控件的宽高，或者指定match_parent
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED: // 任意大小，没有任何限制。情况使用较少
            case MeasureSpec.AT_MOST:// 手动指定控件的宽高为wrap_content，控件大小随着内容变化而变化，只要不超过父控件允许的最大尺寸即可
                int caclSize;
                if (isWidth) {
                    caclSize = getPaddingLeft() + getPaddingRight() + mLintToLineW * 3;
                } else {
                    caclSize = getPaddingTop() + getPaddingBottom() + (mEndY - (int) mMaxLineStartY);
                }
                logT("计算得出的尺寸 = " + caclSize);
                if (specMode == MeasureSpec.AT_MOST)
                    result = Math.min(caclSize, specSize);
                break;
        }
        return result;
    }

    private final class Line {

        public Line() {
        }

        public Line(float x, float startY, float stopY) {
            this.x = x;
            this.startY = startY;
            this.stopY = stopY;
        }

        public float x;
        public float startY;
        public float stopY;

        public float[] startYs;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0,getPaddingTop());
        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);
            canvas.drawLine(line.x, line.startYs[mLeveNum], line.x, line.stopY, mPaint);
        }
    }

    /**
     * 数组反转
     * @param array
     * @return
     */
    private float[] reverseArray(float[] array) {
        float[] new_array = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            // 反转后数组的第一个元素等于源数组的最后一个元素：
            new_array[i] = array[array.length - i - 1];
        }
        return new_array;
    }

    private Handler handler = new Handler( );

    private Runnable runnable = new Runnable( ) {
        public void run ( ) {
            switch (mLeveNum) {
                case 0:
                    mLeveNum = 1;
                    break;
                case 1:
                    mLeveNum = 2;
                    break;
                case 2:
                    mLeveNum = 0;
                    for (int i = 0; i < LINE_ANIM_COUNT; i++) {
                        Line line = mLines.get(i);
                        line.startYs = reverseArray(line.startYs);
                    }
                    break;
            }
            postInvalidate();
            handler.postDelayed(runnable,ANIM_DURTION);
        }
    };

    public void start(int delay) {
        stop();
        handler.postDelayed(runnable,delay);
    }

    public void stop() {
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start(600);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    public void onClick(View v) {
        logT("点击了view");
        if (mListener == null)
            return;
        mListener.onFreViewClick();
    }

    public OnFrequencyViewClickListener mListener;
    public void onClickSetFreViewListener(OnFrequencyViewClickListener listener) {
        mListener = listener;
    }
    public interface OnFrequencyViewClickListener{
        void onFreViewClick();
    }
}
