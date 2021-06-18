package com.fota.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;


public class BitmapUtils {

    /**
     * 回收Bitmap
     *
     * @param bitmap Bitmap
     */
    public static void recycle(Bitmap bitmap) {
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
        }
    }
    public static Bitmap shotWebView(WebView webView) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Android5.0以上
                float scale = webView.getScale();
                int width = webView.getWidth();
                int height = (int) (webView.getContentHeight() * scale + 0.5);
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                webView.draw(canvas);

                // 保存图片
                return bitmap;
            } else {
                // Android5.0以下
                Picture picture = webView.capturePicture();
                int width = picture.getWidth();
                int height = picture.getHeight();
                if (width > 0 && height > 0) {
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    picture.draw(canvas);

                    return bitmap;
                }
                return null;
            }
        } catch (OutOfMemoryError oom) {
            return null;
        }
    }
    /**
     * 获取Bitmap
     *
     * @param view view
     * @return Bitmap
     */
    public static Bitmap getBitmap(View view) {
        int width = view.getWidth();
        int height = view.getHeight();

        // getDrawingCache()获取Bitmap方法
//        view.setDrawingCacheEnabled(true);
//        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
//        view.destroyDrawingCache();
//        view.setDrawingCacheEnabled(false);

        // draw(canvas)获取Bitmap方法
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    /**
     * 获取Bitmap
     *
     * @param file file
     * @return Bitmap
     */
    public static Bitmap getBitmap(File file) {
        if (file == null) {
            return null;
        }

        // 获取Bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        // 获取ExifInterface
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (exif == null) {
            return bitmap;
        }

        return adjustOrientation(bitmap, exif);
    }

    /**
     * 获取Bitmap
     *
     * @param context context
     * @param uri     uri
     * @return Bitmap
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Bitmap getBitmap(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        // 获取Bitmap
        Bitmap bitmap = null;
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取ExifInterface
        ExifInterface exif = null;
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                exif = new ExifInterface(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (exif == null) {
            return bitmap;
        }

        return adjustOrientation(bitmap, exif);
    }

    /**
     * 校准Bitmap方向
     *
     * @param bitmap bitmap
     * @param exif   ExifInterface
     * @return Bitmap
     */
    public static Bitmap adjustOrientation(Bitmap bitmap, ExifInterface exif) {
        if (exif == null) {
            return bitmap;
        }

        // 计算旋转角度
        int angle;
        int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        switch (ori) {
            default:
                angle = 0;
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
        }
        if (angle == 0) {
            return bitmap;
        }

        // 旋转图片
        Matrix matrix = new Matrix();
        matrix.setRotate(angle);
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        recycle(bitmap);
        return result;
    }

    public static void saveBitmapToFile(Bitmap bitmap, File outFile, SaveBitmapCallback callback) {
        if (bitmap == null) {
            callback.onFail(new NullPointerException("Bitmap不能为null"));
            return;
        }

        // 保存文件
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            callback.onSuccess(outFile);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFail(e);
        }
    }

    /**
     * 添加水印到Bitmap
     *
     * @param bitmap     bitmap
     * @param baseWidth  基础数据(用于计算等比例后的真实数据)
     * @param baseHeight 基础数据(用于计算等比例后的真实数据)
     * @param text       水印文本
     * @param textColor  水印颜色
     * @param textSize   水印文字大小
     * @param offsetX    水印X轴偏移量
     * @param offsetY    水印Y轴偏移量
     * @param isLeft     是否在左边
     */
    public static void addWatermark(Bitmap bitmap, float baseWidth, float baseHeight, String text, int textColor, float textSize, float offsetX, float offsetY, boolean isLeft) {
        if (bitmap == null) {
            return;
        }

        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        if (TextUtils.isEmpty(text)) {
            return;
        }

        // 计算成比例的数据(相对宽高取最小值)
        float ratioWidth = width / baseWidth;
        float ratioHeight = height / baseHeight;
        float ratio = Math.min(ratioWidth, ratioHeight);
        textSize = textSize * ratio;
        offsetX = offsetX * ratio;
        offsetY = offsetY * ratio;

        // 绘制水印文字
        Canvas canvas = new Canvas(bitmap);
        Paint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);

        float textX;
        if (isLeft) {
            textPaint.setTextAlign(Paint.Align.LEFT);

            textX = offsetX;
        } else {
            textPaint.setTextAlign(Paint.Align.RIGHT);

            textX = width - offsetX;
        }
        float textY = height - offsetY - rect.height() - rect.top;
        canvas.drawText(text, textX, textY, textPaint);
    }





    /**
     * 拼接Bitmap
     *
     * @param bitmapList bitmapList
     */
    public static Bitmap puzzleBitmap(List<Bitmap> bitmapList) {
        if (bitmapList == null || bitmapList.isEmpty()) {
            return null;
        }

        // 计算画布宽高
        int width = 0;
        int height = 0;
        for (Bitmap bitmap : bitmapList) {
            width = Math.max(width, bitmap.getWidth());
            height += bitmap.getHeight();
        }

        // 创建画布
        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);

        // 拼接图片
        int top = 0;
        for (Bitmap bitmap : bitmapList) {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            float left = (width - bitmapWidth) / 2.0f;
            canvas.drawBitmap(bitmap, left, top, null);

            top += bitmapHeight;
        }

        return resultBitmap;
    }

}