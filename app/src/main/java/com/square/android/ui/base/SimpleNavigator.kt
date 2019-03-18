package com.square.android.ui.base

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command

interface SimpleNavigator : Navigator {
    override fun applyCommands(commands: Array<out Command>?) {}
}