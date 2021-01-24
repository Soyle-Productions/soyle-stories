package com.soyle.stories.desktop.config.location

import com.soyle.stories.di.scoped
import com.soyle.stories.location.deleteLocation.DeletedLocationNotifier
import com.soyle.stories.location.deleteLocation.DeletedLocationReceiver
import com.soyle.stories.project.ProjectScope

object Notifiers {

    init {
        scoped<ProjectScope> {
            provide(DeletedLocationReceiver::class) {
                DeletedLocationNotifier()
            }
        }
    }

}