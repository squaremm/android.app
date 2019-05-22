package com.square.android.ui.base.tutorial

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.TextUtils
import android.view.*
import androidx.annotation.IntDef
import android.view.WindowManager
import com.square.android.data.local.LocalDataManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import java.util.*

class TutorialLoadedEvent(val data: Tutorial?)

class TutorialService : Service() {

    private val eventBus: EventBus by inject()

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTutorialLoadedEvent(event: TutorialLoadedEvent) {
        tutorial = event.data
    }

    @IntDef(TutorialKey.PLACE, TutorialKey.BOOKINGS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TutorialKey {
        companion object {
            const val PLACE = 0
            const val BOOKINGS = 1
        }
    }

    private var tutorial: Tutorial? = null

    lateinit var mTutorialView: TutorialView
    private var mWindowManager: WindowManager? = null
    private var mWindowLayoutParams: WindowManager.LayoutParams? = null

    private var localDataManager: LocalDataManager? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        eventBus.register(this)

        setupService()
    }

    private fun setupService() {
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager?

        if (android.os.Build.VERSION.SDK_INT < 26) {
            mWindowLayoutParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT)
            mWindowLayoutParams!!.gravity = Gravity.CENTER or Gravity.TOP

        } else {
            mWindowLayoutParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT)
            mWindowLayoutParams!!.gravity = Gravity.CENTER or Gravity.TOP
        }

        mTutorialView = TutorialView(this)
        mTutorialView!!.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_UP) {
                    mTutorialView!!.onTouchEvent(event)

                    return true
                }
                return false
            }
        })

        localDataManager = LocalDataManager(this)
    }

    override fun onStartCommand(intent: Intent?, flag: Int, startId: Int): Int {
        if(intent != null && intent.hasExtra(TUTORIAL_APP_EXTRA_KEY)) {
            val tutorialKey = getTutorialKey(intent)

            if (!TextUtils.isEmpty(tutorialKey)) {
                if(tutorial == null){

                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            onStartCommand(intent,flag,startId)
                        }
                    }, 50)

                } else{
                    startTutorial(tutorialKey)
                }
            }
        }

        return START_STICKY
    }

    private fun startTutorial(tutorialKey: String){
        tutorial?.onFinishTutorialListener = (object : TutorialView.OnFinishTutorialListener {
            override fun onTutorialFinished(dontShowAgain: Boolean) {
                commonTutorialFinished(tutorialKey, dontShowAgain)
            }
        })
        tutorial?.mTutorialView = mTutorialView
        tutorial?.mTutorialKey = tutorialKey

        if (tutorial != null) {
            Handler(Looper.getMainLooper()).post(Runnable {
                mWindowManager!!.addView(mTutorialView, mWindowLayoutParams)
                tutorial?.show()
            })
        }
    }

    private fun getTutorialKey(intent: Intent): String {
        return intent.getStringExtra(TUTORIAL_APP_EXTRA_KEY)
    }

    fun commonTutorialFinished(tutorialKey: String, dontShowAgain: Boolean) {

        //TODO: is this handler necessary?
        Handler(Looper.getMainLooper()).post(Runnable {
            mWindowManager!!.removeViewImmediate(mTutorialView)
            localDataManager!!.setTutorialDontShowAgain(tutorialKey, dontShowAgain)
        })

        tutorial = null
    }

    companion object {
        val TUTORIAL_APP_EXTRA_KEY = "square_tutorial_app_key"

        val TUTORIAL_1_PLACE = "tutorial_1_place"
        val TUTORIAL_2_BOOKING = "tutorial_2_booking"
        val TUTORIAL_3_REDEMPTIONS = "tutorial_3_redemptions"
        val TUTORIAL_4_SELECT_OFFER = "tutorial_4_select_offer"
        val TUTORIAL_5_REVIEW= "tutorial_5_review"
    }
}
