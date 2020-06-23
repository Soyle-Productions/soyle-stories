package com.soyle.stories.theme.addValueWebToTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.theme.usecases.addValueWebToTheme.ValueWebAddedToTheme
import kotlin.coroutines.coroutineContext

class AddValueWebToThemeNotifier : Notifier<AddValueWebToTheme.OutputPort>(), AddValueWebToTheme.OutputPort {

    override suspend fun addedValueWebToTheme(response: ValueWebAddedToTheme) {
        notifyAll(coroutineContext) { it.addedValueWebToTheme(response) }
    }

}