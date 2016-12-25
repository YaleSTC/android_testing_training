package com.example.samplegallery.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

class LruBitmapCache extends LruCache<String, Bitmap>
        implements ImageLoader.ImageCache {

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    LruBitmapCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }


    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    // Make cache of size such that it may hold ~3 screens worth of images
    static int getCacheSize(Context ctx) {
        final DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        final int screenWidth = dm.widthPixels;
        final int screenHeight = dm.heightPixels;
        // a pixel has 4 channels, each 1 byte big (r, g, b, gamma)
        final int screenBytes = screenHeight * screenWidth * 4;

        // 3 pages worth of images!
        return screenBytes * 3;
    }
}
