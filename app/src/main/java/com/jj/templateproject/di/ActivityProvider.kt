package com.jj.templateproject.di

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ActivityProvider(private val application: Application) {

    var activeActivity: Activity? = null

    fun start() {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
                activeActivity = null
            }

            override fun onActivityResumed(activity: Activity) {
                activeActivity = activity
            }

            override fun onActivityStopped(p0: Activity) {
                /* no-op */
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
                /* no-op */
            }

            override fun onActivityDestroyed(p0: Activity) {
                /* no-op */
            }

            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                /* no-op */
            }

            override fun onActivityStarted(p0: Activity) {
                /* no-op */
            }
        })
    }
}