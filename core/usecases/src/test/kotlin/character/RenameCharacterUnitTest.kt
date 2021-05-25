package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.prose.*
import com.soyle.stories.domain.prose.events.MentionTextReplaced
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.singleLine
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeCharacterInTheme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.renameCharacter.RenameCharacter
import com.soyle.stories.usecase.character.renameCharacter.RenameCharacterUseCase
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class RenameCharacterUnitTest {

    private val character = makeCharacter()
    val themeId = UUID.randomUUID()
    val inputName = nonBlankStr("Input Name")

    private var updatedCharacter: Character? = null
    private var updatedTheme: Theme? = null
    private var updatedProse: Prose? = null
    private val updatedScenes: MutableList<Scene> = mutableListOf()

    private val characterRepository = CharacterRepositoryDouble(onUpdateCharacter = ::updatedCharacter::set)
    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = ::updatedTheme::set)
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = updatedScenes::add)
    private val proseRepository = ProseRepositoryDouble(onReplaceProse = ::updatedProse::set)
    private var result: RenameCharacter.ResponseModel? = null

    @Test
    fun `character does not exist`() {
        val error = assertThrows<CharacterDoesNotExist> {
            renameCharacter()
        }
        error.characterId.mustEqual(character.id.uuid)
    }

    @Test
    fun `name is same as first name`() {
        characterRepository.givenCharacter(character.withName(inputName))
        renameCharacter()
        updatedCharacter.mustEqual(null) { "Character should not have been updated" }
        assertNull(result) { "No output should have been received" }
    }

    @Nested
    inner class `When Name is Different from Current Name` {

        init {
            characterRepository.givenCharacter(character)
        }

        @Test
        fun `should update character with new name`() {
            renameCharacter()
            val updatedCharacter = updatedCharacter!!
            updatedCharacter.name.value.mustEqual(inputName.value)
        }

        @Test
        fun `should output character renamed event`() {
            renameCharacter()
            result!!.characterRenamed.let {
                it.characterId.mustEqual(character.id)
                it.newName.mustEqual(inputName.value)
            }
        }

        @Nested
        inner class `When Theme has Character` {

            private val theme =
                makeTheme(includedCharacters = mapOf(character.id to makeCharacterInTheme(character.id)))

            init {
                themeRepository.givenTheme(theme)
            }

            @Test
            fun `should update character in theme name`() {
                renameCharacter()
                updatedTheme!!.getIncludedCharacterById(character.id)!!.name.mustEqual(inputName.value)
            }

            @Test
            fun `should output character in theme renamed event`() {
                renameCharacter()
                result!!.affectedThemeIds.mustEqual(listOf(theme.id.uuid))
            }

        }

        @Nested
        inner class `When Character is in Scene` {

            private val scenesWithCharacter = List(4) { makeScene().withCharacterIncluded(character).scene }

            init {
                scenesWithCharacter.forEach(sceneRepository::givenScene)
            }

            @Test
            fun `should update scenes with character`() {
                renameCharacter()
                updatedScenes.map { it.id }.toSet().mustEqual(scenesWithCharacter.map { it.id }.toSet())
                updatedScenes.forEach {
                    it.includedCharacters.find { it.characterId == character.id }!!.characterName.mustEqual(inputName)
                }
            }

            @Test
            fun `should output character in scene renamed`() {
                renameCharacter()
                result!!.renamedCharacterInScenes.map { it.sceneId }.toSet()
                    .mustEqual(scenesWithCharacter.map { it.id }.toSet())
                result!!.renamedCharacterInScenes.forEach {
                    it.renamedCharacter.characterId.mustEqual(character.id)
                    it.renamedCharacter.characterName.mustEqual(inputName.value)
                }
            }

        }

    }

    @Nested
    inner class `Rule - All Prose that mention the character should update the mention of that character` {

        private val proseId = Prose.Id()
        private val prose = makeProse(id = proseId,
            content = listOf(
                ProseContent("", character.id.mentioned() to singleLine(character.name.value))
            )
        )

        init {
            proseRepository.givenProse(prose)
            characterRepository.givenCharacter(character)
        }

        @Test
        fun `should update prose`() {
            renameCharacter()
            updatedProse!!.let {
                it.text.mustEqual(inputName.value) { "prose with only mention should have entire content replaced" }
                it.mentions.single().run{
                    entityId.mustEqual(character.id.mentioned())
                    startIndex.mustEqual(0)
                    endIndex.mustEqual(inputName.length)
                }
            }
        }

        @Test
        fun `should output prose mention text replaced events`() {
            renameCharacter()
            result!!.mentionTextReplaced.single().let {
                it.deletedText.mustEqual(character.name.value)
                it.entityId.mustEqual(character.id.mentioned())
                it.insertedText.mustEqual(inputName.value)
                it.newContent.mustEqual(updatedProse!!.text)
                it.newMentions.mustEqual(updatedProse!!.mentions)
            }
        }

        @Nested
        inner class `Should not modify mentioned name variants`
        {

            private val variant = characterName()

            private val prose = makeProse(id = proseId)
                .withTextInserted(variant.value).prose
                .withEntityMentioned(character.id.mentioned(), 0, variant.length).prose

            init {
                proseRepository.givenProse(prose)
                characterRepository.givenCharacter(character.withNameVariant(variant).character)
            }

            @Test
            fun `Prose should not be updated`() {
                renameCharacter()
                assertNull(updatedProse)
            }

            @Test
            fun `should not output prose updated events`() {
                renameCharacter()
                result!!.mentionTextReplaced.mustEqual(emptyList<MentionTextReplaced>())
            }

        }

    }

    private fun renameCharacter(inputName: NonBlankString = this.inputName) {
        val useCase: RenameCharacter =
            RenameCharacterUseCase(characterRepository, themeRepository, sceneRepository, proseRepository)
        val output = object : RenameCharacter.OutputPort {

            override suspend fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
                result = response
            }
        }

        runBlocking {
            useCase.invoke(character.id.uuid, inputName, output)
        }
    }

}