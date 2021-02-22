package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.common.components.fieldLabel
import com.soyle.stories.di.resolve
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneView
import javafx.scene.Parent
import javafx.scene.control.MenuItem
import tornadofx.*

class SceneDetails : View("Scene") {

    override val scope: SceneDetailsScope = super.scope as SceneDetailsScope
    private val viewListener = resolve<SceneDetailsViewListener>()
    private val model = resolve<SceneDetailsModel>()

    override val root: Parent = form {
        settingSection()
        IncludedCharactersInSceneView(this, scope)
    }

    private fun Parent.settingSection() {
        vbox {
            addClass(com.soyle.stories.soylestories.Styles.section)
            fieldLabel(model.locationSectionLabel)
            menubutton {
                addClass("location-select")
                enableWhen { locationsAreAvailableOrALocationIsSelected() }
                textProperty().bind(selectedLocationNameOrEmpty())
                items.bind(model.availableLocations) {
                    MenuItem(it.name).apply {
                        action {
                            viewListener.linkLocation(it.id)
                        }
                    }
                }
            }
        }
    }

    private fun locationsAreAvailableOrALocationIsSelected() =
        booleanBinding(
            model.availableLocations,
            model.selectedLocation
        ) { isNotEmpty() || model.selectedLocation.value != null }

    private fun selectedLocationNameOrEmpty() =
        stringBinding(
            model.selectedLocation,
            model.locationDropDownEmptyLabel
        ) {
            value?.name ?: model.locationDropDownEmptyLabel.value ?: ""
        }

    init {
        model.invalid.onChange {
            if (it) viewListener.getValidState()
        }
        if (model.invalid.value != false) {
            viewListener.getValidState()
        }
        SceneDetailsStyles
    }

}

