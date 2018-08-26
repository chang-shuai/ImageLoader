package com.example.bowan.newimageloader.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailLoaderThread<T> extends HandlerThread {
    private static final String TAG = "ThumbnailLoaderThread";
    private static final int MESSAGE_LOAD = 0;

    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private boolean mHasQuit = false;
    private BitmapLruCache mBitmapLruCache;
    private ThumbnailLoaderListener<T> mThumbnailLoaderListener;

    public interface ThumbnailLoaderListener<T> {
        void onThumbnailLoaderListener(T target, Bitmap bitmap);
    }

    public void setThumbnailLoaderListener(ThumbnailLoaderListener listener) {
        mThumbnailLoaderListener = listener;
    }

    public ThumbnailLoaderThread(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        mBitmapLruCache = BitmapLruCache.newInstance();
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_LOAD) {
                    T target = (T) msg.obj;
                    handlerRequest(target);
                }
            }
        };

    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    /**
     * 通过这个方法, UI线程中的图片Path和需要填充的ImageView传送给子线程的Handler.
     * @param target
     * @param imagePath
     */
    public void queueThumbnail(T target, String imagePath) {
        if (imagePath == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, imagePath);
            mRequestHandler.obtainMessage(MESSAGE_LOAD, target).sendToTarget();
        }
    }

    private void handlerRequest(final T target) {
        String imagePath = mRequestMap.get(target);

        if (imagePath == null) {
            return;
        }
        final Bitmap bitmap = getBitmapByImagePath(imagePath);

        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mRequestMap.get(target) == null || mHasQuit) {
                    return;
                }
                mRequestMap.remove(target);
                mThumbnailLoaderListener.onThumbnailLoaderListener(target, bitmap);
            }
        });

    }

    private Bitmap getBitmapByImagePath(String imagePath) {
        Bitmap bitmap = mBitmapLruCache.getBitmapFromLruCache(imagePath);
        if (bitmap == null) {
            bitmap = PictureTool.getScaledBitmap(imagePath);
            mBitmapLruCache.addBitmapToLruCache(imagePath, bitmap);
        }
        return bitmap;
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_LOAD);
        mRequestMap.clear();
    }
}
