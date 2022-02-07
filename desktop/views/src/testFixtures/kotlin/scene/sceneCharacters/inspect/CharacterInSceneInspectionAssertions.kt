package com.soyle.stories.desktop.view.scene.sceneCharacters.inspect

import com.soyle.stories.common.ViewOf
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspectionViewModel
import org.junit.jupiter.api.Assertions.assertEquals


class CharacterInSceneInspectionAssertions(private val access: CharacterInSceneInspectionAccess) {

    fun hasDesireValue(expectedValue: String) {
        assertEquals(expectedValue, access.desireInput.text)
    }

    fun doesNotHaveDesire() {
        assertEquals("", access.desireInput.text)
    }

    fun hasMotivationValue(expectedValue: String) {
        assertEquals(expectedValue, access.motivationInput.text.orEmpty())
    }

    fun hasInheritedMotivationValue(expectedValue: String) {
        val inheritedMotivation = access.inheritedMotivation!!
        assertEquals(expectedValue, inheritedMotivation.text)
    }

}

inline fun assertThat(view: ViewOf<CharacterInSceneInspectionViewModel>, assertions: CharacterInSceneInspectionAssertions.() -> Unit) {
    CharacterInSceneInspectionAssertions(view.access()).assertions()
}