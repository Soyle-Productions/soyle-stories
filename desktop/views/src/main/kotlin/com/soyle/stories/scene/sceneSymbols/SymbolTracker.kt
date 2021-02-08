package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.ButtonStyles.Companion.inviteButton
import com.soyle.stories.common.components.buttons.ButtonStyles.Companion.primaryButton
import com.soyle.stories.common.components.fieldLabel
import com.soyle.stories.common.components.text.mainHeader
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.proseEditor.ProseEditorView
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView.Styles.Companion.hasSymbols
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView.Styles.Companion.symbolTracker
import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Menu
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import org.fxmisc.richtext.TextExt
import tornadofx.*
import java.util.*

class SymbolTracker : Fragment() {

    private val state = resolve<SymbolsInSceneState>()
    private val viewListener = resolve<SymbolsInSceneViewListener>()

    override val root: Parent = stackpane {
        addClass(symbolTracker)
        dynamicContent(state.themesInScene) {
            determineRootContent(it).apply {
                vgrow = Priority.ALWAYS
            }
            requestLayout()
        }
    }

    private fun Parent.determineRootContent(themes: List<SymbolsInSceneViewModel.ThemeInScene>?): Node {
        return when {
            themes.isNullOrEmpty() -> {
                togglePseudoClass(hasSymbols.name, false)
                emptyState()
            }
            else -> {
                togglePseudoClass(hasSymbols.name, true)
                hasSymbolsState(themes)
            }
        }
    }

    private fun Parent.emptyState(): Node {
        return vbox(spacing = 16) {
            alignment = Pos.CENTER
            addClass(ComponentsStyles.cardBody)
            imageview("com/soyle/stories/scene/Symbols-design.png") {
                this.isPreserveRatio = true
                this.isSmooth = true
                fitHeight = 260.0
            }
            mainHeader("Track Symbols in Scene")
            textflow {
                textAlignment = TextAlignment.CENTER
                text("When you ")
                add(TextExt("@mention").apply {
                    addClass(ProseEditorView.Styles.mention)
                    style { fontWeight = FontWeight.BOLD }
                })
                text(" " + """
                    a symbol in the scene, it will automatically be added here.  However, you can also choose to pin a 
                    symbol to this scene by clicking the button below.  Pinned symbols will not be automatically 
                    removed if they are no longer mentioned within the scene.
                """.trimIndent().filterNot { it == '\n' })
            }
            pinSymbolMenuButton {
                addClass(inviteButton)
                alignment = Pos.CENTER
            }
        }
    }

    private fun Parent.hasSymbolsState(themes: List<SymbolsInSceneViewModel.ThemeInScene>): Node {
        return vbox {
            hbox {
                alignment = Pos.CENTER_LEFT
                mainHeader("Tracked Symbols")
                spacer()
                pinSymbolMenuButton {}
            }
            vbox {
                addClass("symbol-list")
                themes.forEach { themeViewModel ->
                    fieldLabel(themeViewModel.themeName)
                    themeViewModel.symbolsInScene.forEach {
                        trackedSymbolChip(it, onTogglePin = ::toggleSymbolPinned)
                    }
                }
            }
        }
    }

    private fun Parent.pinSymbolMenuButton(op: MenuButton.() -> Unit) = menubutton("Pin Symbol") {
        addClass(primaryButton)
        addClass("pin-symbol-button")
        setOnShowing { loadAvailableSymbolsToTrack() }
        state.availableSymbols.onChange { it: ObservableList<SymbolsInSceneViewModel.AvailableTheme>? ->
            items.setAll(availableSymbolOptions(it))
        }
        op()
    }

    private fun loadAvailableSymbolsToTrack() {
        state.availableSymbols.value = null
        val sceneId = state.targetScene.value?.id
            ?: throw IllegalStateException("This should have been an impossible state to get to.  You tried to select a symbol to pin to a scene, but the code doesn't think there is an active scene.  Please report this and EXACTLY how you got here.")
        viewListener.listAvailableSymbolsToTrack(Scene.Id(UUID.fromString(sceneId)))
    }

    private fun availableSymbolOptions(availableSymbols: List<SymbolsInSceneViewModel.AvailableTheme>?): List<MenuItem> {
        val createNewSymbolAndThemeItem = MenuItem("Create New Symbol and Theme").apply {
            action {
                CreateSymbolDialog(
                    scope as ProjectScope,
                    null,
                    null,
                    currentWindow
                ) {
                    pinSymbol(Symbol.Id(it.symbolId))
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
                createNewSymbolAndThemeItem,
                MenuItem("No Symbols Left to Track").apply { isDisable = true },
            )
            else -> listOf(
                createNewSymbolAndThemeItem
            ) + availableSymbols.map(::availableThemeItem)
        }
    }

    private fun availableThemeItem(availableTheme: SymbolsInSceneViewModel.AvailableTheme): MenuItem {
        return Menu(availableTheme.themeName).apply {
            id = availableTheme.themeId.toString()
            item("Create New Symbol") {
                action {
                    CreateSymbolDialog(
                        scope as ProjectScope,
                        availableTheme.themeId.uuid.toString(),
                        null,
                        currentWindow
                    ) {
                        pinSymbol(Symbol.Id(it.symbolId))
                    }
                }
            }
            if (availableTheme.symbolsInScene.isEmpty()) {
                item("No Symbols Left to Track") { isDisable = true }
            }
            availableTheme.symbolsInScene.forEach {
                item(it.symbolName) {
                    action { pinSymbol(it.symbolId) }
                }
            }
        }
    }

    private fun toggleSymbolPinned(symbolItem: SymbolsInSceneViewModel.SymbolInScene) {
        if (symbolItem.isPinned) {
            unpinSymbol(symbolItem.symbolId)
        } else {
            pinSymbol(symbolItem.symbolId)
        }
    }

    private fun pinSymbol(symbolId: Symbol.Id) {
        val sceneId = state.targetScene.value?.id
            ?: throw IllegalStateException("This should have been an impossible state to get to.  You tried to pin a symbol to a scene, but the code doesn't think there is an active scene.  Please report this and EXACTLY how you got here.")
        viewListener.pinSymbol(Scene.Id(UUID.fromString(sceneId)), symbolId)
    }

    private fun unpinSymbol(symbolId: Symbol.Id) {
        val sceneId = state.targetScene.value?.id
            ?: throw IllegalStateException("This should have been an impossible state to get to.  You tried to pin a symbol to a scene, but the code doesn't think there is an active scene.  Please report this and EXACTLY how you got here.")
        viewListener.unpinSymbol(Scene.Id(UUID.fromString(sceneId)), symbolId)
    }


    init {

    }
}