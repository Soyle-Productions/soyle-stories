package com.soyle.stories.characterarc

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Theme
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class CharacterArcTest {

	val themeUUID = UUID.randomUUID()
	val characterUUID = UUID.randomUUID()
	val name = "Test Character Arc"
	val arc = CharacterArc.planNewCharacterArc(Character.Id(characterUUID), Theme.Id(themeUUID), name)

	@Test
	fun `character arcs are entities`() {
		assertTrue(arc isSameEntityAs arc)
		assertTrue(arc.withNewName("New Name") isSameEntityAs arc)
	}

	@Test
	fun `character Arcs outline the growth of a Character`() {
		assertEquals(characterUUID, arc.characterId.uuid)
	}

	@Test
	fun `character arcs are part of a theme`() {
		assertEquals(themeUUID, arc.themeId.uuid)
	}

	@Test
	fun `character arcs have a name`() {
		assertEquals(name, arc.name)
	}

}