package com.soyle.stories.scene.setting.list.item

import com.soyle.stories.common.*
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.ComponentsStyles.Companion.filled
import com.soyle.stories.common.components.ComponentsStyles.Companion.hasProblem
import com.soyle.stories.common.components.ComponentsStyles.Companion.secondary
import com.soyle.stories.common.components.buttons.ButtonStyles.Companion.noArrow
import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipColor
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipDeleteIcon
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipVariant
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneSettingLocationRenamed
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesReceiver
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedReceiver
import com.soyle.stories.scene.locationsInScene.listLocationsToUse.ListLocationsToUseInSceneController
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.RemoveLocationFromSceneController
import com.soyle.stories.scene.locationsInScene.replace.ReplaceSettingInSceneController
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView.Styles.Companion.sceneSettingItem
import com.soyle.stories.usecase.location.listAllLocations.LocationItem
import com.soyle.stories.usecase.scene.inconsistencies.SceneInconsistencies
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInScene
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Menu
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.Tooltip
import javafx.scene.effect.BlendMode
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.SVGPath
import javafx.scene.shape.Shape
import javafx.util.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.javafx.JavaFxDispatcher
import kotlinx.coroutines.withContext
import tornadofx.*
import javax.swing.plaf.TreeUI

class SceneSettingItemView(
    private val model: SceneSettingItemModel,
    private val locale: SceneSettingItemLocale,
    private val removeLocationFromSceneController: RemoveLocationFromSceneController,
    private val listAvailableLocationsToUseInScene: ListLocationsToUseInSceneController,
    private val replaceSettingInScene: ReplaceSettingInSceneController,
    sceneSettingRenamed: Notifier<SceneSettingLocationRenamedReceiver>,
    sceneSettingInconsistency: Notifier<SceneInconsistenciesReceiver>
) : Chip() {

    fun interface Factory {

        operator fun invoke(model: SceneSettingItemModel): SceneSettingItemView
    }

    private val name = stringProperty(model.locationName)
    private val inconsistency = booleanProperty(false)

    init {
        id = model.locationId.toString()
        addClass(sceneSettingItem)
        toggleClass(hasProblem, inconsistency)
    }

    init {
        textProperty().bind(name)
        deleteGraphic = optionsMenu()
    }

    init {
        // keeps the option menu button visible
        onDelete { }
        tooltipProperty().bind(inconsistency.objectBinding {
            if (it != true) return@objectBinding null
            else removedTooltip()
        })
    }

    private fun optionsMenu(): Node = MenuButton("", MaterialIconView(MaterialIcon.MORE_VERT)).apply {
        addClass(Styles.options)
        addClass(noArrow)
        addClass(filled)
        addClass(secondary)

        removeItem()
        val replaceMenu = menu("") {
            id = "replace"
            textProperty().bind(locale.replaceWith)
        }

        setOnShowing {
            replaceMenu.loadReplacements()
        }
    }

    @ViewBuilder
    private fun MenuButton.removeItem() = item("") {
        id = "remove"
        textProperty().bind(locale.removeFromScene)
        action {
            removeLocationFromSceneController.removeLocation(model.sceneId, model.locationId)
        }
    }

    private fun Menu.loadReplacements() {
        items.setAll(loadingItem())
        listAvailableLocationsToUseInScene.listLocationsToUse(model.sceneId) {
            if (it.isEmpty()) {
                items.single().id = "empty-message"
                items.single().textProperty().bind(locale.allExistingLocationsInProjectHaveBeenUsed)
            } else {
                items.setAll(it.map(::replacementLocationItem))
            }
        }
    }

    private fun loadingItem() = MenuItem().apply {
        id = "loading"
        textProperty().bind(locale.loading)
        isDisable = true
    }

    private fun replacementLocationItem(locationItem: LocationItem) = MenuItem().apply {
        id = locationItem.id.toString()
        text = locationItem.locationName
        addClass(Styles.availableLocation)
        action {
            replaceSettingInScene.replaceSettingInScene(model.sceneId, model.locationId, locationItem.id)
        }
    }

    private fun removedTooltip(): Tooltip {
        val tooltip = Tooltip().apply {
            textProperty().bind(locale.locationHasBeenRemovedFromStory)
            showDelay = Duration.ZERO
        }
        return tooltip
    }

    private val domainEventReceiver = object :
        SceneSettingLocationRenamedReceiver,
        SceneInconsistenciesReceiver {

        override suspend fun receiveSceneSettingLocaitonsRenamed(events: List<SceneSettingLocationRenamed>) {
            val lastRelevantEvent = events.lastOrNull {
                it.sceneId == model.sceneId && it.sceneSettingLocation.id == model.locationId
            } ?: return
            guiUpdate {
                name.set(lastRelevantEvent.sceneSettingLocation.locationName)
            }
        }

        override suspend fun receiveSceneInconsistencies(sceneInconsistencies: SceneInconsistencies) {
            if (sceneInconsistencies.sceneId != model.sceneId) return
            val sceneSettingInconsistencies = sceneInconsistencies
                .filterIsInstance<SceneInconsistencies.SceneInconsistency.SceneSettingInconsistency>()
                .firstOrNull() ?: return
            val sceneSettingLocationInconsistencies = sceneSettingInconsistencies
                .find { it.locationId == model.locationId } ?: return
            guiUpdate {
                inconsistency.set(sceneSettingLocationInconsistencies.isNotEmpty())
            }
        }

    }

    init {
        sceneSettingRenamed.addListener(domainEventReceiver)
        sceneSettingInconsistency.addListener(domainEventReceiver)
    }

    override fun getUserAgentStylesheet(): String = Styles().externalForm

    class Styles : Stylesheet() {

        companion object {

            val sceneSettingItem by cssclass()
            val options by cssclass()
            val availableLocation by cssclass()
            val glyphIcon by cssclass("glyph-icon")

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            sceneSettingItem {
                chipVariant = Chip.Variant.outlined
                chipColor = Chip.Color.secondary
                padding = box(4.px)

                label {
                    padding = box(0.px, 6.px, 0.px, 12.px)
                }

                chipDeleteIcon {
                    padding = box(0.px, 2.px, 0.px, 0.px)
                    options {
                        backgroundRadius = multi(box(100.percent))
                        padding = box(0.px, 6.px)
                        label {
                            padding = box(0.px)
                        }
                        glyphIcon {
                            fill = javafx.scene.paint.Color.WHITE
                        }
                        contextMenu {
                            menuItem {
                                label {
                                    textFill = ColorStyles.lightTextColor
                                }
                            }
                        }
                    }
                }

                and(hasProblem) {
                    borderColor = multi(box(ColorStyles.Orange))
                    borderWidth = multi(box(2.px))

                    label {
                        textFill = ColorStyles.Orange
                    }
                }
            }
        }

    }

}