package com.soyle.stories.writer

import com.soyle.stories.usecase.character.remove.RemoveCharacterFromStory
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject
import com.soyle.stories.usecase.theme.removeSymbolFromTheme.RemoveSymbolFromTheme
import com.soyle.stories.usecase.theme.removeValueWebFromTheme.RemoveValueWebFromTheme
import kotlin.reflect.KClass

sealed class DialogType(val useCase: KClass<*>) {
	object DeleteCharacter : DialogType(RemoveCharacterFromStory::class)
	object DeleteScene : DialogType(com.soyle.stories.usecase.scene.delete.DeleteScene::class)
	object ReorderScene : DialogType(com.soyle.stories.usecase.scene.reorderScene.ReorderScene::class)
	object DeleteTheme : DialogType(com.soyle.stories.usecase.theme.deleteTheme.DeleteTheme::class)
	object DeleteSymbol : DialogType(RemoveSymbolFromTheme::class)
	object DeleteValueWeb : DialogType(RemoveValueWebFromTheme::class)
	object DeleteStoryEvent : DialogType(RemoveStoryEventFromProject::class)
	class Other(useCase: KClass<*>) : DialogType(useCase)
	companion object {
		fun values(): Array<DialogType> {
			return arrayOf(
				DeleteCharacter,
				DeleteScene,
				ReorderScene,
				DeleteTheme,
				DeleteSymbol,
				DeleteValueWeb,
				DeleteStoryEvent
			)
		}

		fun valueOf(value: String): DialogType {
			return when (value) {
				"DeleteCharacter" -> DeleteCharacter
				"DeleteScene" -> DeleteScene
				"ReorderScene" -> ReorderScene
				"DeleteTheme" -> DeleteTheme
				"DeleteSymbol" -> DeleteSymbol
				"DeleteValueWeb" -> DeleteValueWeb
				"DeleteStoryEvent" -> DeleteStoryEvent
				else -> throw IllegalArgumentException("No object com.soyle.stories.writer.DialogType.$value")
			}
		}
	}
}