package com.fota.android.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.fota.android.R;
import com.fota.android.app.ConstantsPage;
import com.fota.android.app.IntentExtra;
import com.fota.android.common.bean.home.NoticeBean;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.core.base.SimpleFragmentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 上下滚动textview
 */
public class TextSwitcherView extends TextSwitcher implements ViewSwitcher.ViewFactory {
    private List<NoticeBean> reArrayList = new ArrayList<>();
    private int resIndex = 0;
    private final int UPDATE_TEXTSWITCHER = 1;
    private int timerStartAgainCount = 0;
    private Context mContext;
    private boolean isStart = true;

    public TextSwitcherView(Context context) {

        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
        if (this.reArrayList != null && this.reArrayList.size() > 0) {
            this.setText(this.reArrayList.get(0).getTextTitle());
        }
        init();
    }

    public TextSwitcherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
        // TODO Auto-generated constructor stub
    }

    private void init() {
        this.setFactory(this);
        this.setInAnimation(getContext(), R.anim.textswitch_vertical_in);
        this.setOutAnimation(getContext(), R.anim.textswitch_vertical_out);
        Timer timer = new Timer();
        timer.schedule(timerTask, 1, 5000);
    }

    public void pause() {
        isStart = false;
    }

    public void start() {
        isStart = true;
    }

    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {   //不能在这里创建任何UI的更新，toast也不行
            // TODO Auto-generated method stub
            Message msg = new Message();
            msg.what = UPDATE_TEXTSWITCHER;
            handler.sendMessage(msg);
        }
    };
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXTSWITCHER:
                    if (reArrayList != null && reArrayList.size() > 1)
                        updateTextSwitcher();

                    break;
                default:
                    break;
            }

        }

        ;
    };

    /**
     * 需要传递的资源
     *
     * @param reArrayList
     */
    public void getResource(List<NoticeBean> reArrayList) {
        this.reArrayList = reArrayList;
        resIndex = 0;
        updateTextSwitcher();
    }

    public void updateTextSwitcher() {
        if (!isStart)
            return;
        if (this.reArrayList != null && this.reArrayList.size() > 0) {

            resIndex++;
            if (resIndex > this.reArrayList.size() - 1) {
                resIndex = 0;
            }
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(reArrayList.get(resIndex).getHyperlink())) {
//                        ToastUtils.showShort(mContext.getResources().getString(R.string.url_none));
                        return;
                    }

                    Bundle bundle = new Bundle();
//                    ToastUtils.showShort(reArrayList.get(resIndex).getTextTitle());
                    bundle.putString(IntentExtra.DATA, reArrayList.get(resIndex).getHyperlink());
                    SimpleFragmentActivity.gotoFragmentActivity(getContext(), ConstantsPage.WebPowerfulFragment, bundle);
                }
            });
            this.setText(this.reArrayList.get(resIndex).getTextTitle());

        }

    }

    @Override
    public View makeView() {
        // TODO Auto-generated method stub
        TextView tView = new TextView(getContext());
        tView.setTextSize(12);
        tView.setSingleLine();
        tView.setTextColor(Pub.getColor(getContext(), R.attr.font_color));
        tView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        return tView;
    }
}
