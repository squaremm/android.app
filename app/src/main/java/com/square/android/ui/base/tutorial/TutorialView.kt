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
        mOnNextStepIsChangingListener?.onNextStepIsChanging(mCurrentTutorialStepNumber + 1)

        if(mCurrentTutorialStep!!.waitValue > 0){
            awaitStepChange()
            hideContent(true)

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    mAwaitStepChange = false
                    configureCurrentStep()
                    showView = true
                    hideContent(false)
                }
            }, mCurrentTutorialStep!!.waitValue.toLong())
        } else{
            showView = true
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

        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT


        mScreenMetrics = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(mScreenMetrics)

        createViews()
    }

    private fun createViews(){
        tutorialMessage = TextView(context)
        tutorialMessage.id = R.id.tutorialMessage
        var params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        tutorialMessage.layoutParams = params
        tutorialMessage.textSize = 16f
        tutorialMessage.setTextColor(ContextCompat.getColor(context, R.color.nice_pink))
        tutorialMessage.setPadding(resources.getDimensionPixelSize(R.dimen.value_16dp),resources.getDimensionPixelSize(R.dimen.value_16dp), resources.getDimensionPixelSize(R.dimen.value_16dp),resources.getDimensionPixelSize(R.dimen.value_16dp))
        //TODO: check later if multipier should stay 1f or be 0f
        tutorialMessage.setLineSpacing( resources.getDimensionPixelSize(R.dimen.value_8dp).toFloat(),1f)
        tutorialMessage.background = ContextCompat.getDrawable(context, R.drawable.white_button_background)
        tutorialMessage.visibility = View.GONE
        addView(tutorialMessage)
//        tutorialMessage.requestLayout()

        tutorialArrow = AppCompatImageView(context)
        tutorialArrow.id = R.id.tutorialArrow
        params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        params.startToStart = tutorialMessage.id
        params.endToEnd = tutorialMessage.id
        params.bottomToTop =  tutorialMessage.id
        tutorialArrow.layoutParams = params
        tutorialArrow.setImageResource(R.drawable.arrow_top_left)
        tutorialArrow.imageTintMode = PorterDuff.Mode.SRC_ATOP
        tutorialArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.nice_pink))
        tutorialArrow.visibility = View.GONE
        addView(tutorialArrow)
//        tutorialArrow.requestLayout()
    }

    private fun setupViews() {
        var layoutParams = ConstraintLayout.LayoutParams(tutorialMessage.layoutParams)

        layoutParams.matchConstraintPercentWidth = mCurrentTutorialStep!!.infoWindowPercentagePos!![0]
        layoutParams.matchConstraintPercentHeight = mCurrentTutorialStep!!.infoWindowPercentagePos!![1]
        tutorialMessage.layoutParams = layoutParams
        tutorialMessage.text = mCurrentTutorialStep!!.stepText


        layoutParams = ConstraintLayout.LayoutParams(tutorialArrow.layoutParams)
        if(mCurrentTutorialStep!!.arrowPos == TutorialStep.ArrowPos.TOP){
            layoutParams.bottomToTop =  tutorialMessage.id
            layoutParams.topToBottom = -1
        } else if(mCurrentTutorialStep!!.arrowPos == TutorialStep.ArrowPos.BOTTOM){
            layoutParams.bottomToTop = -1
            layoutParams.topToBottom = tutorialMessage.id
        }
        layoutParams.matchConstraintPercentWidth = mCurrentTutorialStep!!.arrowHorizontalPercentagePos
        tutorialArrow.layoutParams = layoutParams
        tutorialArrow.setImageResource(mCurrentTutorialStep!!.arrowDrawable)

        hideContent(false)

        //TODO: check if it is required
        tutorialMessage.requestLayout()
        tutorialArrow.requestLayout()

        x1 = mCurrentTutorialStep!!.transparentViewPixelPos!![0]
        x2 = mCurrentTutorialStep!!.transparentViewPixelPos!![1]
        y1 = mCurrentTutorialStep!!.transparentViewPixelPos!![2]
        y2 = mCurrentTutorialStep!!.transparentViewPixelPos!![3]
        createWindowFrame()
    }

    override fun dispatchDraw(canvas: Canvas) {
        if(showView){
            bitmap?.let {canvas.drawBitmap(it, 0f, 0f, null)} ?: createWindowFrame()
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
//            paint.alpha = 99
            osCanvas.drawRect(outerRectangle, paint)

            // left top right bottom
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

                    //TODO check if clicked inside transparent view bounds (x1, x2, y1, y2)

                    // if ok
                    showNextStep()

//                // else
//                performClick()
//                // or nothing?
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

//    fun awaitStepChangeForView(view: View?, runnable: Runnable?) {
//        if (view != null) {
//            val awaitRunnable = Runnable {
//                runnable?.run()
//                resumeStepChange()
//            }
//            mAwaitStepChange = true
//            if (view.visibility == View.VISIBLE) {
//                view.post(awaitRunnable)
//            } else {
//                view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                    override fun onGlobalLayout() {
//                        if (view.visibility == View.VISIBLE) {
//                            awaitRunnable.run()
//                            view.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                        }
//                    }
//                })
//            }
//        }
//    }

    fun resumeStepChange() {
        mAwaitStepChange = false
        configureCurrentStep()
        stepChangeFinalPhase()
    }

    private fun configureCurrentStep() {
        if (mCurrentTutorialStepNumber == mTutorialSteps!!.size - 1 ) {

        } else{
            mCurrentTutorialStep = mTutorialSteps?.get(mCurrentTutorialStepNumber)
            setupViews()
        }
    }

    private fun stepChangeFirstPhase() {
        if (mCurrentTutorialStepNumber < mTutorialSteps!!.size - 1) {
            mCurrentTutorialStepNumber++
            mOnNextStepIsChangingListener?.onNextStepIsChanging(mCurrentTutorialStepNumber + 1)

            if(mCurrentTutorialStep!!.waitValue > 0){
                awaitStepChange()
                showView = false
                hideContent(true)
                awaitForStep(mCurrentTutorialStep!!.waitValue.toLong())
            }
        }
    }

    private fun awaitForStep(delay: Long) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                resumeStepChange()
                showView = true
                hideContent(false)
            }
        }, delay)
    }

    private fun hideContent(hide: Boolean){
       if(hide){
           tutorialArrow.visibility = View.GONE
           tutorialMessage.visibility = View.GONE
       } else{
           tutorialArrow.visibility = View.VISIBLE
           tutorialMessage.visibility = View.VISIBLE
       }
    }

    @SuppressLint("SetTextI18n")
    private fun stepChangeFinalPhase() {

        if (mCurrentTutorialStepNumber == mTutorialSteps!!.size - 1 ) {
            mFinished = true
            mOnFinishTutorialListener?.onTutorialFinished(true)
        }

//
//        if (mCurrentTutorialStepNumber == mTutorialSteps.size() - 1 && !mFinished) {
//            mFinished = true
//        } else if (mFinished) {
//            mOnFinishTutorialListener?.onTutorialFinished(mDontShowAgain)
//        }
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