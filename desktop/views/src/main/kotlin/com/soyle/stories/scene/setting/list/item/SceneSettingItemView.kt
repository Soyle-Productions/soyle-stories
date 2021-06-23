package com.soyle.stories.scene.setting.list.item

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.components.ComponentsStyles.Companion.hasProblem
import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipColor
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipVariant
import com.soyle.stories.common.guiUpdate
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.events.SceneSettingLocationRenamed
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesReceiver
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedReceiver
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.RemoveLocationFromSceneController
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView.Styles.Companion.sceneSettingItem
import com.soyle.stories.usecase.scene.inconsistencies.SceneInconsistencies
import javafx.application.Platform
import javafx.scene.control.Tooltip
import javafx.util.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.javafx.JavaFxDispatcher
import kotlinx.coroutines.withContext
import tornadofx.*

class SceneSettingItemView(
    private val model: SceneSettingItemModel,
    private val locale: SceneSettingItemLocale,
    private val removeLocationFromSceneController: RemoveLocationFromSceneController,
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
    }

    init {
        onDelete { removeLocationFromSceneController.removeLocation(model.sceneId, model.locationId) }
        tooltipProperty().bind(inconsistency.objectBinding {
            if (it != true) return@objectBinding null
            else removedTooltip()
        })
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
        SceneInconsistenciesReceiver
    {

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

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            sceneSettingItem {
                chipVariant = Chip.Variant.outlined
                chipColor = Chip.Color.secondary

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