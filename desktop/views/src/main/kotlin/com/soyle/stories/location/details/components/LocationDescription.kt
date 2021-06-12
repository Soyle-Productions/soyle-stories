package com.soyle.stories.location.details.components

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.common.softBind
import com.soyle.stories.location.details.LocationDetailsActions
import com.soyle.stories.location.details.LocationDetailsLocale
import com.soyle.stories.location.details.LocationDetailsStyles
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Parent
import javafx.scene.control.TextArea
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import tornadofx.*

class LocationDescription(
    private val description: ObservableValue<String>,
    private val actions: LocationDetailsActions,
    private val locale: LocationDetailsLocale
    ) : VBox() {

    companion object {
        @ViewBuilder
        fun EventTarget.locationDescription(
            description: ObservableValue<String>,
            actions: LocationDetailsActions,
            locale: LocationDetailsLocale
        ) = opcr(this, LocationDescription(description, actions, locale))
    }

    // initialize styles
    init {
        addClass(TextStyles.section)
        addClass(LocationDetailsStyles.description)
    }

    // initialize structure
    init {
        sectionTitle(locale.description)
        input()
    }

    @ViewBuilder
    private fun Parent.input(): TextArea {
        return textarea {
            minHeight = Region.USE_PREF_SIZE
            prefRowCountProperty().onChange { parent.requestLayout() }
            textProperty().softBind(description) { it }

            onLoseFocus { reDescribe(text) }
        }
    }

    private fun reDescribe(newDescription: String)
    {
        if (newDescription != description.value) {
            actions.reDescribeLocation(newDescription)
        }
    }

}