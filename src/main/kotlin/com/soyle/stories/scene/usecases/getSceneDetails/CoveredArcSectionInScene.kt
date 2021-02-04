package com.soyle.stories.scene.usecases.getSceneDetails

import java.util.*

class CoveredArcSectionInScene(
    val arcSectionId: UUID,
    val arcSectionTemplateName: String,
    val arcSectionValue: String,
    val arcSectionTemplateAllowsMultiple: Boolean,
    val characterArcId: UUID,
    val characterArcName: String
)