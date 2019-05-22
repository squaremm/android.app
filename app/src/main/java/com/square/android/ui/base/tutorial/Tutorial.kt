package com.square.android.ui.base.tutorial

import java.util.*

class Tutorial private constructor(
        var tutorialView: TutorialView? = null,
        var tutorialKey: TutorialService.TutorialKey? = null,
        private var tutorialSteps: ArrayList<TutorialStep> = ArrayList(),
        var onFinishTutorialListener: OnFinishTutorialListener? = null,
        var onNextStepIsChangingListener: OnNextStepIsChangingListener? = null,
        var onContinueTutorialListener: OnContinueTutorialListener? = null
) {
    private var startFromStep = 1

    val isTutorialFinished: Boolean
        get() = tutorialView?.isFinished() ?: true

    fun show() {
        tutorialView?.let {
            it.onFinishTutorialListener = onFinishTutorialListener
            it.onNextStepIsChangingListener = onNextStepIsChangingListener
            it.onContinueTutorialListener = onContinueTutorialListener
            it.tutorialSteps = tutorialSteps
            it.initialStep()
        }
    }

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
                         l3: OnFinishTutorialListener) =
                setTutorialView(tutorial.tutorialView)
                .startFromStep(tutorial.startFromStep)
                .setTutorialKey(tutorialKey)
                .setOnNextStepIsChangingListener(l1)
                .setOnContinueTutorialListener(l2)
                .setOnFinishTutorialListener(l3)

        fun startFromStep(startFromStep: Int) = apply {
            this.startFromStep = startFromStep
        }

        fun setTutorialView(tutorialView: TutorialView?) = apply {
            this.tutorialView = tutorialView
        }

        fun setTutorialKey(tutorialKey: TutorialService.TutorialKey?) = apply {
            this.tutorialKey = tutorialKey
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

        fun build() = Tutorial(
                tutorialView,
                tutorialKey,
                tutorialSteps,
                onFinishTutorialListener,
                onNextStepIsChangingListener,
                onContinueTutorialListener)
    }
}