package com.soyle.stories.scene.reorderSceneRamifications

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.scene.deleteSceneRamifications.DeleteSceneRamificationsViewModel
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleBooleanProperty

class ReorderSceneRamificationsModel : Model<ReorderSceneRamificationsScope, ReorderSceneRamificationsViewModel>(ReorderSceneRamificationsScope::class) {

    val invalid = bind(ReorderSceneRamificationsViewModel::invalid)
    val scenes = bind(ReorderSceneRamificationsViewModel::scenes)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope
}