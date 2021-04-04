package com.soyle.stories.scene.sceneSetting

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.common.components.buttons.primaryMenuButton
import com.soyle.stories.common.components.layouts.emptyToolInvitation
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.createLocationDialog.createLocationDialog
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.proseEditor.ProseEditorView
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import org.fxmisc.richtext.TextExt
import tornadofx.*
import java.util.*

class LocationSetter : Fragment()  {

    private val state = resolve<SceneSettingState>()
    private val viewListener = resolve<SceneSettingViewListener>()

    override val root: Parent = stackpane {
        addClass(SceneSettingView.Styles.locationSetter)
        dynamicContent(state.usedLocations) {
            determineRootContent(it).apply {
                vgrow = Priority.ALWAYS
            }
            requestLayout()
        }
    }

    private fun Parent.determineRootContent(themes: List<LocationItemViewModel>?): Node {
        return when {
            themes.isNullOrEmpty() -> {
                togglePseudoClass(SceneSettingView.Styles.hasLocations.name, false)
                emptyState()
            }
            else -> {
                togglePseudoClass(SceneSettingView.Styles.hasLocations.name, true)
                hasSymbolsState(themes)
            }
        }
    }

    private fun Parent.emptyState(): Node {
        return emptyToolInvitation {
            imageview("com/soyle/stories/scene/Symbols-design.png") {
                this.isPreserveRatio = true
                this.isSmooth = true
                fitHeight = 260.0
            }
            toolTitle("Use Locations as Scene Setting")
            textflow {
                textAlignment = TextAlignment.CENTER
                text("When you ")
                add(TextExt("@mention").apply {
                    addClass(ProseEditorView.Styles.mention)
                    style { fontWeight = FontWeight.BOLD }
                })
                text(" " + """
                    a location in the scene, you can choose to use the location as a setting in the scene.  However,
                    you can also choose to use a location as a setting in this scene by clicking the button below.
                """.trimIndent().filterNot { it == '\n' })
            }
            useLocationMenuButton {
                addClass(ButtonStyles.inviteButton)
                alignment = Pos.CENTER
            }
        }
    }

    private fun Parent.hasSymbolsState(locations: List<LocationItemViewModel>): Node {
        return vbox {
            hbox {
                alignment = Pos.CENTER_LEFT
                toolTitle("Scene Setting")
                spacer()
                useLocationMenuButton {}
            }
            vbox {
                isFillWidth = false
                addClass(SceneSettingView.Styles.locationList)
                locations.forEach {
                    SceneSettingChip(it, onRemoveLocation = ::removeSceneLocation)
                }
            }
        }
    }

    private fun Parent.useLocationMenuButton(op: MenuButton.() -> Unit) = primaryMenuButton("Use Location") {
        addClass("use-location-button")
        setOnShowing { loadAvailableLocationsToUse() }
        state.availableLocations.onChange { it: ObservableList<LocationItemViewModel>? ->
            items.setAll(availableLocationOptions(it))
        }
        items.setAll(availableLocationOptions(state.availableLocations.value))
        op()
    }

    private fun loadAvailableLocationsToUse() {
        state.availableLocations.value = null
        val sceneId = state.targetScene.value?.id
            ?: throw IllegalStateException("This should have been an impossible state to get to.  You tried to select a symbol to pin to a scene, but the code doesn't think there is an active scene.  Please report this and EXACTLY how you got here.")
        viewListener.listAvailableLocationsToUse(Scene.Id(UUID.fromString(sceneId)))
    }

    private fun availableLocationOptions(availableSymbols: List<LocationItemViewModel>?): List<MenuItem> {
        val createNewLocationItem = MenuItem("Create New Location").apply {
            action {
                createLocationDialog(scope as ProjectScope) {
                    useLocation(Location.Id(it.locationId))
                }
            }
        }
        return when {
            availableSymbols == null -> listOf(MenuItem("Loading ...").apply {
                parentPopupProperty().onChange {
                    it?.style { baseColor = Color.WHITE }
                }
            })
            availableSymbols.isEmpty() -> listOf(
                createNewLocationItem,
                MenuItem("All existing locations have been used").apply { isDisable = true },
            )
            else -> listOf(
                createNewLocationItem
            ) + availableSymbols.map {
                MenuItem(it.name).apply {
                    id = it.id.toString()
                    action { useLocation(it.id) }
                }
            }
        }
    }

    private fun useLocation(locationId: Location.Id) {
        val sceneId = state.targetScene.value?.id
            ?: throw IllegalStateException("This should have been an impossible state to get to.  You tried to use a location as a scene setting, but the code doesn't think there is an active scene.  Please report this and EXACTLY how you got here.")
        viewListener.useLocation(Scene.Id(UUID.fromString(sceneId)), locationId)
    }

    private fun removeSceneLocation(locationItem: LocationItemViewModel)
    {
        val sceneId = state.targetScene.value?.id
            ?: throw IllegalStateException("This should have been an impossible state to get to.  You tried to remove a scene setting, but the code doesn't think there is an active scene.  Please report this and EXACTLY how you got here.")
        viewListener.removeLocation(Scene.Id(UUID.fromString(sceneId)), locationItem.id)
    }
}