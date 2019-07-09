@file:Suppress("RemoveExplicitTypeArguments")

package com.square.android.di

import com.square.android.data.ActualBillingRepository
import com.square.android.data.ActualRepository
import com.square.android.data.Repository
import com.square.android.data.local.LocalDataManager
import com.square.android.data.BillingRepository
import com.square.android.utils.BooleanWrapper
import org.greenrobot.eventbus.EventBus
import org.koin.dsl.module.module

val dataModule = module {
    single<LocalDataManager> { LocalDataManager(context = get()) }

    single<Repository> { ActualRepository(api = get(name = "base_api"), localManager = get()) }

    single<BillingRepository> { ActualBillingRepository(api = get(name = "billing_api"), localManager = get()) }

    single<EventBus> { EventBus.getDefault() }

    single<BooleanWrapper>(name = "allowSubsCheck"){ BooleanWrapper() }
}
