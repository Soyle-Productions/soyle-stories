package com.soyle.stories.core.definitions.scene

import com.soyle.stories.core.definitions.scene.character.`Characters in Scene Assertions`
import com.soyle.stories.core.definitions.scene.storyevent.`Story Events in Scene Assertions`
import com.soyle.stories.core.framework.scene.`Covered Story Events Steps`
import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.core.framework.scene.`Scene Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.AvailableCharactersToAddToScene
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.should

class `Scene Assertions`(
    private val sceneRepository: SceneRepository
) : `Scene Steps`.Then,
    `Scene Character Steps`.Then by `Characters in Scene Assertions`(sceneRepository),
    `Covered Story Events Steps`.Then by `Story Events in Scene Assertions`()
{

    override fun the(sceneId: Scene.Id): `Scene Steps`.Then.StateAssertions = object :
        `Scene Steps`.Then.StateAssertions {
        private val scene: Scene
            get() = runBlocking { sceneRepository.getSceneOrError(sceneId.uuid) }

        override fun `should not include any characters`() {
            val scene = scene
            scene.should("""
                    $sceneId should not include any characters
                       Included Characters:   ${scene.includedCharacters.toList()}
                """.trimIndent()) {
                includedCharacters.isEmpty()
            }
        }

        override fun `should include the`(characterId: Character.Id) {
            val scene = scene
            scene.should("""
                    $sceneId should include the $characterId
                       Included Characters:   ${scene.includedCharacters.toList()}
                """.trimIndent()) {
                includesCharacter(characterId)
            }
        }

        override fun `should not include the`(characterId: Character.Id) {
            val scene = scene
            scene.should("""
                    $sceneId should not include the $characterId
                       Included Characters:   ${scene.includedCharacters.toList()}
                """.trimIndent()) {
                !includesCharacter(characterId)
            }
        }
    }

}