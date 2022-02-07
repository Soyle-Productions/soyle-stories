package com.soyle.stories.core.definitions.scene.character

import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.inspect.CharacterInSceneInspection
import com.soyle.stories.usecase.scene.character.list.CharactersInScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.AvailableCharactersToAddToScene
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*

class `Characters in Scene Assertions`(
    private val sceneRepository: SceneRepository
) : `Scene Character Steps`.Then {

    override fun characterInScene(
        sceneId: Scene.Id,
        character: Character.Id
    ): `Scene Character Steps`.Then.CharacterInSceneStateAssertions = object :
        `Scene Character Steps`.Then.CharacterInSceneStateAssertions {

        private val scene: Scene
            get() = runBlocking { sceneRepository.getSceneOrError(sceneId.uuid) }

        override fun `should be the`(role: RoleInScene) {
            val scene = scene
            scene.should(
                """
                    $character in $scene should be the $role
                        Character in Scene: ${scene.includedCharacters[character]}
                        Role: ${scene.includedCharacters[character]?.roleInScene}
                """.trimIndent()
            ) {
                includedCharacters[character]?.roleInScene == role
            }
        }

        override fun `should not have a role`(stop: Unit) {
            val scene = scene
            scene.should(
                """
                    $character in $scene should not have a role
                        Character in Scene: ${scene.includedCharacters[character]}
                        Role: ${scene.includedCharacters[character]?.roleInScene}
                """.trimIndent()
            ) {
                includedCharacters[character]?.roleInScene == null
            }
        }

        override fun `should have the desire of`(desire: String) {
            val scene = scene
            scene.should(
                """
                    $character in $scene should have the desire "$desire"
                        Character in Scene: ${scene.includedCharacters[character]}
                        Desire: ${scene.includedCharacters[character]?.desire}
                """.trimIndent()
            ) {
                includedCharacters[character]?.desire == desire
            }
        }

        override fun `should be motivated by`(motivation: String) {
            val scene = scene
            scene.should(
                """
                    $character in $scene should have the motivation "$motivation"
                        Character in Scene: ${scene.includedCharacters[character]}
                        Motivation: ${scene.includedCharacters[character]?.motivation}
                """.trimIndent()
            ) {
                includedCharacters[character]?.motivation == motivation
            }
        }
    }

    override fun the(charactersInScene: CharactersInScene): `Scene Character Steps`.Then.SceneCharactersStateAssertions =
        object : `Scene Character Steps`.Then.SceneCharactersStateAssertions {
            override fun `should include the`(character: Character.Id) {
                charactersInScene.should(
                    """
                        Characters in Scene should include the $character
                            ids: ${charactersInScene.items.map { it.characterId }}
                    """.trimIndent()
                ) {
                    charactersInScene.items.any { it.characterId == character }
                }
            }

            override fun `should not include the`(character: Character.Id) {
                charactersInScene.should(
                    """
                        Characters in Scene should not include the $character
                            ids: ${charactersInScene.items.map { it.characterId }}
                    """.trimIndent()
                ) {
                    charactersInScene.items.none { it.characterId == character }
                }
            }
        }

    override fun characterInSceneItem(
        charactersInScene: CharactersInScene,
        character: Character.Id
    ): `Scene Character Steps`.Then.CharacterInSceneItemAssertions = object :
        `Scene Character Steps`.Then.CharacterInSceneItemAssertions {
        val characterItem
            get() = charactersInScene.items.find { it.characterId == character }

        override fun `should not have any story event coverage`() {
            characterItem!!.sources.shouldBeEmpty()
        }

        override fun `should be flagged as removed from the story`() {
            characterItem!!.project.shouldBeNull()
        }

        override fun `should have the role`(role: RoleInScene) {
            characterItem!!.roleInScene.shouldBeEqualTo(role)
        }

    }

    override fun the(
        availableCharactersToAddToScene: AvailableCharactersToAddToScene
    ): `Scene Character Steps`.Then.AvailableCharactersToAddToSceneAssertions = object : `Scene Character Steps`.Then.AvailableCharactersToAddToSceneAssertions {
        override fun `should include the`(characterId: Character.Id) {
            availableCharactersToAddToScene.shouldContainAny { it.characterId == characterId.uuid }
        }

        override fun `should not include any characters`() {
            availableCharactersToAddToScene.shouldBeEmpty()
        }
    }

    override fun the(inspection: CharacterInSceneInspection): `Scene Character Steps`.Then.CharacterInSceneInspectionAssertions =
        object : `Scene Character Steps`.Then.CharacterInSceneInspectionAssertions {
            override fun `should not have an inherited motivation`() {
                inspection.should {
                    inheritedMotivation == null
                }
            }

            override fun `should have an inherited motivation from the`(scene: Scene.Id) {
                inspection.should {
                    inheritedMotivation?.sceneId == scene
                }
            }

            override fun `should have an inherited motivation of`(motivation: String) {
                inspection.should {
                    inheritedMotivation?.motivation == motivation
                }
            }

            override fun `should have motivation of`(motivation: String) {
                inspection.should {
                    this.motivation == motivation
                }
            }
        }
}