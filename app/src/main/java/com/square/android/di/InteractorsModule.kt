package com.square.android.di

import com.square.android.domain.review.ReviewInteractor
import com.square.android.domain.review.ReviewInteractorImpl
import org.koin.dsl.module.module

val interactorsModule = module {
    single<ReviewInteractor> { ReviewInteractorImpl(get()) }
}