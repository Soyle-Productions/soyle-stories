package com.soyle.stories.desktop.config.drivers.scene.character

import com.soyle.stories.common.ViewOf
import com.soyle.stories.desktop.view.scene.sceneCharacters.inspect.access
import com.soyle.stories.desktop.view.scene.sceneCharacters.inspect.drive
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspectionViewModel

fun ViewOf<CharacterInSceneInspectionViewModel>.assignRole(role: String) {
    when (role) {
        "Inciting Character" -> if (! access().incitingCharacterToggle.isSelected) drive { incitingCharacterToggle.fire() }
        "Opponent" -> if (! access().opponentCharacterToggle.isSelected) drive { opponentCharacterToggle.fire() }
    }
}
fun ViewOf<CharacterInSceneInspectionViewModel>.setDesireAs(desire: String) {
    drive {
        desireInput.requestFocus()
        desireInput.text = desire
        motivationInput.requestFocus()
    }
}

fun ViewOf<CharacterInSceneInspectionViewModel>.setMotivationAs(motivation: String) {
    drive {
        motivationInput.requestFocus()
        motivationInput.text = motivation
        desireInput.requestFocus()
    }
}