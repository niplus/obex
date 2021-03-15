package com.fota.android.widget.popwin;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fota.android.R;
import com.fota.android.commonlib.utils.Pub;
import com.fota.android.commonlib.utils.UIUtil;
import com.fota.android.core.base.BaseDialog;
import com.fota.android.core.base.ft.FtKeyValue;
import com.fota.android.widget.recyclerview.EasyAdapter;
import com.fota.android.widget.recyclerview.RecyclerViewUtils;
import com.fota.android.widget.recyclerview.ViewHolder;

import java.util.List;

public class SelectItemDialog extends BaseDialog {

    private boolean isListStringMode;
    private String checkString;
    private String title;
    private RecyclerView keyList;
    private EasyAdapter adapter;
    private TextView tvTitle;


    public SelectItemDialog(Context context, String title, List list, FtKeyValue seletItem) {
        super(context);
        this.title = title;
        this.list = list;
        this.seletItem = seletItem;
    }

    public SelectItemDialog(Context context, String title, List list, String checkString) {
        super(context);
        this.title = title;
        this.list = list;
        this.checkString = checkString;
        isListStringMode = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_key_value);
        initView();
    }

    private void initView() {
        //忘记资金密码
        keyList = findViewById(R.id.key_list);
        tvTitle = findViewById(R.id.tv_title);
        //资金密码
        View outside = findViewById(R.id.outside);
        outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        View tvCancel = findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        initAdapter();
        RecyclerViewUtils.initRecyclerView(keyList, getContext());
        keyList.setAdapter(adapter);
        adapter.putList(list);
        UIUtil.setTextWithVisable(tvTitle, title);
    }

    List<FtKeyValue> list;

    FtKeyValue seletItem;


    private void initAdapter() {

        //ListString模式
        if (isListStringMode) {
            adapter = new EasyAdapter<String, ViewHolder>(getContext(), R.layout.item_simple_digital) {

                @Override
                public void convert(ViewHolder holder, final String model, int position) {
                    holder.setText(R.id.text1, model);
                    boolean isCheck = Pub.equals(model, checkString);


                    holder.setTextColor(R.id.text1, isCheck ? Pub.getColor(getContext(), R.attr.main_color)
                            : Pub.getColor(getContext(), R.attr.font_color4));
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listenerString != null) {
                                listenerString.onClick(model);
                            }
                            dismiss();
                        }
                    });
                }
            };
            return;
        }

        //ListKeyValue模式
        adapter = new EasyAdapter<FtKeyValue, ViewHolder>(getContext(), R.layout.item_simple_digital) {

            @Override
            public void convert(ViewHolder holder, final FtKeyValue model, int position) {
                holder.setText(R.id.text1, model.getKey());
                boolean isCheck = isEquals(model);
                holder.setTextColor(R.id.text1, isCheck ? Pub.getColor(getContext(), R.attr.main_color)
                        : Pub.getColor(getContext(), R.attr.font_color4));
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onClick(model);
                        }
                        dismiss();
                    }
                });
            }
        };
    }

    /**
     * 要显示的文字
     *
     * @param model
     * @return
     */
    protected String getText(FtKeyValue model) {
        return model.getKey();
    }

    /**
     * 是否相等
     *
     * @param model
     * @return
     */
    protected boolean isEquals(FtKeyValue model) {
        return Pub.equals(model.getValue(), seletItem == null ? null : seletItem.getValue());
    }

    OnSureClickListener listener;


    public void setListener(OnSureClickListener listener) {
        this.listener = listener;
    }

    public interface OnSureClickListener {

        void onClick(FtKeyValue model);
    }


    OnStringClickListener listenerString;

    public void setOnStringClickListener(OnStringClickListener listenerString) {
        this.listenerString = listenerString;
    }

    public interface OnStringClickListener {

        void onClick(String model);
    }


}