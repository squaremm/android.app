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

typealias OnNextStepIsChangingListener = (targetStepNumber: Int) -> Unit
typealias OnFinishTutorialListener = (dontShowAgain: Boolean) -> Unit
typealias OnContinueTutorialListener = (endDelay: Long) -> Unit

class TutorialView : ConstraintLayout {

    constructor(context: Context) : super(context) {init()}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {init()}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {init()}

    private var bitmap: Bitmap? = null

    private var x1: Float = 0f
    private var x2: Float = 0f
    private var y1: Float = 0f
    private var y2: Float = 0f

    private var awaitStepChange = false

    lateinit var tutorialMessage: TextView
    lateinit var tutorialArrow: AppCompatImageView

    private var showView = false

    private var finished = false

    private var screenMetrics: DisplayMetrics? = null

    private var currentTutorialStepNumber = -1
    private var currentTutorialStep: TutorialStep? = null

    var tutorialSteps: ArrayList<TutorialStep>? = null

    var onNextStepIsChangingListener: OnNextStepIsChangingListener? = null
    var onContinueTutorialListener: OnContinueTutorialListener? = null
    var onFinishTutorialListener: OnFinishTutorialListener? = null


    fun initialStep(){
        showView = false
        currentTutorialStepNumber = -1
        finished = false

        currentTutorialStepNumber++

        currentTutorialStep = tutorialSteps?.get(currentTutorialStepNumber)

        onNextStepIsChangingListener?.invoke(currentTutorialStepNumber + 1)

        if(currentTutorialStep!!.waitValue > 0){
            awaitStepChange()
            hideContent(true)

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    awaitStepChange = false
                    configureCurrentStep()
                }
            }, currentTutorialStep!!.waitValue.toLong())
        } else{
            configureCurrentStep()
        }
    }

    fun isFinished(): Boolean {
        return finished
    }

    fun init(){
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        isClickable = true

        screenMetrics = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(screenMetrics)

        createViews()
    }

    private fun createViews(){
        tutorialMessage = TextView(context).apply {
            id = R.id.tutorialMessage
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.nice_pink))
            setPadding(resources.getDimensionPixelSize(R.dimen.value_16dp),
                    resources.getDimensionPixelSize(R.dimen.value_16dp),
                    resources.getDimensionPixelSize(R.dimen.value_16dp),
                    resources.getDimensionPixelSize(R.dimen.value_16dp))
            setLineSpacing( resources.getDimensionPixelSize(R.dimen.value_4dp).toFloat(),1f)
            background = ContextCompat.getDrawable(context, R.drawable.white_button_background)
            visibility = View.GONE
            gravity = Gravity.CENTER
        }

        addView(tutorialMessage)

        tutorialArrow = AppCompatImageView(context).apply {
            id = R.id.tutorialArrow
            imageTintMode = PorterDuff.Mode.SRC_ATOP
            imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.nice_pink))
            visibility = View.GONE
        }

        addView(tutorialArrow)
    }

    private fun setupViews() {
        Handler(Looper.getMainLooper()).post {
            tutorialMessage.run {
                text = currentTutorialStep!!.stepText
                measure(0, 0)
                x = (this@TutorialView.width * currentTutorialStep!!.infoWindowPercentagePos!![0]) - measuredWidth/2
                y = (this@TutorialView.height * currentTutorialStep!!.infoWindowPercentagePos!![1]) - measuredHeight/2
            }

            tutorialArrow.run {
                setImageResource(currentTutorialStep!!.arrowDrawable)
                measure(0, 0)
                if(currentTutorialStep!!.arrowPos == TutorialStep.ArrowPos.TOP){
                    y =(tutorialMessage.y - measuredHeight) +3
                } else if(currentTutorialStep!!.arrowPos == TutorialStep.ArrowPos.BOTTOM){
                    y = (tutorialMessage.y + tutorialMessage.measuredHeight) -3
                }
                x = (tutorialMessage.x +(currentTutorialStep!!.arrowHorizontalPercentagePos * tutorialMessage.measuredWidth)) - measuredWidth/2
            }

            currentTutorialStep?.run {
                if(transparentViewPixelPos!![0] == 0f && transparentViewPixelPos!![1] == 0f
                        && transparentViewPixelPos!![2] == 0f && transparentViewPixelPos!![3] == 0f){
                    x1 = 0f
                    x2 = this@TutorialView.width.toFloat()
                    y1 = 0f
                    y2 = this@TutorialView.height.toFloat()
                } else{
                    x1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, transparentViewPixelPos!![0], context.resources.displayMetrics)
                    x2 = this@TutorialView.width - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, transparentViewPixelPos!![1], context.resources.displayMetrics)
                    y1 = (this@TutorialView.height * transparentViewPixelPos!![2]) - ((TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, transparentViewPixelPos!![3], context.resources.displayMetrics)) /2)
                    y2 = (this@TutorialView.height * transparentViewPixelPos!![2]) + ((TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, transparentViewPixelPos!![3], context.resources.displayMetrics)) /2)
                }

                showView = true

                if(shouldShowUi == 1){
                    hideContent(false)
                }
            }

            postInvalidate()
        }

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

    override fun isInEditMode() = true

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

        if (!awaitStepChange) {
            configureCurrentStep()
            stepChangeFinalPhase()
        }
    }

    fun awaitStepChange() {
        awaitStepChange = true
    }

    fun resumeStepChange() {
        awaitStepChange = false
        configureCurrentStep()
        stepChangeFinalPhase()
    }

    private fun configureCurrentStep() {
        if (currentTutorialStepNumber < tutorialSteps!!.size) {
            setupViews()
        }
    }

    private fun stepChangeFirstPhase() {
        currentTutorialStepNumber++

        if (currentTutorialStepNumber < tutorialSteps!!.size) {
            currentTutorialStep = tutorialSteps?.get(currentTutorialStepNumber)

            onNextStepIsChangingListener?.invoke(currentTutorialStepNumber + 1)

            if(currentTutorialStep!!.waitValue > 0){
                awaitStepChange()
                awaitForStep(currentTutorialStep!!.waitValue.toLong())
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
           Handler(Looper.getMainLooper()).post {
               tutorialArrow.visibility = View.GONE
               tutorialMessage.visibility = View.GONE
           }

       } else{
           Handler(Looper.getMainLooper()).post {
               tutorialArrow.visibility = View.VISIBLE
               tutorialMessage.visibility = View.VISIBLE
           }
       }
    }

    @SuppressLint("SetTextI18n")
    private fun stepChangeFinalPhase() {
        if (currentTutorialStepNumber >= tutorialSteps!!.size) {
            finished = true

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    onFinishTutorialListener?.invoke(true)
                }
            }, currentTutorialStep!!.endDelay)

            // Use this method to navigate to another fragment in ViewPager that implements Tutorial
            // Wrap your action in Timer and add 50+ ms delay to endDelay
            // See: android.ui.fragment.offer.OfferFragment Tutorial.Builder() -> override fun continueTutorial(endDelay: Long) {
            onContinueTutorialListener?.invoke(currentTutorialStep!!.endDelay)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

}