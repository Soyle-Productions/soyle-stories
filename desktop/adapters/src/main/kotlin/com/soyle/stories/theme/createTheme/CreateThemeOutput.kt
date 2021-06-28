package com.soyle.stories.theme.createTheme

import com.soyle.stories.theme.addSymbolToTheme.SymbolAddedToThemeReceiver
import com.soyle.stories.usecase.theme.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.usecase.theme.createTheme.CreateTheme
import com.soyle.stories.usecase.theme.createTheme.CreatedTheme

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