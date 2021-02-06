package com.soyle.stories.domain.character

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.Theme
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

	@Test
	fun `Cannot add section with the same template if it does not allow multiple`() {
		val templateWithMaxOfOne = template("", false, multiple = false)
		val template = CharacterArcTemplate(listOf(
			templateWithMaxOfOne
		))
		val arc = CharacterArc.planNewCharacterArc(characterId, themeId, name, template)
			.withArcSection(templateWithMaxOfOne)
		assertThrows<CharacterArcAlreadyContainsMaximumNumberOfTemplateSection> {
			arc.withArcSection(templateWithMaxOfOne)
		}.run {
			arcId.mustEqual(arc.id.uuid)
			characterId.mustEqual(arc.characterId.uuid)
			themeId.mustEqual(arc.themeId.uuid)
			templateSectionId.mustEqual(templateWithMaxOfOne.id.uuid)
		}
		assertThrows<CharacterArcAlreadyContainsMaximumNumberOfTemplateSection> {
			arc.withArcSection(CharacterArcSection.planNewCharacterArcSection(characterId, themeId, templateWithMaxOfOne))
		}.run {
			arcId.mustEqual(arc.id.uuid)
			characterId.mustEqual(arc.characterId.uuid)
			themeId.mustEqual(arc.themeId.uuid)
			templateSectionId.mustEqual(templateWithMaxOfOne.id.uuid)
		}
	}

	@Test
	fun `cannot add section with template that is not in arc template`() {
		val templateSection = template("", false, true)
		val template = CharacterArcTemplate(listOf())
		val arc = CharacterArc.planNewCharacterArc(characterId, themeId, name, template)
		assertThrows<TemplateSectionIsNotPartOfArcTemplate> {
			arc.withArcSection(templateSection)
		}.run {
			arcId.mustEqual(arc.id.uuid)
			characterId.mustEqual(arc.characterId.uuid)
			themeId.mustEqual(arc.themeId.uuid)
			templateSectionId.mustEqual(templateSection.id.uuid)
		}
		assertThrows<TemplateSectionIsNotPartOfArcTemplate> {
			arc.withArcSection(CharacterArcSection.planNewCharacterArcSection(characterId, themeId, templateSection))
		}.run {
			arcId.mustEqual(arc.id.uuid)
			characterId.mustEqual(arc.characterId.uuid)
			themeId.mustEqual(arc.themeId.uuid)
			templateSectionId.mustEqual(templateSection.id.uuid)
		}
	}

	@Test
	fun `moral sections have an order`() {
		val templateSection = template("", false, true, moral = true)
		val template = CharacterArcTemplate(listOf(templateSection))
		val arc = CharacterArc.planNewCharacterArc(characterId, themeId, name, template)
		val arcWithArcSection = arc.withArcSection(templateSection)
		val arcSection = arcWithArcSection.arcSections.single()
		arcWithArcSection.moralArgument().arcSections.first().mustEqual(arcSection)
	}

	@Test
	fun `only moral sections have an order`() {
		val templateSection = template("", false, true, moral = false)
		val template = CharacterArcTemplate(listOf(templateSection))
		val arc = CharacterArc.planNewCharacterArc(characterId, themeId, name, template)
		val arcWithArcSection = arc.withArcSection(templateSection)
		val arcSection = arcWithArcSection.arcSections.single()

		assertFalse(arcWithArcSection.moralArgument().arcSections.contains(arcSection)) {
			"Moral argument section order should only contain moral arc sections" }
	}

}