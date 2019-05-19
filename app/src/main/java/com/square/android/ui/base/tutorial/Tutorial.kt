package com.square.android.ui.base.tutorial

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class Tutorial : Parcelable {

    @TutorialService.TutorialKey
    private var mTutorialKey: Int = 0
    private var mTutorialSteps = ArrayList<TutorialStep>()
    lateinit var onFinishTutorialListener: TutorialView.OnFinishTutorialListener
    lateinit var onNextStepIsChangingListener: TutorialView.OnNextStepIsChangingListener
    private var mStartFromStep = 1
    lateinit var mTutorialView: TutorialView

    val isTutorialFinished: Boolean
        get() = mTutorialView.isFinished()

    constructor(builder: Builder) {
        this.mTutorialView = builder.tutorialView
        this.mTutorialKey = builder.tutorialKey
        this.mTutorialSteps = builder.tutorialSteps
        this.mStartFromStep = builder.startFromStep
        this.onFinishTutorialListener = builder.onFinishTutorialListener
        this.onNextStepIsChangingListener = builder.onNextStepIsChangingListener
    }

    constructor(parcel: Parcel) {
        parcel.readTypedList(mTutorialSteps, TutorialStep.CREATOR)
        mStartFromStep = parcel.readInt()
    }

    fun show() {
        mTutorialView.setOnFinishTutorialListener(onFinishTutorialListener)
        mTutorialView.setOnStepIsChangingListener(onNextStepIsChangingListener)
        mTutorialView.setTutorialSteps(mTutorialSteps)
        mTutorialView.initialStep()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(mTutorialSteps)
        dest.writeInt(mStartFromStep)
    }

    override fun describeContents(): Int {
        return 0
    }

    class Builder(var tutorialView: TutorialView, @TutorialService.TutorialKey var tutorialKey: Int) {

        var tutorialSteps = ArrayList<TutorialStep>()
        var startFromStep = 1
        lateinit var onFinishTutorialListener: TutorialView.OnFinishTutorialListener
        lateinit var onNextStepIsChangingListener: TutorialView.OnNextStepIsChangingListener

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

        fun setOnFinishTutorialListener(onFinishTutorialListener: TutorialView.OnFinishTutorialListener): Builder {
            this.onFinishTutorialListener = onFinishTutorialListener
            return this
        }

        fun setOnNextStepIsChangingListener(onNextStepIsChangingListener: TutorialView.OnNextStepIsChangingListener): Builder {
            this.onNextStepIsChangingListener = onNextStepIsChangingListener
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