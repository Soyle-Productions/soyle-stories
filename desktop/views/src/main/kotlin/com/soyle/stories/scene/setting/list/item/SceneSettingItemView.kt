package com.soyle.stories.scene.setting.list.item

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.components.ComponentsStyles.Companion.hasProblem
import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipColor
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipVariant
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.events.SceneSettingLocationRenamed
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedReceiver
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.RemoveLocationFromSceneController
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView.Styles.Companion.sceneSettingItem
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
    sceneSettingRenamed: Notifier<SceneSettingLocationRenamedReceiver>
) : Chip(), SceneSettingLocationRenamedReceiver
{

    fun interface Factory {
        operator fun invoke(model: SceneSettingItemModel): SceneSettingItemView
    }

    private val name = stringProperty(model.locationName)

    init {
        id = model.locationId.toString()
        addClass(sceneSettingItem)
        toggleClass(hasProblem, model.removed)
    }

    init {
        textProperty().bind(name)
    }

    init {
        onDelete { removeLocationFromSceneController.removeLocation(model.sceneId, model.locationId) }
        tooltipProperty().bind(model.removed.objectBinding {
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

    override suspend fun receiveSceneSettingLocaitonsRenamed(events: List<SceneSettingLocationRenamed>) {
        val lastRelevantEvent = events.lastOrNull { it.sceneId == model.sceneId && it.sceneSettingLocation.id == model.locationId }
            ?: return
        if (!Platform.isFxApplicationThread()) {
            withContext(Dispatchers.JavaFx) {
                name.set(lastRelevantEvent.sceneSettingLocation.locationName)
            }
        } else {
            name.set(lastRelevantEvent.sceneSettingLocation.locationName)
        }
    }

    init {
        sceneSettingRenamed.addListener(this)
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