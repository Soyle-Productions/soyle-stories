package com.soyle.stories.prose.usecases.detectInvalidMentions

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.*
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.prose.repositories.ProseRepository
import com.soyle.stories.prose.repositories.getProseOrError

class DetectInvalidatedMentionsUseCase(
    private val proseRepository: ProseRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
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
}