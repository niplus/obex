package com.fota.android.widget.popwin;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fota.android.R;

import java.util.List;

public class DigitalPopupWindow extends BasePopWindow {
    private Context context;
    private List<String> datas;
    private ListView listView;
    private DigitalAdapter adapter;
    private DigitalInterface digitalInterface;

    public DigitalPopupWindow(Context context, List<String> datas) {
        super(context);
        this.context = context;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        setFocusable(true);
        setOutsideTouchable(false);
        setClippingEnabled(false);

        ColorDrawable dw = new ColorDrawable(0);
        setBackgroundDrawable(dw);
        this.datas = datas;
    }

    public void setPopView(View view, DigitalInterface digitalInter) {
        listView = view.findViewById(R.id.list_digital);
        initListView();
        this.digitalInterface = digitalInter;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setContentView(view);
    }

    private void initListView() {
        adapter = new DigitalAdapter();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    class DigitalAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public DigitalAdapter() {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_simple_digital, null);
            }
            final TextView textView = (TextView) convertView.findViewById(R.id.text1);
            textView.setText((String) getItem(position));
//            if(position == getCount() - 1) {
//                View divider = convertView.findViewById(R.id.view_digital_divider);
//                divider.setVisibility(View.GONE);
//            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    digitalInterface.digitalChange((String) getItem(position));
                    dismiss();
                }
            });
            return convertView;
        }
    }

    public interface DigitalInterface {

        void digitalChange(String digital);

    }
}
