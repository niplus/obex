package com.fota.android.widget.btbwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;

import java.util.ArrayList;

/**
 * Created by Dell on 2018/4/19.
 */
@SuppressLint("AppCompatCustomView")
public class FotaEditText extends EditText {

    /**
     * 删除按钮的引用
     */
    private Drawable imgAble;
    private ArrayList<TextWatcher> mListeners;

    public FotaEditText(Context context) {
        this(context, null);
        init(context);
    }

    public FotaEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
        init(context);
    }

    public FotaEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(watcher);
        if (mListeners == null) {
            mListeners = new ArrayList<TextWatcher>();
        }
        mListeners.add(watcher);
    }

    /**
     * 偷懒
     *
     * @param text
     */
    public void setTextWithOutChanged(String text) {
        if (Pub.isListExists(mListeners)) {
            for (TextWatcher watcher : mListeners) {
                removeTextChangedListener(watcher);
            }
        }
        setText(text);
        if (Pub.isListExists(mListeners)) {
            for (TextWatcher watcher : mListeners) {
                super.addTextChangedListener(watcher);
            }
        }
    }


    protected void init(Context context) {
        setTypeface(null, Typeface.NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(null);
        } else {
            setBackgroundDrawable(null);
        }

        setHintTextColor(Pub.getColor(getContext(), R.attr.font_color5));
        setTextColor(Pub.getColor(getContext(), R.attr.font_color));

        if (getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)) {
            addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int len = s.toString().length();
                    if (len == 1 && s.toString().equals(".")) {
                        s.clear();
                    }
                    if (len == 2 && s.toString().equals("00")) {
                        setText("0");
                    }
                }
            });
        }
        this.setTextColor(Pub.getColor(context, R.attr.font_color));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //键盘头一次点总是不把输入框顶出来 会导致登录后键盘不能收起
//        KeyBoardUtils.openKeybord(this, getContext());
        if (imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 60;
            rect.top = rect.top - 15;
            rect.bottom = rect.bottom + 15;
            if (rect.contains(eventX, eventY))
                setText("");
        }
        return super.onTouchEvent(event);
    }
}

