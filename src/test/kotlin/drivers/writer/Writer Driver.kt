package com.soyle.stories.desktop.config.drivers.writer

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController

class WriterDriver(private val projectScope: ProjectScope)
{

    fun givenDialogShouldNotBeShown(dialog: DialogType) {
        projectScope.get<SetDialogPreferencesController>().setDialogPreferences(dialog.name, false)
    }
    fun givenDialogShouldBeShown(dialog: DialogType) {
        projectScope.get<SetDialogPreferencesController>().setDialogPreferences(dialog.name, true)
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { WriterDriver(this) } }
        }
        operator fun invoke(workBench: WorkBench) = workBench.scope.get<WriterDriver>()
    }

}