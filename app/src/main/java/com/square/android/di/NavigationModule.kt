package com.square.android.di

import com.square.android.utils.DialogDepository
import org.koin.dsl.module.module
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

val navigationModule = module {
    single<Cicerone<Router>> {
        Cicerone.create()
    }

    single {
        get<Cicerone<Router>>().router
    }

    single {
        get<Cicerone<Router>>().navigatorHolder
    }

    single {
        DialogDepository(get(), get())
    }
}