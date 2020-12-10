package com.sirius.net.networkingapp.volley

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton(context: Context) {
    private var mInstance: VolleySingleton? = null
    private var mRequestQueue: RequestQueue? = null
    private var mCtx: Context? = null

    init {
        mCtx = context
        mRequestQueue = getRequestQueue()
    }

    @Synchronized
    fun getInstance(context: Context): VolleySingleton? {
        if (mInstance == null) {
            mInstance = VolleySingleton(context)
        }
        return mInstance
    }

    fun getRequestQueue(): RequestQueue? {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx!!.applicationContext)
        }
        return mRequestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>?) {
        getRequestQueue()!!.add(req)
    }
}