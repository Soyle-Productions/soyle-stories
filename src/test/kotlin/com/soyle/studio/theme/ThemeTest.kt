package com.soyle.studio.theme

import arrow.core.Either
import arrow.core.extensions.sequence.foldable.size
import arrow.core.flatMap
import arrow.core.right
import com.soyle.studio.common.`when`
import com.soyle.studio.common.then
import com.soyle.studio.common.thenFailWith
import com.soyle.studio.theme.events.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:46 PM
 */
class ThemeTest {


	private val testId = UUID.randomUUID()
	private val testLine = "Something profound, I'm sure"
	private val testCharacters = listOf<UUID>(
		UUID.randomUUID(),
		UUID.randomUUID(),
		UUID.randomUUID(),
		UUID.randomUUID(),
		UUID.randomUUID()
	)

	inline fun given(state: () -> Either<*, Theme>): Theme {
		val (theme) = state() as Either.Right
		return Theme(theme.id, theme.projectId, theme.line, theme.characterArcs)
	}

	@Test
	fun canTakeNoteOfNewTheme() {
		`when` {
			Theme.takeNoteOf(testId, testLine, testCharacters.first())
		} then {
			assertEquals(ThemeCreated(testId, id), events.component1())
			assertEquals(CharactersAddedToTheme(id, setOf(testCharacters.first())), events.component2())
			assert(events.contains(CharacterArcCreated(id, testCharacters.first())))
		}
	}

	@Test
	fun canIncludeCharacters() {
		given {
			Theme(Theme.Id(testId), UUID.randomUUID(), testLine, mapOf()).right()
		} `when` {
			includeCharacters(testCharacters)
		} then {
			assert(events.contains(CharactersAddedToTheme(Theme.Id(testId), testCharacters.toSet())))
			assertEquals(
				testCharacters.size * arcSectionTypeSet.filter { it.usedInCharacterComp }.size,
				events.filterIsInstance<CharacterArcSectionCreated>().size
			)
		}
	}

	@Test
	fun cannotIncludeCharactersTwice() {
		given {
			Theme(Theme.Id(testId), UUID.randomUUID(), testLine, mapOf())
				.includeCharacters(testCharacters.subList(0, 2))
		} `when` {
			includeCharacters(testCharacters)
		} then {
			assertEquals(
				CharactersAddedToTheme(Theme.Id(testId), testCharacters.subList(2, 5).toSet()), events.component1()
			)
		}
	}

	@Test
	fun createCharacterArcShouldProduceCharacterArcCreatedEvent() {
		given {
			Theme(Theme.Id(testId), UUID.randomUUID(), testLine, mapOf())
				.includeCharacters(testCharacters)
		} `when` {
			createCharacterArc(testCharacters.first())
		} then {
			assertEquals(
				CharacterArcCreated(Theme.Id(testId), testCharacters.first()),
				events.filterIsInstance<CharacterArcCreated>().single()
			)
		}
	}

	@Test
	fun creatingACharacterArcShouldCreateRequiredArcSections() {
		given {
			Theme(Theme.Id(testId), UUID.randomUUID(), testLine, mapOf())
				.includeCharacters(testCharacters)
		} `when` {
			createCharacterArc(testCharacters.first())
		} then {
			assertEquals(
				arcSectionTypeSet.asSequence()
					.filter { it.isRequired }
					.filterNot { it.usedInCharacterComp } // these should have all been added already
					.count(),
				events.filterIsInstance<CharacterArcSectionCreated>().size
			)
			assertEquals(
				arcSectionTypeSet.filter { it.isRequired || it.usedInCharacterComp }.size,
				characterArcs.getValue(testCharacters.first()).sections.size
			)

		}
	}

	@Test
	fun cannotCreateCharacterArcsThatAreNotInTheme() {
		given {
			Theme(Theme.Id(testId), UUID.randomUUID(), testLine, mapOf())
				.includeCharacters(testCharacters.subList(0, 2))
		} `when` {
			createCharacterArc(testCharacters.component5())
		} thenFailWith {
			CannotCreateCharacterArcForCharacterNotInTheme(testCharacters.component5())
		}
	}

	@Test
	fun canExcludeCharacters() {
		given {
			Theme(Theme.Id(testId), UUID.randomUUID(), testLine, mapOf())
				.includeCharacters(testCharacters)
		} `when` {
			excludeCharacters(listOf(testCharacters.first()))
		} then {
			assertEquals(
				CharactersRemovedFromTheme(Theme.Id(testId), listOf(testCharacters.first())),
				events.filterIsInstance<CharactersRemovedFromTheme>().single()
			)
		}
	}

	@Test
	fun cannotExcludeCharactersNotInTheme() {
		given {
			Theme(Theme.Id(testId), UUID.randomUUID(), testLine, mapOf()).right()
		} `when` {
			excludeCharacters(listOf(testCharacters.first()))
		} then {
			assert(events.isEmpty())
		}
	}

	@Test
	fun cannotExcludeCharactersWithExplicitlyCreatedArcs() {
		given {
			Theme(Theme.Id(testId), UUID.randomUUID(), testLine, mapOf())
				.includeCharacters(testCharacters)
				.flatMap { it.createCharacterArc(testCharacters.first()) }
		} `when` {
			excludeCharacters(testCharacters)
		} thenFailWith  {
			CannotExcludeCharactersWithExplicitlyCreatedArcs(
				listOf(testCharacters.first()),
				testCharacters.drop(1)
			)
		}
	}

	@Test
	fun removingACharacterArcFromAThemeCreatesANewTheme() {
		given {
			Theme(Theme.Id(testId), UUID.randomUUID(), testLine, mapOf())
				.includeCharacters(testCharacters)
				.flatMap { it.createCharacterArc(testCharacters.first()) }
		} `when` {
			Theme.separateCharacterArcFromTheme(this, testCharacters.first())
		} then {
			assertEquals(CharactersRemovedFromTheme(Theme.Id(testId), listOf(testCharacters.first())), first.events.component1())
			assertEquals(ThemeCreated(first.projectId, second.id), second.events.component1())
			assertEquals(CharactersAddedToTheme(second.id, testCharacters.toSet()), second.events.component2())
		}
	}
}