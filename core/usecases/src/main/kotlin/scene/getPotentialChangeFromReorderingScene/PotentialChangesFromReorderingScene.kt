package com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene

import com.soyle.stories.usecase.scene.common.AffectedScene

class PotentialChangesFromReorderingScene(val affectedScenes: List<AffectedScene>) {

    fun isEmpty(): Boolean = affectedScenes.isEmpty()

}