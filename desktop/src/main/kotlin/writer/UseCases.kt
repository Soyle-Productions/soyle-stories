package com.soyle.stories.desktop.config.writer

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.writer.getDialogPreference.GetDialogPreferenceController
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferencesUseCase

object UseCases {

    init {
        scoped<ProjectScope> {
            getDialogPreference()
        }
    }

    private fun InProjectScope.getDialogPreference() {
        provide<GetDialogPreferences> { GetDialogPreferencesUseCase(applicationScope.writerId, get()) }
        provide<GetDialogPreferenceController> { GetDialogPreferenceController(applicationScope.get(), get()) }
    }

}