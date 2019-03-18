@file:Suppress("RemoveExplicitTypeArguments")

package com.square.android.di

import com.square.android.data.ActualRepository
import com.square.android.data.Repository
import com.square.android.data.local.LocalDataManager
import org.greenrobot.eventbus.EventBus
import org.koin.dsl.module.module

val dataModule = module {
    single<LocalDataManager> { LocalDataManager(context = get()) }

    single<Repository> { ActualRepository(api = get(), localManager = get()) }

    single<EventBus> { EventBus.getDefault() }
}
