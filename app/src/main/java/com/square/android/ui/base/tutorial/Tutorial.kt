package com.square.android.ui.base.tutorial

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class Tutorial : Parcelable {

    @TutorialService.TutorialKey
    var mTutorialKey: String? = null
    private var mTutorialSteps = ArrayList<TutorialStep>()
    var onFinishTutorialListener: TutorialView.OnFinishTutorialListener? = null
    var onNextStepIsChangingListener: TutorialView.OnNextStepIsChangingListener? = null
    var onContinueTutorialListener: TutorialView.OnContinueTutorialListener? = null

    private var mStartFromStep = 1
    var mTutorialView: TutorialView? = null

    val isTutorialFinished: Boolean
        get() = mTutorialView?.isFinished() ?: true

    constructor(builder: Builder) {
        this.mTutorialView = builder.tutorialView
        this.mTutorialKey = builder.tutorialKey
        this.mTutorialSteps = builder.tutorialSteps
        this.mStartFromStep = builder.startFromStep
        this.onFinishTutorialListener = builder.onFinishTutorialListener
        this.onNextStepIsChangingListener = builder.onNextStepIsChangingListener
        this.onContinueTutorialListener = builder.onContinueTutorialListener
    }

    constructor(parcel: Parcel) {
        parcel.readTypedList(mTutorialSteps, TutorialStep.CREATOR)
        mStartFromStep = parcel.readInt()
    }

    fun show() {
        mTutorialView?.setOnFinishTutorialListener(onFinishTutorialListener)
        mTutorialView?.setOnStepIsChangingListener(onNextStepIsChangingListener)
        mTutorialView?.setonContinueTutorialListener(onContinueTutorialListener)
        mTutorialView?.setTutorialSteps(mTutorialSteps)
        mTutorialView?.initialStep()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(mTutorialSteps)
        dest.writeInt(mStartFromStep)
    }

    override fun describeContents(): Int {
        return 0
    }

    class Builder(var tutorialView: TutorialView? = null, var tutorialKey: String? = null) {

        var tutorialSteps = ArrayList<TutorialStep>()
        var startFromStep = 1
        var onFinishTutorialListener: TutorialView.OnFinishTutorialListener? = null
        var onNextStepIsChangingListener: TutorialView.OnNextStepIsChangingListener? = null
        var onContinueTutorialListener: TutorialView.OnContinueTutorialListener? = null


        fun fromExisting(tutorial: Tutorial, l1: TutorialView.OnNextStepIsChangingListener, l3: TutorialView.OnFinishTutorialListener): Builder {
            this.tutorialSteps = tutorial.mTutorialSteps
            this.startFromStep = tutorial.mStartFromStep
            this.tutorialKey = tutorial.mTutorialKey
            setOnFinishTutorialListener(l3)
            setOnNextStepIsChangingListener(l1)
            return this
        }

        fun addNextStep(tutorialStep: TutorialStep): Builder {
            this.tutorialSteps.add(tutorialStep)
            return this
        }

        fun setOnFinishTutorialListener(onFinishTutorialListener: TutorialView.OnFinishTutorialListener? = null): Builder {
            this.onFinishTutorialListener = onFinishTutorialListener
            return this
        }

        fun setOnNextStepIsChangingListener(onNextStepIsChangingListener: TutorialView.OnNextStepIsChangingListener? = null): Builder {
            this.onNextStepIsChangingListener = onNextStepIsChangingListener
            return this
        }

        fun setOnContinueTutorialListener(onContinueTutorialListener: TutorialView.OnContinueTutorialListener? = null): Builder {
            this.onContinueTutorialListener = onContinueTutorialListener
            return this
        }

        fun build(): Tutorial {
            return Tutorial(this)
        }
    }

    companion object CREATOR : Parcelable.Creator<Tutorial> {
        override fun createFromParcel(parcel: Parcel): Tutorial {
            return Tutorial(parcel)
        }

        override fun newArray(size: Int): Array<Tutorial?> {
            return arrayOfNulls(size)
        }
    }
}