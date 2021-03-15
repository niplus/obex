package com.fota.android.widget.popwin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fota.android.R;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.moudles.futures.FutureTopInfoBean;

/**
 * Created by Administrator on 2018/4/19.
 */

public class FutureTopWindow extends BasePopWindow {

    private Context context;
    private LayoutInflater inflater;
    String value;
    private TextView accountMargin;
    private TextView available;
    private TextView securityBorder;
    private TextView effectiveLever;
    //FtKeyValue selectItem;

    /**
     * 默认的popwindow
     *
     * @param context
     */
    public FutureTopWindow(Context context) {
        super(context);
        this.context = context;
        inflater = LayoutInflater.from(context);
        init();
    }


    private void init() {
        View view = inflater.inflate(R.layout.fragment_future_window_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(null);
        accountMargin = (TextView) view.findViewById(R.id.account_margin);
        available = (TextView) view.findViewById(R.id.available);
        securityBorder = (TextView) view.findViewById(R.id.security_border);
        effectiveLever = (TextView) view.findViewById(R.id.effective_lever);
    }


    public void showAsDropDown(View anchor, FutureTopInfoBean bean) {
        super.showAsDropDown(anchor);
        setData(bean);
    }

    public void setData(FutureTopInfoBean bean) {
        if (bean == null) {
            UIUtil.setText(accountMargin, "--");
            UIUtil.setText(available, "--");
            UIUtil.setText(securityBorder, "--");
            UIUtil.setText(effectiveLever, "--");
            return;
        }
        UIUtil.setText(accountMargin, bean.getAccountMargin());
        UIUtil.setText(available, bean.getAvailable());
        UIUtil.setText(securityBorder, bean.getSecurityBorder());
        UIUtil.setText(effectiveLever, bean.getEffectiveLever());
    }

    public Context getContext() {
        return context;
    }
}