package com.square.android.ui.base.tutorial

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.util.*
import com.square.android.R
import android.os.Looper
import android.os.Handler
import android.util.TypedValue

class TutorialView : ConstraintLayout {

    constructor(context: Context) : super(context) {init()}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {init()}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {init()}

    private var bitmap: Bitmap? = null

    private var x1: Float = 0f
    private var x2: Float = 0f
    private var y1: Float = 0f
    private var y2: Float = 0f

    private var mAwaitStepChange = false

    lateinit var tutorialMessage: TextView
    lateinit var tutorialArrow: AppCompatImageView

    private var showView = false

    private var mFinished = false

    private var mScreenMetrics: DisplayMetrics? = null

    private var mTutorialSteps: ArrayList<TutorialStep>? = null
    private var mCurrentTutorialStep: TutorialStep? = null

    private var mCurrentTutorialStepNumber = -1

    private var mOnNextStepIsChangingListener: OnNextStepIsChangingListener? = null
    private var mOnFinishTutorialListener: OnFinishTutorialListener? = null

    fun setOnFinishTutorialListener(listener: OnFinishTutorialListener) {
        this.mOnFinishTutorialListener = listener
    }

    fun setOnStepIsChangingListener(listener: OnNextStepIsChangingListener) {
        this.mOnNextStepIsChangingListener = listener
    }

    fun setTutorialSteps(tutorialSteps: ArrayList<TutorialStep>) {
        this.mTutorialSteps = tutorialSteps
    }

    fun initialStep(){
        mCurrentTutorialStepNumber++

        mCurrentTutorialStep = mTutorialSteps?.get(mCurrentTutorialStepNumber)

        mOnNextStepIsChangingListener?.onNextStepIsChanging(mCurrentTutorialStepNumber + 1)

        if(mCurrentTutorialStep!!.waitValue > 0){
            awaitStepChange()
            hideContent(true)

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    mAwaitStepChange = false
                    configureCurrentStep()
                }
            }, mCurrentTutorialStep!!.waitValue.toLong())
        } else{
            configureCurrentStep()
        }
    }

    fun isFinished(): Boolean {
        return mFinished
    }

    fun init(){
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        isClickable = true

        mScreenMetrics = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(mScreenMetrics)

        createViews()
    }

    private fun createViews(){
        tutorialMessage = TextView(context)
        tutorialMessage.id = R.id.tutorialMessage
        tutorialMessage.textSize = 16f
        tutorialMessage.setTextColor(ContextCompat.getColor(context, R.color.nice_pink))
        tutorialMessage.setPadding(resources.getDimensionPixelSize(R.dimen.value_16dp),resources.getDimensionPixelSize(R.dimen.value_16dp), resources.getDimensionPixelSize(R.dimen.value_16dp),resources.getDimensionPixelSize(R.dimen.value_16dp))
        tutorialMessage.setLineSpacing( resources.getDimensionPixelSize(R.dimen.value_4dp).toFloat(),1f)
        tutorialMessage.background = ContextCompat.getDrawable(context, R.drawable.white_button_background)
        tutorialMessage.visibility = View.GONE
        tutorialMessage.gravity = Gravity.CENTER

        addView(tutorialMessage)

        tutorialArrow = AppCompatImageView(context)
        tutorialArrow.id = R.id.tutorialArrow
        tutorialArrow.imageTintMode = PorterDuff.Mode.SRC_ATOP
        tutorialArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.nice_pink))
        tutorialArrow.visibility = View.GONE

        addView(tutorialArrow)
    }

    private fun setupViews() {
        Handler(Looper.getMainLooper()).post(Runnable {
            tutorialMessage.text = mCurrentTutorialStep!!.stepText
            tutorialMessage.measure(0, 0)
            tutorialMessage.x = (width * mCurrentTutorialStep!!.infoWindowPercentagePos!![0]) - tutorialMessage.measuredWidth/2
            tutorialMessage.y = (height * mCurrentTutorialStep!!.infoWindowPercentagePos!![1]) - tutorialMessage.measuredHeight/2

            tutorialArrow.setImageResource(mCurrentTutorialStep!!.arrowDrawable)
            tutorialArrow.measure(0, 0)
            if(mCurrentTutorialStep!!.arrowPos == TutorialStep.ArrowPos.TOP){
                tutorialArrow.y =(tutorialMessage.y - tutorialArrow.measuredHeight) +3
            } else if(mCurrentTutorialStep!!.arrowPos == TutorialStep.ArrowPos.BOTTOM){
                tutorialArrow.y = (tutorialMessage.y + tutorialMessage.measuredHeight) -3
            }
            tutorialArrow.x = (tutorialMessage.x +(mCurrentTutorialStep!!.arrowHorizontalPercentagePos * tutorialMessage.measuredWidth)) - tutorialArrow.measuredWidth/2

            x1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCurrentTutorialStep!!.transparentViewPixelPos!![0], context.resources.displayMetrics)
            x2 = width - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCurrentTutorialStep!!.transparentViewPixelPos!![1], context.resources.displayMetrics)
            y1 = (height * mCurrentTutorialStep!!.transparentViewPixelPos!![2]) - ((TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCurrentTutorialStep!!.transparentViewPixelPos!![3], context.resources.displayMetrics)) /2)
            y2 = (height * mCurrentTutorialStep!!.transparentViewPixelPos!![2]) + ((TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCurrentTutorialStep!!.transparentViewPixelPos!![3], context.resources.displayMetrics)) /2)

            showView = true
            hideContent(false)

            postInvalidate()
        })

    }

    override fun dispatchDraw(canvas: Canvas) {
        if(showView){
            bitmap?.let {canvas.drawBitmap(it, 0f, 0f, null)} ?: run {
                createWindowFrame()
                bitmap?.let{canvas.drawBitmap(it, 0f, 0f, null)}
            }
        }

        super.dispatchDraw(canvas)
    }

    private fun createWindowFrame() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        bitmap?.let {
            val osCanvas = Canvas(it)
            val outerRectangle = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = ContextCompat.getColor(context, R.color.tutorial_bg)
            osCanvas.drawRect(outerRectangle, paint)

            val transRect = RectF(x1, y1, x2, y2)
            paint.color = Color.TRANSPARENT
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
            osCanvas.drawRect(transRect, paint)
        }
    }

    override fun isInEditMode(): Boolean {
        return true
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        bitmap = null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP ->{
                if(showView){
                    if((event.x in x1..x2) && (event.y in y1..y2)){
                        bitmap = null
                        showView = false
                        hideContent(true)
                        showNextStep()
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun showNextStep() {
        stepChangeFirstPhase()

        if (!mAwaitStepChange) {
            configureCurrentStep()
            stepChangeFinalPhase()
        }
    }

    fun awaitStepChange() {
        mAwaitStepChange = true
    }

    fun resumeStepChange() {
        mAwaitStepChange = false
        configureCurrentStep()
        stepChangeFinalPhase()
    }

    private fun configureCurrentStep() {
        if (mCurrentTutorialStepNumber < mTutorialSteps!!.size) {
            setupViews()
        }
    }

    private fun stepChangeFirstPhase() {
        mCurrentTutorialStepNumber++

        if (mCurrentTutorialStepNumber < mTutorialSteps!!.size) {
            mCurrentTutorialStep = mTutorialSteps?.get(mCurrentTutorialStepNumber)

            mOnNextStepIsChangingListener?.onNextStepIsChanging(mCurrentTutorialStepNumber + 1)

            if(mCurrentTutorialStep!!.waitValue > 0){
                awaitStepChange()
                awaitForStep(mCurrentTutorialStep!!.waitValue.toLong())
            }
        }
    }

    private fun awaitForStep(delay: Long) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                resumeStepChange()
            }
        }, delay)
    }

    private fun hideContent(hide: Boolean){
       if(hide){
           Handler(Looper.getMainLooper()).post(Runnable {
               tutorialArrow.visibility = View.GONE
               tutorialMessage.visibility = View.GONE
           })

       } else{
           Handler(Looper.getMainLooper()).post(Runnable {
               tutorialArrow.visibility = View.VISIBLE
               tutorialMessage.visibility = View.VISIBLE
           })
       }
    }

    @SuppressLint("SetTextI18n")
    private fun stepChangeFinalPhase() {
        if (mCurrentTutorialStepNumber >= mTutorialSteps!!.size) {
            mFinished = true
            mOnFinishTutorialListener?.onTutorialFinished(true)
        }
    }

    interface OnNextStepIsChangingListener {
        fun onNextStepIsChanging(targetStepNumber: Int)
    }

    interface OnFinishTutorialListener {
        fun onTutorialFinished(dontShowAgain: Boolean)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

}