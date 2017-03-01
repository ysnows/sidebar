package ysnows.ysnowssidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by xianguangjin on 16/6/1.
 * <p>
 * 自定义边栏滑动View
 */

public class SideBar extends View {

    private ArrayList<String> data;
    private Paint paint;
    private int measuredWidth;
    private int totalTextHeight;//文本总高度

    private int gap;

    private int tvColor;
    private int tvSelectedColor;
    private int selectedPos = -1;
    private int scaledTouchSlop;
    private OnSelectedListener onSelectedListener;
    private TextView tipView;//提示位置的TextView
    private int measuredHeight;
    private int remainningHeight;

    public SideBar(Context context) {
        this(context, null);

    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SideBar);
        gap = (int) attributes.getDimension(R.styleable.SideBar_tv_gap, dp2px(context, 5));
        int tvSize = (int) attributes.getDimension(R.styleable.SideBar_tv_size, dp2px(context, 15));
        tvColor = attributes.getColor(R.styleable.SideBar_tv_color, Color.WHITE);
        tvSelectedColor = attributes.getColor(R.styleable.SideBar_tv_selected_color, Color.BLUE);

        paint = new Paint();
        paint.setTextSize(tvSize);
        paint.setColor(tvColor);

        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        attributes.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();

        int dataHeight = getDataSize()[1];

        remainningHeight = 0;
        if (measuredHeight > dataHeight) {//设置的高度比数据高度高
            remainningHeight = (measuredHeight - dataHeight) / 2;
        }

        if (data != null) {
            totalTextHeight = 0;
            for (int i = 0; i < data.size(); i++) {
                String str = data.get(i);
                Rect bounds = new Rect();
                paint.getTextBounds(str, 0, str.length(), bounds);//获取文本所占用的Rect区域
                int startX = (measuredWidth - bounds.width() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();//文本开始的x坐标，加入了padding处理，并让文字始终是Horizontal方向居中
                if (i == 0) {
                    totalTextHeight = bounds.height() + getPaddingTop() + remainningHeight;
                } else {
                    totalTextHeight += (bounds.height() + gap);
                }
                paint.setColor(selectedPos == i ? tvSelectedColor : tvColor);
                canvas.drawText(str, startX, totalTextHeight, paint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        //处理wrap_content是的宽高
        if (heightMode == MeasureSpec.AT_MOST) {
            height = getDataSize()[1];
        }

        if (widthMode == MeasureSpec.AT_MOST) {
            width = getDataSize()[0] + getPaddingLeft() + getPaddingRight();
        }

        setMeasuredDimension(width, height);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float downY = 0;
        float moveY = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                setSelecteItem(event);
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = event.getY();
                if (moveY - downY > scaledTouchSlop) {
                    setSelecteItem(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                clearState();
                break;
        }
        return true;
    }

    /**
     * 回复初始值，清空状态
     */
    private void clearState() {
        selectedPos = -1;
        if (tipView != null) {
            tipView.setVisibility(GONE);
        }

        invalidate();
    }

    /**
     * 计算滑动的位置在哪一个文本上，并刷新UI
     *
     * @param event
     */

    private void setSelecteItem(MotionEvent event) {
        int height = getDataSize()[1] - getPaddingBottom() - getPaddingTop();
        int itemHeight = height / data.size();
        int newPos = (int) ((event.getY() - getPaddingTop() - remainningHeight) / itemHeight) - 1;
        if (newPos != selectedPos && newPos >= 0 && newPos < data.size()) {
            selectedPos = newPos;
            if (onSelectedListener != null) {
                onSelectedListener.onSelected(selectedPos);
                if (tipView != null) {
                    tipView.setVisibility(VISIBLE);
                    tipView.setText(data.get(selectedPos));
                }
            }
            invalidate();
        }
    }

    /**
     * 获取数据所占用的高度
     *
     * @return
     */
    private int[] getDataSize() {
        int width = 0;
        if (data != null) {
            totalTextHeight = 0;
            for (int i = 0; i < data.size(); i++) {
                String str = data.get(i);
                Rect bounds = new Rect();
                paint.getTextBounds(str, 0, str.length(), bounds);
                width = Math.max(bounds.width(), width);
                if (i == 0) {
                    totalTextHeight = bounds.height() + getPaddingTop();
                } else {
                    totalTextHeight += (bounds.height() + gap);
                }
            }
        }
        return new int[]{width, totalTextHeight + getPaddingBottom()};
    }


    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(ArrayList<String> data) {
        this.data = data;
        invalidate();
    }

    /**
     * 设置选中文本位置监听器
     *
     * @param onSelectedListener
     */
    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener {
        void onSelected(int position);
    }


    /**
     * 设置显示位置的TipView
     *
     * @param textView
     */
    public void attachTipView(TextView textView) {
        this.tipView = textView;
    }

    public int getTvSelectedColor() {
        return tvSelectedColor;
    }

    public void setTvSelectedColor(int tvSelectedColor) {
        this.tvSelectedColor = tvSelectedColor;
    }

    public int getTvColor() {
        return tvColor;
    }

    public void setTvColor(int tvColor) {
        this.tvColor = tvColor;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }


    /**
     * dp转px
     *
     * @param context
     * @param dip
     * @return
     */
    static public int dp2px(Context context, int dip) {
        return (int) (dip * getScreenDensity(context) + 0.5f);
    }


    static public float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getMetrics(dm);
            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }

}
