package com.soyle.stories.characterarc.components

import com.soyle.stories.character.renameCharacter.RenamedCharacterNotifier
import com.soyle.stories.characterarc.eventbus.RenameCharacterOutput
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.get
import com.soyle.stories.gui.View
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleStringProperty

private class CharacterNameModel(private val scope: ProjectScope, characterId: String, private val model: SimpleStringProperty) : View<String>, ReadOnlyProperty<String> by model {
	val presenter by lazy {
		CharacterNamePresenter(characterId, this)
	}

	override fun update(update: String.() -> String) {
		scope.applicationScope.get<ThreadTransformer>().gui {
			val current = model.get()
			val next = current.update()
			println("${this@CharacterNameModel} Update name from $current to $next")
			model.set(next)
		}
	}
}

fun ProjectScope.characterNameModel(characterId: String, characterName: String): ReadOnlyProperty<String>
{
	val model = SimpleStringProperty(characterName)
	val viewImpl = CharacterNameModel(this, characterId, model)
	get<RenamedCharacterNotifier>().addListener(viewImpl.presenter)
	return viewImpl
}