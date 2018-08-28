package com.example.bowan.newimageloader.util;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapLruCache {

    private static LruCache<String, Bitmap> mLruCache;
    private static final BitmapLruCache mBitmapLruCache = new BitmapLruCache();

    /**
     * 获取最大的可用内存, 取1/8为LruCache使用, 重写sizeOf方法,
     * 返回Item占用的内存.
     */
    private BitmapLruCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory/8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getWidth()*value.getHeight();
            }
        };

    }

    public static BitmapLruCache newInstance() {

        return mBitmapLruCache;
    }

    public void addBitmapToLruCache(String imagePath, Bitmap bitmap) {
        if (getBitmapFromLruCache(imagePath) == null && bitmap != null) {
            mLruCache.put(imagePath, bitmap);
        }
    }

    public Bitmap getBitmapFromLruCache(String imagePath) {
        return mLruCache.get(imagePath);
    }

}
