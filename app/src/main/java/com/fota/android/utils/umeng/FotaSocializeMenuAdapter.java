package com.fota.android.utils.umeng;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.shareboard.widgets.SocializePagerAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FotaSocializeMenuAdapter  extends SocializePagerAdapter {
    private List<SnsPlatform[][]> mPageData;
    private Context mContext;
    private FotaShareBoardMenuHelper mMenuHelper;

//    public FotaSocializeMenuAdapter(Context var1, FotaShareBoardConfig var2) {
//        this(var1, var2, null);
//    }

    public FotaSocializeMenuAdapter(Context var1, FotaShareBoardConfig var2, List<SHARE_MEDIA> f) {
        this.mPageData = new ArrayList();
        this.mContext = var1;
        this.mMenuHelper = new FotaShareBoardMenuHelper(var2);
        this.setData(f);
    }

    public void setData(List<SHARE_MEDIA> f) {
        if (f == null||f.size()<=0)
            return;
        List<SnsPlatform> var2 = setDisplayList(f);
        this.mPageData.clear();
        if (f != null) {
            this.mPageData.addAll(this.mMenuHelper.formatPageData(var2));
        }

        this.notifyDataSetChanged();
    }

    public int getCount() {
        return this.mPageData == null ? 0 : this.mPageData.size();
    }

    public boolean isViewFromObject(View var1, Object var2) {
        return var1 == var2;
    }

    public Object instantiateItem(ViewGroup var1, int var2) {
        View var3 = this.mMenuHelper.createPageLayout(this.mContext, (SnsPlatform[][])this.mPageData.get(var2));
        var1.addView(var3);
        return var3;
    }

    public void destroyItem(ViewGroup var1, int var2, Object var3) {
        var1.removeView((View)var3);
    }

    public List<SnsPlatform> setDisplayList(List<SHARE_MEDIA> f) {
        List<SnsPlatform> g = new ArrayList();
        Iterator var2 = f.iterator();

        while(var2.hasNext()) {
            SHARE_MEDIA var3 = (SHARE_MEDIA)var2.next();
            g.add(var3.toSnsPlatform());
        }

        return g;
    }
}
