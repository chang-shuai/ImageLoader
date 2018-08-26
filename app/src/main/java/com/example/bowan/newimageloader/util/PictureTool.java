package com.example.bowan.newimageloader.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PictureTool {
    public static Bitmap getScaledBitmap(String imagePath, int destWidth, int destHeight) {
        BitmapFactory.Options options = null;

        /**
         * 利用Options获取缩放比例, 并且不将图片加载到内存中
         */
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        /**
         * 因为设置为不加载到内存中, 说以decodeFile返回null, 用options中获取宽高
         */
        BitmapFactory.decodeFile(imagePath, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        /**
         * 计算得到缩放比例
         */
        int inSampleSize = 1;
        if (destWidth<srcWidth || destHeight<srcHeight) {
            float widthScale = srcWidth/destWidth;
            float heightScale = srcHeight/destHeight;
            inSampleSize = Math.round(widthScale > heightScale ? widthScale : heightScale);
        }

        /**
         * 重新赋值options, 设置随访比例, 并且默认加载到内存中
         */
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

        return bitmap;
    }

    /**
     * 设置默认宽高为200
     * @param imagePath
     * @return
     */
    public static Bitmap getScaledBitmap(String imagePath) {
        Bitmap bitmap = getScaledBitmap(imagePath, 200, 200);
        return bitmap;
    }

}
