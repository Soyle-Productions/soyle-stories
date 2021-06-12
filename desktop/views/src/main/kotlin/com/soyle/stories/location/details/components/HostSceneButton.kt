package com.soyle.stories.location.details.components

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.common.scopedListener
import com.soyle.stories.location.details.LocationDetailsActions
import com.soyle.stories.location.details.LocationDetailsLocale
import com.soyle.stories.location.details.models.AvailableSceneToHostModel
import com.soyle.stories.location.details.LocationDetailsStyles
import javafx.beans.property.ReadOnlyListProperty
import javafx.scene.Parent
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import tornadofx.*

class HostSceneButton(
    private val availableScenesToHost: ReadOnlyListProperty<AvailableSceneToHostModel>,
    private val actions: LocationDetailsActions,
    private val locale: LocationDetailsLocale
) : MenuButton() {

    companion object {
        @ViewBuilder
        fun Parent.hostSceneButton(
            availableScenesToHost: ReadOnlyListProperty<AvailableSceneToHostModel>,
            actions: LocationDetailsActions,
            locale: LocationDetailsLocale
        ) = opcr(this, HostSceneButton(availableScenesToHost, actions, locale))
    }

    init {
        id = LocationDetailsStyles.addScene.name
        addClass(ComponentsStyles.outlined)
        addClass(ComponentsStyles.secondary)
        addClass(ButtonStyles.noArrow)
        toggleClass(ButtonStyles.inviteButton, availableScenesToHost.emptyProperty().not())
    }

    init {
        textProperty().bind(locale.hostScene)
        scopedListener(availableScenesToHost) {
            when {
                it == null -> items.setAll(loadingItem())
                it.isEmpty() -> items.setAll(
                    createSceneItem(),
                    SeparatorMenuItem(),
                    noAvailableScenesItem()
                )
                else -> items.setAll(
                    listOf(createSceneItem(), SeparatorMenuItem()) + it.map(::availableSceneItem)
                )
            }
        }
    }

    init {
        setOnShowing { actions.loadAvailableScenes() }
    }

    private fun loadingItem() = MenuItem().apply {
        id = "loading"

        textProperty().bind(locale.loading)
    }
    private fun createSceneItem() = MenuItem().apply {
        id = "createScene"

        textProperty().bind(locale.createScene)

        action { actions.createSceneToHost() }
    }
    private fun noAvailableScenesItem() = MenuItem().apply {
        isDisable = true
        textProperty().bind(locale.allExistingScenesInProjectHaveBeenHosted)
    }
    private fun availableSceneItem(availableScene: AvailableSceneToHostModel) = MenuItem().apply {
        id = availableScene.sceneId.toString()

        textProperty().bind(availableScene.name)

        action { actions.hostScene(availableScene.sceneId) }
    }

}