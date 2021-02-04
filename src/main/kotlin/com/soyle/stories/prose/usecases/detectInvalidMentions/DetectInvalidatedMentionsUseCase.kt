package com.soyle.stories.prose.usecases.detectInvalidMentions

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.prose.repositories.ProseRepository
import com.soyle.stories.prose.repositories.getProseOrError
import com.soyle.stories.theme.repositories.ThemeRepository

class DetectInvalidatedMentionsUseCase(
    private val proseRepository: ProseRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository,
    private val themeRepository: ThemeRepository
) : DetectInvalidatedMentions {
    override suspend fun invoke(proseId: Prose.Id, output: DetectInvalidatedMentions.OutputPort) {
        val prose = proseRepository.getProseOrError(proseId)
        output.receiveDetectedInvalidatedMentions(
            DetectInvalidatedMentions.ResponseModel(
                proseId,
                prose.getNonExistingMentionIds().toSet()
            )
        )
    }

    private suspend fun Prose.getNonExistingMentionIds(): List<MentionedEntityId<*>> {
        return mentions
            .groupBy { it.entityId::class }
            .flatMap { (_, mentions) ->
                val firstMention = mentions.first()
                when (firstMention.entityId) {
                    is MentionedCharacterId -> getNonExistingCharacterIds(mentions)
                    is MentionedLocationId -> getNonExistingLocationIds(mentions)
                    is MentionedSymbolId -> getNonExistingSymbolIds(mentions)
                }
            }
    }

    private suspend fun getNonExistingCharacterIds(mentions: List<ProseMention<*>>): List<MentionedCharacterId>
    {
        return characterRepository.getCharacterIdsThatDoNotExist(mentions.map { it.entityId.id as Character.Id }.toSet()).map {
            it.mentioned()
        }
    }

    private suspend fun getNonExistingLocationIds(mentions: List<ProseMention<*>>): List<MentionedLocationId>
    {
        return locationRepository.getLocationIdsThatDoNotExist(mentions.map { it.entityId.id as Location.Id }.toSet()).map {
            it.mentioned()
        }
    }

    private suspend fun getNonExistingSymbolIds(mentions: List<ProseMention<*>>): List<MentionedSymbolId>
    {
        val mentionedSymbolIds = mentions.associateBy { it.entityId.id as Symbol.Id }
        return themeRepository.getSymbolIdsThatDoNotExist(mentionedSymbolIds.keys).map {
            mentionedSymbolIds.getValue(it).entityId as MentionedSymbolId
        }
    }
}