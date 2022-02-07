package com.soyle.stories.usecase.prose.detectInvalidMentions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.prose.*
import com.soyle.stories.domain.prose.content.ProseContent
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

    private suspend fun getNonExistingCharacterIds(mentions: List<ProseContent.Mention<*>>): List<MentionedCharacterId>
    {
        val mentionsByCharacterId = mentions.groupBy { it.entityId.id as Character.Id }
        val nonExistingIds = characterRepository.getCharacterIdsThatDoNotExist(mentionsByCharacterId.keys)

        val remainingIds = mentionsByCharacterId.keys.filterNot { it in nonExistingIds }
        val charactersToInspect = characterRepository.getCharacters(remainingIds.toSet())
        assert(charactersToInspect.size == remainingIds.size)

        return nonExistingIds.map { it.mentioned() } + charactersToInspect.filter {
            it.projectId == null || run {
                val otherNameSet = it.names.map { it.value }.toSet()
                mentionsByCharacterId[it.id].orEmpty().any { it.text.toString() !in otherNameSet }
            }
        }.map { it.id.mentioned() }
    }

    private suspend fun getNonExistingLocationIds(mentions: List<ProseContent.Mention<*>>): List<MentionedLocationId>
    {
        return locationRepository.getLocationIdsThatDoNotExist(mentions.map { it.entityId.id as Location.Id }.toSet()).map {
            it.mentioned()
        }
    }

    private suspend fun getNonExistingSymbolIds(mentions: List<ProseContent.Mention<*>>): List<MentionedSymbolId>
    {
        val mentionedSymbolIds = mentions.associateBy { it.entityId.id as Symbol.Id }
        return themeRepository.getSymbolIdsThatDoNotExist(mentionedSymbolIds.keys).map {
            mentionedSymbolIds.getValue(it).entityId as MentionedSymbolId
        }
    }
}