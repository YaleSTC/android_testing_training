package com.example.samplegallery.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyRequestQueue {
    // we are holding a reference to the Application context that is being extracted from an
    // arbitrary context via context.getApplicationContext(); This does NOT cause a leak
    @SuppressLint("StaticFieldLeak")
    private static VolleyRequestQueue qInstance;
    private RequestQueue reqQueue;
    private ImageLoader imgLoader;
    // see comment above.
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private VolleyRequestQueue(Context context) {
        mContext = context.getApplicationContext();
        reqQueue = getRequestQueue();

        imgLoader = new ImageLoader(
                reqQueue,
                new LruBitmapCache(LruBitmapCache.getCacheSize(context)));
    }

    public RequestQueue getRequestQueue() {
        if (reqQueue == null) {
            // mContext mustn't be null for the following instruction to execute.
            if (mContext == null) {
                throw new NullPointerException("Context is null within getRequestQueue()");
            }

            // important: we are using the APPLICATION context, not ACTIVITY context to make sure
            // that our queue is not destroyed at any point.
            reqQueue = Volley.newRequestQueue(mContext);
        }

        return reqQueue;
    }

    public void cancelAll(String tag) {
        if (qInstance != null) {
            if (reqQueue != null) {
                reqQueue.cancelAll(tag);
            }
        }
    }

    // Notice, we must synchronize to allow thread safety.
    public static synchronized VolleyRequestQueue getInstance(Context context) {
        if (qInstance == null) {
            qInstance = new VolleyRequestQueue(context);
        }

        return qInstance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImgLoader() {
        return imgLoader;
    }
}

