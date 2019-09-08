package com.square.android.presentation.view

interface LoadingView : ProgressView {
    fun showLoadingDialog()
    fun hideLoadingDialog()
}