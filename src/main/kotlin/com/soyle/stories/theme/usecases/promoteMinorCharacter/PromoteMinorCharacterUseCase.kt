package com.soyle.stories.theme.usecases.promoteMinorCharacter

import arrow.core.Either
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.CreatedCharacterArc
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.characterInTheme.MinorCharacter
import com.soyle.stories.theme.CharacterArcAlreadyExistsForCharacterInTheme
import com.soyle.stories.theme.CharacterIsAlreadyMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class PromoteMinorCharacterUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository,
    private val characterArcSectionRepository: CharacterArcSectionRepository
) : PromoteMinorCharacter {
    override suspend fun invoke(
        request: PromoteMinorCharacter.RequestModel,
        output: PromoteMinorCharacter.OutputPort
    ) {
        val theme = themeRepository.getThemeById(Theme.Id(request.themeId))
            ?: throw ThemeDoesNotExist(request.themeId)
        val characterInTheme = theme.getIncludedCharacterById(Character.Id(request.characterId))
            ?: throw CharacterNotInTheme(
                request.themeId,
                request.characterId
            )

        if (characterInTheme !is MinorCharacter) {
            throw CharacterIsAlreadyMajorCharacterInTheme(
                request.characterId,
                request.themeId
            )
        }

        val arcsInTheme = characterArcRepository.listCharacterArcsForTheme(theme.id)
        val existingArc = arcsInTheme.find { it.characterId == characterInTheme.id }
        if (existingArc != null) {
            throw CharacterArcAlreadyExistsForCharacterInTheme(
                request.characterId,
                request.themeId
            )
        }
        val thematicTemplateSectionIds =
            characterInTheme.thematicSections.map { it.template.characterArcTemplateSectionId }.toSet()
        val templateSectionsToMake =
            CharacterArcTemplate.default().sections.filterNot { it.id in thematicTemplateSectionIds }
        val newSections =
            templateSectionsToMake.map {
                CharacterArcSection(
                    CharacterArcSection.Id(UUID.randomUUID()),
                    characterInTheme.id, theme.id, it,
                    null,
                    ""
                )
            }
        val promotionResult = theme.withCharacterPromoted(characterInTheme.id)
            .let { promotedTheme ->
                CharacterArc.planNewCharacterArc(
                    characterInTheme.id,
                    theme.id,
                    theme.name
                ).map { promotedTheme to it }
            }
        if (promotionResult is Either.Right) {
            themeRepository.updateTheme(promotionResult.b.first)
            characterArcRepository.addNewCharacterArc(promotionResult.b.second)
            characterArcSectionRepository.addNewCharacterArcSections(newSections)
            output.receivePromoteMinorCharacterResponse(
                PromoteMinorCharacter.ResponseModel(
                    CreatedCharacterArc(
                        request.themeId,
                        request.characterId,
                        promotionResult.b.second.name
                    )
                )
            )
        }
    }
}