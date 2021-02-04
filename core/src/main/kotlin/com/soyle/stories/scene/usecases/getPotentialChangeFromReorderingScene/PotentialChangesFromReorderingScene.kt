package com.soyle.stories.scene.usecases.getPotentialChangeFromReorderingScene

import com.soyle.stories.scene.usecases.common.AffectedScene

class PotentialChangesFromReorderingScene(val affectedScenes: List<AffectedScene>) {

    fun isEmpty(): Boolean = affectedScenes.isEmpty()

}