package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import java.util.*

class RequiredCharacterArcSectionTypes(
    list: List<RequiredCharacterArcSectionType>
) : List<RequiredCharacterArcSectionType> by list

class RequiredCharacterArcSectionType(
    val templateSectionId: UUID,
    val name: String
)