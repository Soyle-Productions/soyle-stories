package com.soyle.stories.entities

import java.util.*

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 2:41 PM
 */
class CharacterArcTemplateSection(
    val id: Id,
    val name: String
) {

    val isRequired: Boolean by lazy {
        CharacterArcTemplate.default().sections.find { it.id == id } != null
    }

    data class Id(val uuid: UUID)
}