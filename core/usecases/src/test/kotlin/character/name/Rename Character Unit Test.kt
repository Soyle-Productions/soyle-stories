package com.soyle.stories.usecase.character.name

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterDoesNotHaveNameVariant
import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import com.soyle.stories.domain.character.name.exceptions.CharacterAlreadyHasName
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.prose.buildProse
import com.soyle.stories.domain.prose.events.MentionTextReplaced
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.name.rename.RenameCharacter
import com.soyle.stories.usecase.character.name.rename.RenameCharacterUseCase
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Rename Character Unit Test` {

    // prerequisites
    private val character = makeCharacter()

    // input
    private val inputName = characterName()

    // post-requisites
    private var updatedCharacter: Character? = null
    private val updatedProse: MutableList<Prose> = mutableListOf()

    // output
    private var characterRenamed: CharacterRenamed? = null
    private val mentionTextReplaced: MutableList<MentionTextReplaced> = mutableListOf()

    // repositories
    private val characterRepository = CharacterRepositoryDouble(onUpdateCharacter = ::updatedCharacter::set)
    private val proseRepository = ProseRepositoryDouble(onReplaceProse = updatedProse::add)

    @Test
    fun `given character doesn't exist - should return error`() {
        val error = renameCharacter().exceptionOrNull()

        error as CharacterDoesNotExist
        error.characterId.shouldBeEqualTo(character.id)
        shouldNotProduceOutput()
        shouldNotUpdateAnything()
    }

    @Nested
    inner class `Given Character Exists` {

        init {
            characterRepository.givenCharacter(character)
        }

        @Test
        fun `given character does not have requested name - should return error`() {
            val error = renameCharacter(nonBlankStr("Nonexistent name")).exceptionOrNull()

            error.shouldBeEqualTo(CharacterDoesNotHaveNameVariant(character.id, "Nonexistent name"))
            shouldNotProduceOutput()
            shouldNotUpdateAnything()
        }

        @Nested
        inner class `Given Character has Requested Name` {

            @Test
            fun `should return success`() {
                val result = renameCharacter(character.displayName)

                result.isSuccess.shouldBeTrue()
            }

            @Test
            fun `should produce character renamed event`() {
                renameCharacter(character.displayName)

                characterRenamed!!.shouldBeEqualTo(
                    CharacterRenamed(
                        character.id,
                        character.displayName.value,
                        inputName.value
                    )
                )
            }

            @Test
            fun `should update character`() {
                renameCharacter(character.displayName)

                updatedCharacter!!.id.shouldBeEqualTo(character.id)
                updatedCharacter!!.names.shouldContain(inputName)
                updatedCharacter!!.names.shouldNotContain(character.displayName)
            }

            @Test
            fun `given replacement is same - should return duplicate error`() {
                val error = renameCharacter(character.displayName, character.displayName).exceptionOrNull()

                error.shouldBeEqualTo(CharacterAlreadyHasName(character.id, character.displayName.value))
                shouldNotUpdateAnything()
                shouldNotProduceOutput()
            }

            @Test
            fun `given no prose mentions character - should not update prose`() {
                val character = character.withName(nonBlankStr()).character
                    .also(characterRepository::givenCharacter)
                proseRepository.givenProse(buildProse(projectId = character.projectId!!) {
                    """
                        ${character{names.secondaryNames.first()}}
                    """.trimIndent()
                })

                renameCharacter(character.displayName)

                updatedProse.shouldBeEmpty()
                mentionTextReplaced.shouldBeEmpty()
            }

            @Nested
            inner class `Given Prose Mentions Character by Name` {

                private val prose = List(3) {
                    buildProse(projectId = character.projectId!!) {
                        "${character{names.displayName}} is a cool guy x$it"
                    }
                }.onEach(proseRepository::givenProse)

                @Test
                fun `should update prose`() {
                    renameCharacter(character.displayName)

                    updatedProse.size.shouldBeEqualTo(3)
                    updatedProse.map { it.id }.toSet().shouldBeEqualTo(prose.map{ it.id}.toSet())
                    updatedProse.forEach { prose ->
                        prose.mentions.filter { it.entityId.id == character.id }.forEach {
                            it.text.toString().shouldNotBeEqualTo(character.displayName.value)
                        }
                    }
                }

                @Test
                fun `should output mention text replaced events`() {
                    renameCharacter(character.displayName)

                    mentionTextReplaced.shouldHaveSize(3)
                    mentionTextReplaced.map { it.proseId }.toSet().shouldBeEqualTo(prose.map{ it.id}.toSet())
                }

            }

        }

    }

    private fun renameCharacter(name: NonBlankString = characterName(), replacement: NonBlankString = inputName): Result<Nothing?> {
        val useCase: RenameCharacter = RenameCharacterUseCase(characterRepository, proseRepository)
        val request = RenameCharacter.RequestModel(character.id, name, replacement)
        return runBlocking {
            useCase(request, object : RenameCharacter.OutputPort {
                override suspend fun characterRenamed(characterRenamed: CharacterRenamed) {
                    this@`Rename Character Unit Test`.characterRenamed = characterRenamed
                }

                override suspend fun mentionTextReplaced(mentionTextReplaced: MentionTextReplaced) {
                    this@`Rename Character Unit Test`.mentionTextReplaced.add(mentionTextReplaced)
                }
            })
        }
    }

    private fun shouldNotProduceOutput() {
        characterRenamed.shouldBeNull()
        mentionTextReplaced.shouldBeEmpty()
    }

    private fun shouldNotUpdateAnything() {
        updatedCharacter.shouldBeNull()
        updatedProse.shouldBeEmpty()
    }

/*

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
        characterRepository.givenCharacter(character.withName(inputName).character)
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
*/
}