package com.square.android.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import java.lang.ref.WeakReference

class AppInBackgroundEvent
class AppInForegroundEvent

class AppLifecycleTracker(val eventBus: EventBus) : Application.ActivityLifecycleCallbacks  {

    private var numStarted = 0

    private lateinit var lastActivity: WeakReference<Activity>

    override fun onActivityPaused(p0: Activity?) {

    }

    override fun onActivityResumed(p0: Activity?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {

    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {

    }

    override fun onActivityCreated(activity: Activity?, p1: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity?) {
        if (numStarted == 0) {
            eventBus.post(AppInForegroundEvent())
        }
        lastActivity = WeakReference(activity!!)
        numStarted++
    }

    override fun onActivityStopped(activity: Activity?) {
        numStarted--
        if (numStarted == 0) {
            eventBus.post(AppInBackgroundEvent())
        }
    }
}