package com.soyle.stories.location.details.components

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.surfaces.elevated
import com.soyle.stories.location.details.components.LocationDescription.Companion.locationDescription
import com.soyle.stories.location.details.LocationDetailsActions
import com.soyle.stories.location.details.LocationDetailsLocale
import com.soyle.stories.location.details.components.ScenesHostedInLocation.Companion.scenesHostedInLocation
import com.soyle.stories.location.details.models.LocationDetailsModel
import com.soyle.stories.location.details.LocationDetailsStyles
import javafx.beans.value.ObservableValue
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*

class LocationDetailsRoot(
    private val state: ObservableValue<LocationDetailsModel>,
    private val actions: LocationDetailsActions,
    private val locale: LocationDetailsLocale
) : ScrollPane() {

    private val hasSmallWidth = booleanBinding(widthProperty()) { value <= 480 }

    init {
        addClass(LocationDetailsStyles.locationDetails)
    }

    init {
        val content = VBox().apply {
            addClass(Stylesheet.content)
            toggleClass(LocationDetailsStyles.smallWidth, hasSmallWidth)
        }
        contentProperty().set(content)
        content.dynamicContent(state) {
            content.determineContent(it)
        }
    }

    @ViewBuilder
    private fun Parent.determineContent(state: LocationDetailsModel?) =
        when (state) {
            LocationDetailsModel.Loading, null -> loadingContent()
            is LocationDetailsModel.Loaded -> loadedContent(state)
        }

    @ViewBuilder
    private fun Parent.loadingContent() {
        toggleClass(ComponentsStyles.loading, true)
        progressindicator()
    }

    @ViewBuilder
    private fun Parent.loadedContent(state: LocationDetailsModel.Loaded) {
        toggleClass(ComponentsStyles.loaded, true)
        locationDescription(state.description, actions, locale)
        scenesHostedInLocation(state, actions, locale).apply {
            vgrow = Priority.ALWAYS
            elevated().inheritedElevationProperty().bind(content.elevated().absoluteElevationProperty())
        }
    }

    override fun getUserAgentStylesheet(): String = LocationDetailsStyles().base64URL.toExternalForm()
}