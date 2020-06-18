package com.soyle.stories.characterarc.usecases.planNewCharacterArc

import arrow.core.Either
import arrow.core.flatMap
import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter
import com.soyle.stories.translators.asCharacterArcTemplateSection
import java.util.*

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 3:39 PM
 */
class PlanNewCharacterArcUseCase(
    private val characterRepository: com.soyle.stories.characterarc.repositories.CharacterRepository,
    private val themeRepository: com.soyle.stories.characterarc.repositories.ThemeRepository,
    private val characterArcSectionRepository: CharacterArcSectionRepository,
    private val promoteMinorCharacter: PromoteMinorCharacter
) : PlanNewCharacterArc {

    override suspend fun invoke(
        characterId: UUID,
        name: String,
        outputPort: PlanNewCharacterArc.OutputPort
    ) {
        val character = getCharacterById(characterId) {
            return outputPort.receivePlanNewCharacterArcFailure(it)
        }

        val creationResult = Theme.takeNoteOf(Project.Id(), "")
            .map { theme ->
                theme to theme.thematicTemplate.sections.map {
                    CharacterArcSection(
                        CharacterArcSection.Id(
                            UUID.randomUUID()
                        ), character.id, theme.id, it.asCharacterArcTemplateSection(), null, ""
                    )
                }
            }.flatMap { (theme, initialSections) ->
                theme.includeCharacter(character, initialSections).map {
                    it to initialSections
                }
            }

        // cannot wrap in a map {  } call because repositories use suspend functions and map {  } breaks the suspend scope
        if (creationResult is Either.Right) {
            val (theme, initialSections) = creationResult.b
            themeRepository.addNewTheme(theme)
            characterArcSectionRepository.addNewCharacterArcSections(initialSections)
            promoteMinorCharacter.invoke(
                PromoteMinorCharacter.RequestModel(
                    theme.id.uuid,
                    characterId,
                    name
                ),
                object : PromoteMinorCharacter.OutputPort {
                    override fun receivePromoteMinorCharacterFailure(failure: ThemeException) {
                        outputPort.receivePlanNewCharacterArcFailure(failure)
                    }

                    override fun receivePromoteMinorCharacterResponse(response: PromoteMinorCharacter.ResponseModel) {
                        outputPort.receivePlanNewCharacterArcResponse(
                            com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem(
                                response.characterId,
                                response.characterArcName,
                                response.themeId
                            )
                        )
                    }
                }
            )
        }

    }

    private suspend inline fun getCharacterById(
        characterId: UUID,
        ifNotFound: (CharacterDoesNotExist) -> Character
    ): Character {
        return characterRepository.getCharacterById(Character.Id(characterId)) ?: ifNotFound(
            CharacterDoesNotExist(
                characterId
            )
        )
    }
}