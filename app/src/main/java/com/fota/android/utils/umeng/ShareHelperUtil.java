package com.fota.android.utils.umeng;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.fota.android.R;
import com.fota.android.app.FotaApplication;
import com.fota.android.commonlib.utils.ToastUitl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 分享工具类
 */
public class ShareHelperUtil {
    /**
     * 截取view
     **/
    public static Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    /**
     * @param bitmap 保存图片
     */
    public static void getBitmap(Bitmap bitmap, String name) {
        if (bitmap != null) {
            try {
                // 获取内置SD卡路径
                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                // 图片文件路径
                String filePath = sdCardPath + File.separator + name;
//                imageShare(filePath, 0);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 保存图片并插入图库
     *
     * @param context
     * @param bmp
     * @param dir     文件夹
     */
    public static void saveImageToGallery(Context context, Bitmap bmp, String dir) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), dir);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        // 最后通知图库更新
        String path = appDir + fileName;
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        ToastUitl.showShort(FotaApplication.getInstance().getResources().getString(R.string.commission_save_suc));
    }

    /**
     * 指定字体加粗  start
     *
     * @param textView
     * @param searchContent
     * @param fullStr
     */
    public static void checkSearchContent(TextView textView, String searchContent, String fullStr) {

        if (0 < searchContent.length() && fullStr.contains(searchContent)) {
            textView.setText(changeSearchContentStyle(fullStr, searchContent));
        } else {
            textView.setText(fullStr);
        }
    }

    public static SpannableStringBuilder changeSearchContentStyle(String content, String searchContent) {
        SpannableStringBuilder searchStyle = new SpannableStringBuilder();
        int start;
        while (content.contains(searchContent)) {
            start = content.indexOf(searchContent);
            searchStyle.append(getBoldSpannable(content.substring(0, start + searchContent.length()), searchContent));
            content = content.substring(start + searchContent.length());
        }
        searchStyle.append(content);
        return searchStyle;
    }

    private static SpannableStringBuilder getBoldSpannable(String content, String searchContent) {
        int start = content.indexOf(searchContent);
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        ssb.setSpan(new SearchStyleSpan(Typeface.NORMAL), start, start + searchContent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), start, start + searchContent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ssb;
    }//字体加粗  end


}
