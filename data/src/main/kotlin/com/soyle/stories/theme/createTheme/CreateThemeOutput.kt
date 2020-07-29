package com.soyle.stories.theme.createTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.addSymbolToTheme.SymbolAddedToThemeReceiver
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme

class CreateThemeOutput(
    private val createdThemeReceiver: CreatedThemeReceiver,
    private val symbolAddedToThemeReceiver: SymbolAddedToThemeReceiver
) : CreateTheme.OutputPort {

    override suspend fun themeCreated(response: CreatedTheme) {
        createdThemeReceiver.receiveCreatedTheme(response)
    }

    override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {
        symbolAddedToThemeReceiver.receiveSymbolAddedToTheme(response)
    }
}