package com.square.android.ui.base.tutorial

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
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
        println("EEEE unregister ")

        eventBus.unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTutorialLoadedEvent(event: TutorialLoadedEvent) {
        tutorial = event.data

        println("EEEE tutorial: "+ tutorial)
    }

    init {
        println("EEEE register ")

        eventBus.register(this)
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
                   // return true
                }
                return false
            }
        })

        localDataManager = LocalDataManager(this)
    }

    override fun onStartCommand(intent: Intent?, flag: Int, startId: Int): Int {

        if (intent != null && intent.hasExtra(TUTORIAL_APP_EXTRA_KEY)) {
            @TutorialKey val tutorialKey = determineTutorialKey(intent)

            //TODO swap with code below when testing done
//            if (tutorialKey != -1 && !localDataManager!!.getTutorialDontShowAgain(tutorialKey)) {
            if (tutorialKey != -1) {
                if(tutorial == null){

                    println("EEEE tutorial null ")

                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            startTutorial(tutorialKey)
                        }
                    }, 200)

                } else{
                    println("EEEE tutorial not null ")
                    startTutorial(tutorialKey)
                }
            }
        }
        return START_STICKY
    }

    private fun startTutorial(tutorialKey: Int){
        tutorial?.onFinishTutorialListener = (object : TutorialView.OnFinishTutorialListener {
            override fun onTutorialFinished(dontShowAgain: Boolean) {
                commonTutorialFinished(TutorialKey.PLACE, dontShowAgain)
            }
        })
        tutorial?.mTutorialView = mTutorialView
        tutorial?.mTutorialKey = tutorialKey

        if (tutorial != null) {
            mWindowManager!!.addView(mTutorialView, mWindowLayoutParams)
            tutorial?.show()
        }
    }

    private fun determineTutorialKey(intent: Intent): Int {
        val t = intent.getStringExtra(TUTORIAL_APP_EXTRA_KEY)
        if (t == TUTORIAL_1_PLACE) {
            return TutorialKey.PLACE
        }
        return -1
    }

    fun commonTutorialFinished(@TutorialKey tutorialKey: Int, dontShowAgain: Boolean) {
        mWindowManager!!.removeViewImmediate(mTutorialView)
        localDataManager!!.setTutorialDontShowAgain(tutorialKey, dontShowAgain)

        tutorial = null
    }

    companion object {
        val TUTORIAL_APP_EXTRA_KEY = "square_tutorial_app_key"
        val TUTORIAL_KEY = "square_tutorial_key"

        val TUTORIAL_1_PLACE = "tutorial_1_place"
    }
}
