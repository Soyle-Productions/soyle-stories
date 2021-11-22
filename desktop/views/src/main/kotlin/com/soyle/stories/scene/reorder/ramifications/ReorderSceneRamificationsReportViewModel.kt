package com.soyle.stories.scene.reorder.ramifications

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamificationsViewModel
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.usecase.scene.common.AffectedScene
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.observableListOf

class ReorderSceneRamificationsReportViewModel {

    val scenes = observableListOf<AffectedScene>()

    private var onReorder: () -> Unit = {}

    fun setOnReorder(handler: () -> Unit) {
        onReorder = handler
    }

    fun reorder() {
        onReorder()
    }

    private var onCancel: () -> Unit = {}

    fun setOnCancel(handler: () -> Unit) {
        onCancel = handler
    }

    fun cancel() {
        onCancel()
    }

}