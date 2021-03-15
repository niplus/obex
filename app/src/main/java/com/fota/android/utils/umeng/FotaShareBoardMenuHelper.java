package com.fota.android.utils.umeng;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.common.ResContainer;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.shareboard.SocializeImageView;
import com.umeng.socialize.utils.SLog;
import com.umeng.socialize.utils.UmengText;

import java.util.ArrayList;
import java.util.List;

public class FotaShareBoardMenuHelper {
    private static String TAG = FotaShareBoardMenuHelper.class.getSimpleName();
    private FotaShareBoardConfig mShareBoardConfig;

    public FotaShareBoardMenuHelper(FotaShareBoardConfig var1) {
        this.mShareBoardConfig = var1;
    }

    public List<SnsPlatform[][]> formatPageData(List<SnsPlatform> var1) {
        int var2 = this.mShareBoardConfig.mMenuColumnNum;
        int var3 = var1.size();
        ArrayList var4 = new ArrayList();
        int var6;
        if (var3 < this.mShareBoardConfig.mMenuColumnNum) {
            SnsPlatform[][] var15 = new SnsPlatform[1][var3];

            for(var6 = 0; var6 < var1.size(); ++var6) {
                var15[0][var6] = (SnsPlatform)var1.get(var6);
            }

            var4.add(var15);
            return var4;
        } else {
            int var5 = var3 / var2;
            var6 = -1;
            int var7 = var3 % var2;
            if (var7 != 0) {
                var6 = var7 / this.mShareBoardConfig.mMenuColumnNum + (var7 % this.mShareBoardConfig.mMenuColumnNum != 0 ? 1 : 0);
                ++var5;
            }

            int var8;
            for(var8 = 0; var8 < var5; ++var8) {
                int var10;
                if (var8 == var5 - 1 && var6 != -1) {
                    var10 = var6;
                } else {
                    var10 = 1;
                }

                SnsPlatform[][] var9 = new SnsPlatform[var10][this.mShareBoardConfig.mMenuColumnNum];
                var4.add(var9);
            }

            var8 = 0;

            for(int var16 = 0; var16 < var4.size(); ++var16) {
                SnsPlatform[][] var17 = (SnsPlatform[][])var4.get(var16);
                int var11 = var17.length;

                for(int var12 = 0; var12 < var11; ++var12) {
                    SnsPlatform[] var13 = var17[var12];

                    for(int var14 = 0; var14 < var13.length; ++var14) {
                        if (var8 < var3) {
                            var13[var14] = (SnsPlatform)var1.get(var8);
                        }

                        ++var8;
                    }
                }
            }

            return var4;
        }
    }

    public View createPageLayout(Context var1, SnsPlatform[][] var2) {
        LinearLayout var3 = new LinearLayout(var1);
        var3.setOrientation(LinearLayout.VERTICAL);
        var3.setGravity(48);
        LinearLayout.LayoutParams var4 = new LinearLayout.LayoutParams(-1, -2);
        var3.setLayoutParams(var4);

        for(int var5 = 0; var5 < var2.length; ++var5) {
            SnsPlatform[] var6 = var2[var5];
            View var7 = this.createRowLayout(var1, var6, var5 != 0);
            var3.addView(var7);
        }

        return var3;
    }

    private View createRowLayout(Context var1, SnsPlatform[] var2, boolean var3) {
        LinearLayout var4 = new LinearLayout(var1);
        var4.setOrientation(LinearLayout.HORIZONTAL);
        var4.setGravity(1);
        LinearLayout.LayoutParams var5 = new LinearLayout.LayoutParams(-1, -2);
        if (var3) {
            var5.topMargin = this.dip2px(var1, 20.0F);
        }

        var4.setLayoutParams(var5);

        for(int var6 = 0; var6 < var2.length; ++var6) {
            View var7 = this.createBtnView(var1, var2[var6]);
            var4.addView(var7);
        }

        return var4;
    }

    private View createBtnView(Context var1, final SnsPlatform var2) {
        LinearLayout var3 = new LinearLayout(var1);
        LinearLayout.LayoutParams var4 = new LinearLayout.LayoutParams(0, -2);
        var4.weight = 1.0F;
        var3.setLayoutParams(var4);
        var3.setGravity(17);
        if (var2 != null) {
            ResContainer var5 = ResContainer.get(var1);
            View var6 = LayoutInflater.from(var1).inflate(var5.layout("socialize_share_menu_item"), (ViewGroup)null);
            SocializeImageView var7 = (SocializeImageView)var6.findViewById(var5.id("socialize_image_view"));
            TextView var8 = (TextView)var6.findViewById(var5.id("socialize_text_view"));
            if (this.mShareBoardConfig.mMenuBgColor != 0 && this.mShareBoardConfig.mMenuBgShape != ShareBoardConfig.BG_SHAPE_NONE) {
                var7.setBackgroundColor(this.mShareBoardConfig.mMenuBgColor, this.mShareBoardConfig.mMenuBgPressedColor);
                var7.setBackgroundShape(this.mShareBoardConfig.mMenuBgShape, this.mShareBoardConfig.mMenuBgShapeAngle);
            } else {
                var7.setPadding(0, 0, 0, 0);
            }

            if (this.mShareBoardConfig.mMenuIconPressedColor != 0) {
                var7.setPressedColor(this.mShareBoardConfig.mMenuIconPressedColor);
            }

            String var9 = "";

            try {
                var9 = var2.mShowWord;
            } catch (Exception var15) {
                SHARE_MEDIA var11 = var2.mPlatform;
                String var12 = var11 == null ? "" : var11.toString();
                SLog.error(UmengText.SHAREBOARD.NULLNAME + var12, var15);
            }

            if (!TextUtils.isEmpty(var9)) {
                var8.setText(var2.mShowWord);
            }

            var8.setGravity(17);
            int var10 = 0;

            try {
                var10 = ResContainer.getResourceId(var1, "drawable", var2.mIcon);
            } catch (Exception var14) {
                SHARE_MEDIA var16 = var2.mPlatform;
                String var13 = var16 == null ? "" : var16.toString();
                SLog.error(UmengText.SHAREBOARD.NULLNAME + var13, var14);
            }

            if (var10 != 0) {
                var7.setImageResource(var10);
            }

            if (this.mShareBoardConfig.mMenuTextColor != 0) {
                var8.setTextColor(this.mShareBoardConfig.mMenuTextColor);
            }

            var6.setOnClickListener(new View.OnClickListener() {
                public void onClick(View var1) {
                    SHARE_MEDIA var2x = var2.mPlatform;
                    if (FotaShareBoardMenuHelper.this.mShareBoardConfig != null && FotaShareBoardMenuHelper.this.mShareBoardConfig.getShareBoardlistener() != null) {
                        FotaShareBoardMenuHelper.this.mShareBoardConfig.getShareBoardlistener().onclick(var2, var2x);
                    }

                }
            });
            var3.addView(var6);
        }

        return var3;
    }

    private int dip2px(Context var1, float var2) {
        float var3 = var1.getResources().getDisplayMetrics().density;
        return (int)(var2 * var3 + 0.5F);
    }
}

