package com.soyle.stories.entities

import java.util.*

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 2:39 PM
 */
class CharacterArcSection(
    val id: Id,
    val characterId: Character.Id,
    val themeId: Theme.Id,
    val template: CharacterArcTemplateSection,
    val value: String
) {

    private fun copy(
        value: String = this.value
    ) = CharacterArcSection(id, characterId, themeId, template, value)

    fun changeValue(value: String) = copy(value = value)

    data class Id(val uuid: UUID)

    companion object {
        /*
                fun planSectionsToFillTemplate(existingSections: List<CharacterArcSection>, characterArcTemplate: CharacterArcTemplate): List<CharacterArcSection>
                {
                    val existingIds = existingSections.map { it.template.id }.toSet()
                    return characterArcTemplate.sections.filterNot { it.id in existingIds }.map {
                        planNewCharacterArcSection(it)
                    }
                }
        */
        fun planNewCharacterArcSection(
            characterId: Character.Id,
            themeId: Theme.Id,
            template: CharacterArcTemplateSection
        ) =
            CharacterArcSection(
                Id(
                    UUID.randomUUID()
                ), characterId, themeId, template, ""
            )
    }

}