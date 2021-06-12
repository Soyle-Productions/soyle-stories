package com.soyle.stories.desktop.config.location

import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.details.LocationDetailsLocale
import com.soyle.stories.soylestories.ApplicationScope

object Locations {

    operator fun invoke() {
        UseCases
        Notifiers
        Presentation
    }

}