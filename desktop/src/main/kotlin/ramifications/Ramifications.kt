package com.soyle.stories.desktop.config.ramifications

import com.soyle.stories.Locale
import com.soyle.stories.character.delete.ramifications.RemoveCharacterRamificationsReportLocale
import com.soyle.stories.desktop.config.locale.LocaleHolder
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.ramifications.RamificationsViewModel

object Ramifications {

    operator fun invoke() {
        scoped<ProjectScope> {
            provide<RamificationsViewModel> { RamificationsViewModel(get(), get()) }
            provide<RemoveCharacterRamificationsReportLocale>() { applicationScope.get<Locale>().characters.remove.ramifications }
        }
    }

}