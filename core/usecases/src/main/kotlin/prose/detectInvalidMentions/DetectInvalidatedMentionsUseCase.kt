package com.soyle.stories.usecase.prose.detectInvalidMentions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.prose.*
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.theme.ThemeRepository

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
        val mentionedCharacterIdSet = mentions.map { it.entityId.id as Character.Id }.toSet()
        val nonExistingIds = characterRepository.getCharacterIdsThatDoNotExist(mentionedCharacterIdSet)
        val remainingIds = mentionedCharacterIdSet.filterNot { it in nonExistingIds }
        val charactersToInspect = characterRepository.getCharacters(remainingIds.toSet())
        assert(charactersToInspect.size == remainingIds.size)
        return characterRepository.getCharacterIdsThatDoNotExist(mentionedCharacterIdSet).map {
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