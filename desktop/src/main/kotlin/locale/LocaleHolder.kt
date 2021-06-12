package com.soyle.stories.desktop.config.locale

import com.soyle.stories.desktop.locale.SoyleMessageBundle
import com.soyle.stories.di.scoped
import com.soyle.stories.location.details.LocationDetailsLocale
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.value.ObservableValue
import tornadofx.objectProperty
import tornadofx.stringBinding

class LocaleHolder(bundle: SoyleMessageBundle) : LocationDetailsLocale {

    private val currentLocale = objectProperty<SoyleMessageBundle>(bundle)

    fun holdLocale(bundle: SoyleMessageBundle) {
        currentLocale.value = bundle
    }

    override val description: ObservableValue<String> = currentLocale.stringBinding { it!!.description }
    override val scenesHostedInLocation: ObservableValue<String> =
        currentLocale.stringBinding { it!!.scenesHostedInLocation }
    override val hostScene: ObservableValue<String> = currentLocale.stringBinding { it!!.hostScene }
    override val loading: ObservableValue<String> = currentLocale.stringBinding { it!!.loading }
    override val createScene: ObservableValue<String> = currentLocale.stringBinding { it!!.createScene }
    override val hostSceneInLocationInvitationMessage: ObservableValue<String> =
        currentLocale.stringBinding { it!!.hostSceneInLocationInvitationMessage }
    override val allExistingScenesInProjectHaveBeenHosted: ObservableValue<String> =
        currentLocale.stringBinding { it!!.allExistingScenesInProjectHaveBeenHosted }

    override fun locationDetailsToolName(locationName: String): ObservableValue<String> =
        currentLocale.stringBinding { it!!.locationDetailsToolName.format(it.locale, locationName) }


}