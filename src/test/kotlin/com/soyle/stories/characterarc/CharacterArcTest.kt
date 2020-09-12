package com.soyle.stories.characterarc

import com.soyle.stories.common.str
import com.soyle.stories.common.template
import com.soyle.stories.entities.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class CharacterArcTest {

	val themeId = Theme.Id()
	val characterId = Character.Id()
	val name = "Test Character Arc"
	val arc = CharacterArc.planNewCharacterArc(characterId, themeId, name)

	@Test
	fun `character arcs are entities`() {
		assertTrue(arc isSameEntityAs arc)
		assertTrue(arc.withNewName("New Name") isSameEntityAs arc)
	}

	@Test
	fun `character Arcs outline the growth of a Character`() {
		assertEquals(characterId.uuid, arc.characterId.uuid)
	}

	@Test
	fun `character arcs are part of a theme`() {
		assertEquals(themeId.uuid, arc.themeId.uuid)
	}

	@Test
	fun `character arcs have a name`() {
		assertEquals(name, arc.name)
	}

	@Test
	fun `character arcs have required character arc sections`() {
		val requiredSections = listOf(
			template("Template ${str()}"),
			template("Template ${str()}"),
			template("Template ${str()}")
		)
		val template = CharacterArcTemplate(listOf(
			template("", false)
		) + requiredSections)
		val arc = CharacterArc.planNewCharacterArc(characterId, themeId, name, template)
		assertEquals(requiredSections.size, arc.arcSections.size)
		requiredSections.forEach { templateSection ->
			arc.arcSections.find { it.template isSameEntityAs templateSection }!!
		}
	}

}