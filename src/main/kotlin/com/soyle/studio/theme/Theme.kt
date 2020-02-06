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
import java.util.*

class Theme private constructor(
	override val id: Id,
	val projectId: UUID,
	val line: String,
	val characterArcs: Map<UUID, CharacterArc>,
	override val events: List<DomainEvent<Id>>
) : AggregateRoot<Theme.Id> {

	val arcSectionTypeSet
		get() = DefaultArcSectionSet

	constructor(id: Id, projectId: UUID, line: String, characterArcs: Map<UUID, CharacterArc>) : this(
		id,
		projectId,
		line,
		characterArcs,
		emptyList()
	)

	private fun copy(
		line: String = this.line,
		characterArcs: Map<UUID, CharacterArc> = this.characterArcs,
		events: List<DomainEvent<Id>> = this.events
	) = Theme(id, projectId, line, characterArcs, events)

	private fun initialArcSections() = arcSectionTypeSet.filter { it.usedInCharacterComp }.map {
		CharacterArcSection(
			CharacterArcSection.Id(UUID.randomUUID()),
			it
		)
	}

	fun includeCharacters(characters: List<UUID>): Either<*, Theme> {
		val newCharacters = characters.filter { it !in characterArcs }
		if (newCharacters.isEmpty()) return this.right()
		val newArcs = newCharacters.associateWith { CharacterArc(false, initialArcSections()) }
		return copy(
			characterArcs = characterArcs + newArcs,
			events = events + CharactersAddedToTheme(
				id,
				newCharacters.toSet()
			) + newArcs.flatMap { (characterId, arc) ->
				arc.sections.map {
					CharacterArcSectionCreated(id, characterId, it.id, it.type)
				}
			}
		).right()
	}

	fun createCharacterArc(characterId: UUID): Either<*, Theme> {
		val characterArc = characterArcs[characterId]
			?: return CannotCreateCharacterArcForCharacterNotInTheme(characterId).left()
		val newArcSections = arcSectionTypeSet.asSequence()
			.filter { it.isRequired }
			.filterNot { it.usedInCharacterComp }
			.map {
				CharacterArcSection(CharacterArcSection.Id(UUID.randomUUID()), it)
			}
		val arcSectionCreationEvents = newArcSections
			.map {
				CharacterArcSectionCreated(id, characterId, it.id, it.type)
			}

		return copy(
			characterArcs = characterArcs.minus(characterId).plus(
				characterId to characterArc.markCreated().addSections(
					newArcSections.toList()
				)
			),
			events = events + CharacterArcCreated(id, characterId) + arcSectionCreationEvents
		).right()
	}

	fun excludeCharacters(characters: List<UUID>): Either<*, Theme> {
		val toRemove = characters.filter { it in characterArcs }
		if (toRemove.isEmpty()) return this.right()
		val removedArcs = toRemove.map(characterArcs::getValue)
		if (removedArcs.any { it.explicitlyCreated }) {
			return CannotExcludeCharactersWithExplicitlyCreatedArcs(
				toRemove.filter { characterArcs.getValue(it).explicitlyCreated },
				toRemove.filterNot { characterArcs.getValue(it).explicitlyCreated }
			).left()
		}
		return copy(
			characterArcs = characterArcs.minus(toRemove),
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
				.includeCharacters(theme.characterArcs.keys.toList())
				.map {
					it.copy(
						characterArcs = it.characterArcs
							.minus(characterId)
							.plus(characterId to it.characterArcs.getValue(characterId).markCreated())
					)
				}
		}

		fun separateCharacterArcFromTheme(theme: Theme, characterId: UUID): Either<*, PairOf<Theme>> {

			val arc = theme.characterArcs[characterId] ?: return Either.left(
				CannotSeparateCharacterArcNotInTheme(
					characterId
				)
			)

			if (!arc.explicitlyCreated) return CannotSeparateCharacterArcNotYetCreated(characterId).left()

			val newTheme = createNewThemeAsCopy(theme, characterId)

			if (newTheme !is Either.Right) return newTheme as Either.Left

			val oldTheme = theme.copy(
				characterArcs = theme.characterArcs
					.minus(characterId)
					.plus(characterId to (theme.characterArcs.getValue(characterId).markImplicit()))
			).excludeCharacters(listOf(characterId))

			if (oldTheme !is Either.Right) return oldTheme as Either.Left

			return (oldTheme.b to newTheme.b).right()

		}
	}

}