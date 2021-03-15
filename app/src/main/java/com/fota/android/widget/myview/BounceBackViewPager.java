package com.fota.android.widget.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;

import androidx.viewpager.widget.ViewPager;

import com.fota.android.commonlib.utils.L;

/**
 * 边界回弹
 */
public class BounceBackViewPager extends ViewPager {

    private int currentPosition = 0;
    private Rect mRect = new Rect();//用来记录初始位置
    private boolean handleDefault = true;
    private float preX = 0f;
    private static final float RATIO = 0.8f;//摩擦系数
    private static final float SCROLL_WIDTH = 10f;

    private Bitmap bg;
    private Paint b = new Paint(1);
    DispatchDrawListener dispatchDrawListener = null;

    public void setBackGroud(Bitmap paramBitmap) {
        this.bg = paramBitmap;
        this.b.setFilterBitmap(true);
    }

    public void setDispatchDrawListener(DispatchDrawListener dispatchDrawListener) {
        this.dispatchDrawListener = dispatchDrawListener;
    }

    public BounceBackViewPager(Context context) {
        super(context);
    }

    public BounceBackViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            preX = ev.getX();//记录起点
            currentPosition = getCurrentItem();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                onTouchActionUp();
                break;
            case MotionEvent.ACTION_MOVE:
                if (getAdapter().getCount() == 1) {
                    float nowX = ev.getX();
                    float offset = nowX - preX;
                    preX = nowX;

                    if (offset > SCROLL_WIDTH) {//手指滑动的距离大于设定值
                        whetherConditionIsRight(offset);
                    } else if (offset < -SCROLL_WIDTH) {
                        whetherConditionIsRight(offset);
                    } else if (!handleDefault) {//这种情况是已经出现缓冲区域了，手指慢慢恢复的情况
                        if (getLeft() + (int) (offset * RATIO) != mRect.left) {
                            int left = getLeft() + (int) (offset * RATIO);
                            int right = getRight() + (int) (offset * RATIO);
                            L.a("layout(11  left = " + left + "  right = " + right);
                            layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());
                            if (dispatchDrawListener != null) {
                                dispatchDrawListener.moveOver1(left,right);
                            }
                        }
                    }
                } else if ((currentPosition == 0 || currentPosition == getAdapter().getCount() - 1)) {
                    float nowX = ev.getX();
                    float offset = nowX - preX;
                    preX = nowX;

                    if (currentPosition == 0) {
                        if (offset > SCROLL_WIDTH) {//手指滑动的距离大于设定值
                            whetherConditionIsRight(offset);
                        } else if (!handleDefault) {//这种情况是已经出现缓冲区域了，手指慢慢恢复的情况
                            if (getLeft() + (int) (offset * RATIO) >= mRect.left) {
                                int left = getLeft() + (int) (offset * RATIO);
                                int right = getRight() + (int) (offset * RATIO);
                                if (dispatchDrawListener != null) {
                                    dispatchDrawListener.moveOver2(left,right);
                                }
                                L.a("layout(22  left = " + left + "  right = " + right);
                                layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());

                            }
                        }
                    } else {
                        if (offset < -SCROLL_WIDTH) {
                            whetherConditionIsRight(offset);
                        } else if (!handleDefault) {
                            if (getRight() + (int) (offset * RATIO) <= mRect.right) {
                                int left = getLeft() + (int) (offset * RATIO);
                                int right = getRight() + (int) (offset * RATIO);
                                L.a("layout(33  left = " + left + "  right = " + right);
                                layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());
                                if (dispatchDrawListener != null) {
                                    dispatchDrawListener.moveOver3(left,right);
                                }
                            }
                        }
                    }
                } else {
                    handleDefault = true;
                }

                if (!handleDefault) {
                    return true;
                }
                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void whetherConditionIsRight(float offset) {
        if (mRect.isEmpty()) {
            mRect.set(getLeft(), getTop(), getRight(), getBottom());
        }
        handleDefault = false;
        int left = getLeft() + (int) (offset * RATIO);
        int right = getRight() + (int) (offset * RATIO);
        if (dispatchDrawListener != null) {
            dispatchDrawListener.moveOver4(left,right);
        }
        L.a("layout(44  left = " + left + "  right = " + right);
        layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());

    }

    private void onTouchActionUp() {
        if (!mRect.isEmpty()) {
            recoveryPosition();
        }
    }

    private void recoveryPosition() {
        TranslateAnimation ta = new TranslateAnimation(getLeft(), mRect.left, 0, 0);
        ta.setDuration(300);
        startAnimation(ta);
        int left = mRect.left;
        int right = mRect.right;
        L.a("layout(55  left = " + left + "  right = " + right);
        layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
        if (dispatchDrawListener != null) {
            dispatchDrawListener.moveOver5(left,right);
        }
        mRect.setEmpty();
        handleDefault = true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (dispatchDrawListener != null) {
            dispatchDrawListener.dispatchDraw(getScrollX());
        }

//        if (this.bg != null) {
//            int width = this.bg.getWidth();
//            int height = this.bg.getHeight();
//            int count = getAdapter().getCount();
//            int x = getScrollX();
//            L.a("dispatch x = " + x);
//            //子View中背景图片需要显示的宽度，放大背景图或缩小背景图。
//            int n = height * getWidth() / getHeight();
//            //(width - n) / (count - 1)表示除去显示第一个ViewPager页面用去的背景宽度，剩余的ViewPager需要显示的背景图片的宽度。
//            //getWidth()等于ViewPager一个页面的宽度，即手机屏幕宽度。在该计算中可以理解为滑动一个ViewPager页面需要滑动的像素值。
//            //((width - n) / (count - 1)) / getWidth()也就表示ViewPager滑动一个像素时，背景图片滑动的宽度。
//            //x * ((width - n) / (count - 1)) / getWidth()也就表示ViewPager滑动x个像素时，背景图片滑动的宽度。
//            //背景图片滑动的宽度的宽度可以理解为背景图片滑动到达的位置。
//            int totalWidth = (count - 1) * getWidth() * ((width - n) / (count - 1)) / getWidth();
//            int w = x * ((width - n) / (count - 1)) / getWidth();
////            canvas.drawBitmap(this.bg, new Rect(w, 0, n + w, height), new Rect(x, 0, x + getWidth(), getHeight()), this.b);
//            canvas.drawBitmap(this.bg, new Rect(totalWidth - w, 0, totalWidth - w + n, height), new Rect(x, 0, x + getWidth(), getHeight()), this.b);
//        }
        super.dispatchDraw(canvas);
    }

    public interface DispatchDrawListener {
        void dispatchDraw(int x);

        void moveOver1(int left, int right);

        void moveOver2(int left, int right);

        void moveOver3(int left, int right);

        void moveOver4(int left, int right);

        void moveOver5(int left, int right);
    }



}
