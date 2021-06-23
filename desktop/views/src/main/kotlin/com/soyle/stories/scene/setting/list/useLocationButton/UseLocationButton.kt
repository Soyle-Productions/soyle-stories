package com.soyle.stories.scene.setting.list.useLocationButton

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.common.scopedListener
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.locationsInScene.listLocationsToUse.ListLocationsToUseInSceneController
import com.soyle.stories.scene.setting.list.SceneSettingItemList
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButton.Styles.Companion.createLocation
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButton.Styles.Companion.loading
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButton.Styles.Companion.noAvailableLocations
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInScene
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInScene
import javafx.beans.binding.ListExpression
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.stage.Stage
import tornadofx.*

class UseLocationButton(
    private val sceneId: Scene.Id,

    private val locale: UseLocationButtonLocale,
    private val makeCreateLocationDialog: CreateLocationDialog.Factory,
    private val listLocationsToUseInScene: ListLocationsToUseInSceneController,
    private val useLocationController: LinkLocationToSceneController,
) : MenuButton() {

    fun interface Factory {
        operator fun invoke(
            sceneId: Scene.Id
        ): UseLocationButton
    }

    private val availableLocations = listProperty<AvailableSceneSettingModel>(null)

    init {
        id = Styles.useLocation.name
    }

    init {
        addClass(ComponentsStyles.primary)
        addClass(ComponentsStyles.filled)
        addClass(ButtonStyles.noArrow)
    }

    init {
        textProperty().bind(locale.useLocation)
        scopedListener(availableLocations) { populateItems(availableLocations.value) }
    }

    init {
        setOnShowing {
            availableLocations.set(null)
            listLocationsToUseInScene.listLocationsToUse(sceneId) { response ->
                availableLocations.set(response.map {
                    AvailableSceneSettingModel(it.id, stringProperty(it.locationName))
                }.toObservable())
            }
        }
    }

    private fun populateItems(availableLocations: ObservableList<AvailableSceneSettingModel>?) {
        when {
            availableLocations == null -> items.setAll(specialItem(loading, locale.loading, selectable = false))
            else -> populateNotNullItems(availableLocations)
        }
    }

    private fun populateNotNullItems(availableLocations: ObservableList<AvailableSceneSettingModel>) {
        items.setAll(
            listOf(
                createLocationItem(),
                SeparatorMenuItem()
            ) + when {
                availableLocations.isEmpty() -> listOf(noAvailableLocationsItem())
                else -> availableLocations.map(::availableLocationItem)
            }
        )
    }

    private fun createLocationItem() = specialItem(createLocation, locale.createLocation, selectable = true).apply {
        action { createLocation() }
    }

    private fun noAvailableLocationsItem() = specialItem(
        noAvailableLocations,
        locale.allExistingLocationsInProjectHaveBeenUsed,
        selectable = false
    )

    private fun specialItem(id: CssRule, locale: ObservableValue<String>, selectable: Boolean = true): MenuItem =
        MenuItem().apply {
            this.id = id.name
            isDisable = !selectable
            textProperty().bind(locale)
        }

    private fun availableLocationItem(availableLocation: AvailableSceneSettingModel): MenuItem {
        return MenuItem().apply {
            id = availableLocation.locationId.toString()
            addClass(Styles.availableLocation)
            textProperty().bind(availableLocation.locationName)
            action { useLocation(availableLocation.locationId) }
        }
    }

    override fun getUserAgentStylesheet(): String = Styles().externalForm

    private fun createLocation() {
        val dialog = makeCreateLocationDialog {
            useLocationController.linkLocationToScene(sceneId, Location.Id(it.locationId))
        }
        dialog.show(scene?.window)
    }

    private fun useLocation(locationId: Location.Id) {
        useLocationController.linkLocationToScene(sceneId, locationId)
    }

    class Styles : Stylesheet() {

        companion object {

            val useLocation by cssid()

            val loading by cssid()
            val createLocation by cssid()
            val noAvailableLocations by cssid()

            val availableLocation by cssclass()

            init {
                importStylesheet<Styles>()
            }

        }

        init {
        }

    }

}