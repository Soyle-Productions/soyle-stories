package com.soyle.stories.desktop.view.di

import org.koin.core.Koin
import org.koin.core.context.GlobalContext

val koin: Koin get() = GlobalContext.get()