package com.fota.android.widget.btbwidget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *
 */
public class SearchEdittext extends FotaEditText {

    private final String TAG = "SearchEdittext";
    private Drawable drawableRight;
    private Drawable drawableLeft;
    private Rect rBounds;

    public SearchEdittext(Context paramContext) {
        super(paramContext);
        initEditText();
    }

    public SearchEdittext(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        initEditText();
    }

    public SearchEdittext(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        initEditText();
    }

    // 初始化edittext 控件
    private void initEditText() {
        setEditTextDrawable();
        addTextChangedListener(new TextWatcher() { // 对文本内容改变进行监听
            @Override
            public void afterTextChanged(Editable paramEditable) {
            }

            @Override
            public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
            }

            @Override
            public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
                SearchEdittext.this.setEditTextDrawable();
            }
        });
    }

    // 控制图片的显示
    public void setEditTextDrawable() {
        if (getText().toString().length() == 0) {
            setCompoundDrawablesWithIntrinsicBounds(this.drawableLeft, null, null, null);
        } else {
            if (isFocused()) {
                if (TextUtils.isEmpty(getText().toString())){}
                setCompoundDrawablesWithIntrinsicBounds(this.drawableLeft, null, this.drawableRight, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(this.drawableLeft, null, null, null);
            }

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.drawableRight = null;
        this.drawableLeft = null;
        this.rBounds = null;

    }

    /**
     * 添加触摸事件 点击之后 出现 清空editText的效果
     */
    @Override
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if ((this.drawableRight != null) && (paramMotionEvent.getAction() == 1)) {
            this.rBounds = this.drawableRight.getBounds();
            int i = (int) paramMotionEvent.getRawX();// 距离屏幕的距离
            // int i = (int) paramMotionEvent.getX();//距离边框的距离
            if (i > getRight() - 1.5 * this.rBounds.width() && isFocused()) {
                setText("");
                paramMotionEvent.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        return super.onTouchEvent(paramMotionEvent);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {

        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            setEditTextDrawable();
            // 此处为得到焦点时的处理内容
        } else {
            setCompoundDrawablesWithIntrinsicBounds(this.drawableLeft, null, null, null);
            // 此处为失去焦点时的处理内容
        }
    }

    /**
     * 显示右侧X图片的
     * <p>
     * 左上右下
     */
    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4) {
        if (paramDrawable3 != null)
            this.drawableRight = paramDrawable3;
        if (paramDrawable1 != null)
            this.drawableLeft = paramDrawable1;
        super.setCompoundDrawablesWithIntrinsicBounds(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
    }

}
