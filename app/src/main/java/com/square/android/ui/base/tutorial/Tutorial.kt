package com.square.android.ui.base.tutorial

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class Tutorial : Parcelable {

    var tutorialKey: TutorialService.TutorialKey? = null
    private var tutorialSteps = ArrayList<TutorialStep>()
    var onFinishTutorialListener: OnFinishTutorialListener? = null
    var onNextStepIsChangingListener: OnNextStepIsChangingListener? = null
    var onContinueTutorialListener: OnContinueTutorialListener? = null

    private var startFromStep = 1
    var tutorialView: TutorialView? = null

    val isTutorialFinished: Boolean
        get() = tutorialView?.isFinished() ?: true

    constructor(builder: Builder) {
        tutorialView = builder.tutorialView
        tutorialKey = builder.tutorialKey
        tutorialSteps = builder.tutorialSteps
        startFromStep = builder.startFromStep
        onFinishTutorialListener = builder.onFinishTutorialListener
        onNextStepIsChangingListener = builder.onNextStepIsChangingListener
        onContinueTutorialListener = builder.onContinueTutorialListener
    }

    constructor(parcel: Parcel) {
        parcel.readTypedList(tutorialSteps, TutorialStep.CREATOR)
        startFromStep = parcel.readInt()
    }

    fun show() {
        tutorialView?.let {
            it.onFinishTutorialListener = onFinishTutorialListener
            it.onNextStepIsChangingListener = onNextStepIsChangingListener
            it.onContinueTutorialListener = onContinueTutorialListener
            it.tutorialSteps = tutorialSteps
            it.initialStep()
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = dest.run {
        writeTypedList(tutorialSteps)
        writeInt(startFromStep)
    }

    override fun describeContents() = 0

    data class Builder(var tutorialKey: TutorialService.TutorialKey? = null,
                       var tutorialView: TutorialView? = null,
                       var tutorialSteps: ArrayList<TutorialStep> = ArrayList(),
                       var startFromStep: Int = 1,
                       var onFinishTutorialListener: OnFinishTutorialListener? = null,
                       var onNextStepIsChangingListener: OnNextStepIsChangingListener? = null,
                       var onContinueTutorialListener: OnContinueTutorialListener? = null) {

        fun fromExisting(tutorial: Tutorial,
                         l1: OnNextStepIsChangingListener,
                         l2: OnContinueTutorialListener,
                         l3: OnFinishTutorialListener) = apply {
            tutorialView = tutorial.tutorialView;
            tutorialSteps = tutorial.tutorialSteps
            startFromStep = tutorial.startFromStep
            tutorialKey = tutorial.tutorialKey
            setOnNextStepIsChangingListener(l1)
            setOnContinueTutorialListener(l2)
            setOnFinishTutorialListener(l3)
        }

        fun addNextStep(tutorialStep: TutorialStep) = apply {
            this.tutorialSteps.add(tutorialStep)
        }

        fun setOnFinishTutorialListener(onFinishTutorialListener: OnFinishTutorialListener) = apply {
            this.onFinishTutorialListener = onFinishTutorialListener
        }

        fun setOnNextStepIsChangingListener(onNextStepIsChangingListener: OnNextStepIsChangingListener) = apply {
            this.onNextStepIsChangingListener = onNextStepIsChangingListener
        }

        fun setOnContinueTutorialListener(onContinueTutorialListener: OnContinueTutorialListener) = apply {
            this.onContinueTutorialListener = onContinueTutorialListener
        }

        fun build() = Tutorial(this)
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