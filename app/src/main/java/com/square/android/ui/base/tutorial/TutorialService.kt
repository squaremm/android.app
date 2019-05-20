package com.square.android.ui.base.tutorial

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.SparseArray
import android.view.*
import androidx.annotation.IntDef
import com.square.android.R
import android.view.WindowManager
import com.square.android.data.local.LocalDataManager

class TutorialService : Service() {

    @IntDef(TutorialKey.PLACE, TutorialKey.BOOKINGS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TutorialKey {
        companion object {
            const val PLACE = 0
            const val BOOKINGS = 1
        }
    }

    private val mTutorialsSparseArray = SparseArray<Tutorial>()

    private var mTutorialView: TutorialView? = null
    private var mWindowManager: WindowManager? = null
    private var mWindowLayoutParams: WindowManager.LayoutParams? = null

    private var onStepChangingListener : OnStepChangingListener = object : OnStepChangingListener{
        override fun onStepChanging(targetStepNumber: Int) { }
    }

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
                    WindowManager.LayoutParams.TYPE_TOAST,                                               //was FLAG_LAYOUT_IN_SCREEN
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT)
            mWindowLayoutParams!!.gravity = Gravity.CENTER or Gravity.TOP

        } else {
            mWindowLayoutParams = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,                                //was FLAG_LAYOUT_IN_SCREEN
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT)
            mWindowLayoutParams!!.gravity = Gravity.CENTER or Gravity.TOP
        }

        mTutorialView = TutorialView(this)
        mTutorialView!!.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_UP) {
                    mTutorialView!!.onTouchEvent(event)
                   // return true
                }
                return false
            }
        })

        localDataManager = LocalDataManager(this)

        buildTutorials()
    }

    override fun onStartCommand(intent: Intent?, flag: Int, startId: Int): Int {

        if (intent != null && intent.hasExtra(TUTORIAL_APP_EXTRA_KEY)) {
            @TutorialKey val tutorialKey = determineTutorialKey(intent)
            if (tutorialKey != -1) {
                val t = mTutorialsSparseArray.get(tutorialKey)
                if (t != null) {
                    mWindowManager!!.addView(mTutorialView, mWindowLayoutParams)
                    t!!.show()
                }
            }
        }
        return START_STICKY

//TODO: uncomment this and swap with code above when testing done
//        if (intent != null && intent.hasExtra(TUTORIAL_APP_EXTRA_KEY)) {
//            @TutorialKey val tutorialKey = determineTutorialKey(intent)
//            if (tutorialKey != -1 && !localDataManager!!.getTutorialDontShowAgain(tutorialKey)) {
//                val t = mTutorialsSparseArray.get(tutorialKey)
//                if (t != null) {
//                    mWindowManager!!.addView(mTutorialView, mWindowLayoutParams)
//                    t!!.show()
//                }
//            }
//        }
//        return START_STICKY
    }

    private fun determineTutorialKey(intent: Intent): Int {
        val t = intent.getStringExtra(TUTORIAL_APP_EXTRA_KEY)
        if (t == TUTORIAL_1_PLACE) {
            return TutorialKey.PLACE
        }
        return -1
    }

    private fun buildTutorials() {
        mTutorialsSparseArray.put(TutorialKey.PLACE, buildPlaceTutorial())
        // mTutorialsSparseArray.put(TutorialKey.BOOKINGS, buildBookingsTutorial())
    }

    private fun buildPlaceTutorial(): Tutorial {
        return Tutorial.Builder(mTutorialView!!, TutorialKey.PLACE)
                .addNextStep(TutorialStep(
                        // width percentage, height percentage for text with arrow
                        floatArrayOf(0.60f, 0.565f),
                        getString(R.string.tut_1_1),
                        TutorialStep.ArrowPos.BOTTOM,
                        R.drawable.arrow_bottom_left_x_top_right,
                        0.3f,
                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                        floatArrayOf(0f,0f,0.76f,88f),
                        // delay before showing view in ms
                        0f))

                .setOnNextStepIsChangingListener(object: TutorialView.OnNextStepIsChangingListener{
                    override fun onNextStepIsChanging(targetStepNumber: Int) {
                        onStepChangingListener.onStepChanging(targetStepNumber)
                    }
                })
                .setOnFinishTutorialListener(object : TutorialView.OnFinishTutorialListener{
                    override fun onTutorialFinished(dontShowAgain: Boolean) {
                        commonTutorialFinished(TutorialKey.PLACE, dontShowAgain)
                    }
                })
                .build()
    }

    fun commonTutorialFinished(@TutorialKey tutorialKey: Int, dontShowAgain: Boolean) {
        mWindowManager!!.removeViewImmediate(mTutorialView)
        localDataManager!!.setTutorialDontShowAgain(tutorialKey, dontShowAgain)
    }

    interface OnStepChangingListener {
        fun onStepChanging(targetStepNumber: Int)
    }

    companion object {
        val TUTORIAL_APP_EXTRA_KEY = "square_tutorial"

        val TUTORIAL_1_PLACE = "tutorial_1_place"
    }
}
