package com.fota.android.widget.btbwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.fota.android.R;
import com.fota.android.commonlib.base.MyActivityManager;
import com.fota.android.commonlib.utils.Pub;

import java.util.Arrays;

/**
 * 解决英文词组显示不全自动换行问题，将每行显示满
 */
public class JustifyTextView extends TextView {

    public JustifyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JustifyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JustifyTextView(Context context) {
        super(context);
    }


    protected void onDraw(Canvas canvas) {
        TextPaint mPaint = getPaint();
        if (MyActivityManager.getInstance() != null)
            mPaint.setColor(Pub.getColor(MyActivityManager.getInstance().getCurrentActivity(), R.attr.font_color));
        Paint.FontMetrics fm = mPaint.getFontMetrics();

        float baseline = fm.descent - fm.ascent;
        float x = 0;
        float y = baseline;  //由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。

        String txt = getText().toString();

        //文本自动换行
        String[] texts = autoSplit(txt, mPaint, getWidth() - 5);

        System.out.printf("line indexs: %s\n", Arrays.toString(texts));

        for (String text : texts) {
            canvas.drawText(text, x, y, mPaint);  //坐标以控件左上角为原点
            y += baseline + fm.leading; //添加字体行间距
        }
    }

    /**
     * 自动分割文本
     *
     * @param content 需要分割的文本
     * @param p       画笔，用来根据字体测量文本的宽度
     * @param width   最大的可显示像素（一般为控件的宽度）
     * @return 一个字符串数组，保存每行的文本
     */
    private String[] autoSplit(String content, Paint p, float width) {
        int length = content.length();
        float textWidth = p.measureText(content);
        if (textWidth <= width) {
            return new String[]{content};
        }

        int start = 0, end = 1, i = 0;
        int lines = (int) Math.ceil(textWidth / width); //计算行数
        String[] lineTexts = new String[lines];
        while (start < length) {
            if (p.measureText(content, start, end) > width) { //文本宽度超出控件宽度时
                lineTexts[i++] = (String) content.subSequence(start, end);
                start = end;
            }
            if (end == length) { //不足一行的文本
                lineTexts[i] = (String) content.subSequence(start, end);
                break;
            }
            end += 1;
        }
        return lineTexts;
    }

}