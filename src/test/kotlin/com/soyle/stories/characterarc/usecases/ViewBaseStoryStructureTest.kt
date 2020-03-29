package com.soyle.stories.characterarc.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.soyle.stories.entities.*
import com.soyle.stories.theme.*
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:48 AM
 */
class ViewBaseStoryStructureTest {

    private fun given(themes: List<Theme>): (UUID, UUID) -> Either<Exception, com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure.ResponseModel> {
        val repo = object : com.soyle.stories.characterarc.repositories.ThemeRepository, CharacterArcSectionRepository {
            override suspend fun addNewTheme(theme: Theme) = Unit
            suspend fun getCharacterArcByCharacterAndThemeId(
                characterId: Character.Id,
                themeId: Theme.Id
            ): CharacterArc? = themes.find { it.id == themeId }?.getIncludedCharacterById(characterId)?.let {
                val existingSections = it.thematicSections.map { it.template.characterArcTemplateSectionId }.toSet()
                val newSections =
                    CharacterArcTemplate.default().sections.filter { it.id !in existingSections }.map { template ->
                        CharacterArcSection(
                            CharacterArcSection.Id(
                                UUID.randomUUID()
                            ), it.id, themeId, template, ""
                        )
                    }
                CharacterArc(
                    it.id,
                    CharacterArcTemplate.default(),
                    themeId,
                    "Name"
                )
            }

            override suspend fun getThemeById(themeId: Theme.Id): Theme? = themes.find { it.id == themeId }
            override suspend fun addNewCharacterArcSections(characterArcSections: List<CharacterArcSection>) {}
            override suspend fun getCharacterArcSectionById(characterArcSectionId: CharacterArcSection.Id): CharacterArcSection? =
                null

            override suspend fun getCharacterArcSectionsForCharacterInTheme(
                characterId: Character.Id,
                themeId: Theme.Id
            ): List<CharacterArcSection> = themes.find { it.id == themeId }?.getIncludedCharacterById(characterId)?.let {
                CharacterArcTemplate.default().sections.map { template ->
                    CharacterArcSection(
                        CharacterArcSection.Id(
                            UUID.randomUUID()
                        ), it.id, themeId, template, ""
                    )
                }
            } ?: emptyList()

            override suspend fun removeArcSections(sections: List<CharacterArcSection>) {}
            override suspend fun updateCharacterArcSection(characterArcSection: CharacterArcSection) {}
            override suspend fun getCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection> =
                emptyList()

            override suspend fun getCharacterArcSectionsById(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArcSection> {
                TODO("Not yet implemented")
            }

            override suspend fun getCharacterArcSectionsForTheme(themeId: Theme.Id): List<CharacterArcSection> =emptyList()
        }
        val useCase: com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure =
            com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructureUseCase(
                repo,
                repo
            )
        val output = object : com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure.OutputPort {
            var result: Either<Exception, com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure.ResponseModel>? = null
            override fun receiveViewBaseStoryStructureFailure(failure: Exception) {
                result = failure.left()
            }

            override fun receiveViewBaseStoryStructureResponse(response: com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure.ResponseModel) {
                result = response.right()
            }
        }
        return { characterId, themeId ->
            runBlocking {
                useCase.invoke(characterId, themeId, output)
            }
            output.result!!
        }
    }

    val characterUUID = UUID.randomUUID()
    val themeUUID = UUID.randomUUID()
    val character = Character(
        Character.Id(characterUUID), UUID.randomUUID(), "Character Name"
    )

    @Nested
    inner class GivenThemeDoesNotExist {

        val useCase = given(emptyList())

        @Test
        fun shouldOutputThemeDoesNotExistError() {
            val (error) = useCase.invoke(characterUUID, themeUUID) as Either.Left
            error as ThemeDoesNotExist
            assertEquals(themeUUID, error.themeId)
        }

    }

    @Nested
    inner class GivenCharacterNotInRequestedTheme {

        val useCase = given(
            themes = listOf(
                Theme(
                    Theme.Id(
                        themeUUID
                    ), "", mapOf(), mapOf()
                )
            )
        )

        @Test
        fun shouldOutputCharacterNotInRequestedThemeError() {
            val (error) = useCase.invoke(characterUUID, themeUUID) as Either.Left
            error as CharacterNotInTheme
            assertEquals(characterUUID, error.characterId)
            assertEquals(themeUUID, error.themeId)
        }

    }

    @Nested
    inner class GivenCharacterIsNotMajorCharacterInTheme {

        val useCase = given(
            listOf(
                (Theme(
                    Theme.Id(themeUUID),
                    "",
                    mapOf(),
                    mapOf()
                ).includeCharacter(character) as Either.Right).b
            )
        )

        @Test
        fun shouldOutputCharacterDoesNotHaveCharacterArcInThemeError() {
            val (error) = useCase.invoke(characterUUID, themeUUID) as Either.Left
            error as CharacterIsNotMajorCharacterInTheme
            assertEquals(characterUUID, error.characterId)
            assertEquals(themeUUID, error.themeId)
        }

    }

    @Nested
    inner class GivenCharacterWithArcInTheme {

        val theme = (Theme(
            Theme.Id(
                themeUUID
            ), "", mapOf(), mapOf()
        )
            .includeCharacter(character)
            .flatMap { it.promoteCharacter(it.getMinorCharacterById(character.id)!!) }
                as Either.Right
                ).b

        val useCase = given(
            listOf(
                theme
            )
        )

        @Test
        fun onlyRequiredSectionsShouldBeInOutput() {
            val (baseStoryStructure) = useCase.invoke(characterUUID, themeUUID) as Either.Right
            val requiredSections = CharacterArcTemplate.default().sections.map { it.name }.toSet()
            assertEquals(requiredSections, baseStoryStructure.sections.map { it.templateName }.toSet())
        }

    }

}