/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:47 PM
 */
package com.soyle.studio.theme

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.soyle.studio.common.AggregateRoot
import com.soyle.studio.common.DomainEvent
import com.soyle.studio.common.PairOf
import com.soyle.studio.theme.entities.CharacterArcSection
import com.soyle.studio.theme.events.*
import com.soyle.studio.theme.valueobjects.CharacterArc
import com.soyle.studio.theme.valueobjects.CharacterComparison
import com.soyle.studio.theme.valueobjects.CharacterThematicValueSet
import java.util.*

class Theme private constructor(
	override val id: Id,
	val projectId: UUID,
	val line: String,
	val characterThematicValues: Map<UUID, CharacterThematicValueSet>,
	override val events: List<DomainEvent<Id>>
) : AggregateRoot<Theme.Id> {

	val arcSectionTypeSet
		get() = DefaultArcSectionSet

	constructor(id: Id, projectId: UUID, line: String, characterThematicValues: Map<UUID, CharacterThematicValueSet>) : this(
		id,
		projectId,
		line,
		characterThematicValues,
		emptyList()
	)

	private fun copy(
		line: String = this.line,
		characterThematicValues: Map<UUID, CharacterThematicValueSet> = this.characterThematicValues,
		events: List<DomainEvent<Id>> = this.events
	) = Theme(id, projectId, line, characterThematicValues, events)

	private fun silentFailure() = this.right()
	private fun initialArcSections() = arcSectionTypeSet.filter { it.usedInCharacterComp }.map {
		CharacterArcSection(
			CharacterArcSection.Id(UUID.randomUUID()),
			it
		)
	}

	fun includeCharacters(characters: List<UUID>): Either<*, Theme> {
		// prevent duplicates
		val newCharacters = characters.filter { it !in characterThematicValues }
		if (newCharacters.isEmpty()) return silentFailure()

		// for each new character, create a new character comparison
		val newComparisons = newCharacters.associateWith {
			CharacterComparison(initialArcSections())
		}

		return copy(
			characterThematicValues = characterThematicValues + newComparisons,
			events = events + CharactersAddedToTheme(id, newCharacters.toSet()) +
			  newComparisons.flatMap { (characterId, comparison) ->
				  comparison.sections.map {
					  CharacterArcSectionCreated(id, characterId, it.id, it.type)
				  }
			  }
		).right()
	}

	fun createCharacterArc(characterId: UUID): Either<*, Theme> {

		val thematicValueSet = characterThematicValues[characterId]
			?: return CannotCreateCharacterArcForCharacterNotInTheme(characterId).left()

		if (thematicValueSet !is CharacterComparison)
			return CannotCreateCharacterArcIfAlreadyCreatedInTheme(characterId).left()

		val newArcSections = arcSectionTypeSet
			.filter { it.isRequired && ! it.usedInCharacterComp }
			.map {
				CharacterArcSection(CharacterArcSection.Id(UUID.randomUUID()), it)
			}

		val arcSectionCreationEvents = newArcSections
			.map {
				CharacterArcSectionCreated(id, characterId, it.id, it.type)
			}

		return copy(
			characterThematicValues = characterThematicValues.minus(characterId).plus(
				characterId to thematicValueSet.createCharacterArc().addSections(
					newArcSections.toList()
				)
			),
			events = events + CharacterArcCreated(id, characterId) + arcSectionCreationEvents
		).right()
	}

	fun excludeCharacters(characters: List<UUID>): Either<*, Theme> {

		val toRemove = characters.filter { it in characterThematicValues }
		if (toRemove.isEmpty()) return this.right()

		val removedArcs = toRemove.map(characterThematicValues::getValue)
		if (removedArcs.any { it is CharacterArc }) {
			return CannotExcludeCharactersWithACharacterArc(
				toRemove.filter { characterThematicValues.getValue(it) is CharacterArc },
				toRemove.filterNot { characterThematicValues.getValue(it) is CharacterArc }
			).left()
		}

		return copy(
			characterThematicValues = characterThematicValues.minus(toRemove),
			events = events + CharactersRemovedFromTheme(id, toRemove)
		).right()
	}

	data class Id(val uniqueId: UUID)

	companion object {

		fun takeNoteOf(projectId: UUID, line: String, characterId: UUID): Either<*, Theme> {
			val themeId = Id(UUID.randomUUID())
			return Theme(
				themeId, projectId, line, mapOf(), listOf(
					ThemeCreated(
						projectId,
						themeId
					)
				)
			)
				.includeCharacters(listOf(characterId))
				.flatMap { it.createCharacterArc(characterId) }
		}

		private fun createNewThemeAsCopy(theme: Theme, characterId: UUID): Either<Any?, Theme> {
			val themeId = Id(UUID.randomUUID())
			return Theme(
				themeId, theme.projectId, "", mapOf(), listOf(
					ThemeCreated(
						theme.projectId,
						themeId
					)
				)
			)
				.includeCharacters(theme.characterThematicValues.keys.toList())
				.map {
					it.copy(
						characterThematicValues = it.characterThematicValues
							.minus(characterId)
							.plus(characterId to theme.characterThematicValues.getValue(characterId).let {
								CharacterArc(it.sections.map {
									CharacterArcSection(CharacterArcSection.Id(UUID.randomUUID()), it.type)
								})
							})
					)
				}
		}

		fun separateCharacterArcFromTheme(theme: Theme, characterId: UUID): Either<*, PairOf<Theme>> {

			val arc = theme.characterThematicValues[characterId] ?: return Either.left(
				CannotSeparateCharacterArcNotInTheme(
					characterId
				)
			)

			if (arc !is CharacterArc) return CannotSeparateCharacterArcNotYetCreated(characterId).left()

			val newTheme = createNewThemeAsCopy(theme, characterId)

			if (newTheme !is Either.Right) return newTheme as Either.Left

			val oldTheme = theme.copy(
				characterThematicValues = theme.characterThematicValues
					.minus(characterId)
					.plus(characterId to arc.removeCharacterArc())
			).excludeCharacters(listOf(characterId))

			if (oldTheme !is Either.Right) return oldTheme as Either.Left

			return (oldTheme.b to newTheme.b).right()

		}
	}

}