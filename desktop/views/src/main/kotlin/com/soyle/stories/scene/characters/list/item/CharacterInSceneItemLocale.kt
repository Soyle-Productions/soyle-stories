package com.soyle.stories.scene.characters.list.item

import com.soyle.stories.domain.scene.character.RoleInScene
import javafx.beans.binding.StringExpression
import javafx.beans.value.ObservableValue

interface CharacterInSceneItemLocale {

    val menu: ContextMenu

    interface ContextMenu {
        val edit: StringExpression
        val removeCharacterFromScene: StringExpression
        val toggleIncitingCharacter: StringExpression
        val toggleOpponentCharacter: StringExpression
    }

    val role: Roles

    interface Roles {
        val incitingCharacter: ObservableValue<String>
        val opponentCharacter: ObservableValue<String>
    }

    val warning: Warnings

    interface Warnings {
        val characterRemovedFromStory: ObservableValue<String>
        val characterNotInvolvedInAnyStoryEvents: ObservableValue<String>
    }

}