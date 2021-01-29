package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.Styles.Companion.fieldLabel
import com.soyle.stories.common.components.fieldLabel
import com.soyle.stories.di.resolve
import com.soyle.stories.prose.proseEditor.ProseEditorView
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView.Styles.Companion.hasSymbols
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView.Styles.Companion.symbolTracker
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import org.fxmisc.richtext.TextExt
import tornadofx.*

class SymbolTracker : Fragment() {

    private val state = resolve<SymbolsInSceneState>()

    override val root: Parent = stackpane {
        addClass(symbolTracker)
        dynamicContent(state.themesInScene) {
            determineRootContent(it).apply {
                vgrow = Priority.ALWAYS
            }
            requestLayout()
        }
    }

    private fun Parent.determineRootContent(themes: List<SymbolsInSceneViewModel.ThemeInScene>?): Node
    {
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

    private fun Parent.emptyState(): Node
    {
        return vbox(spacing = 16) {
            alignment = Pos.CENTER
            addClass(ComponentsStyles.cardBody)
            imageview("com/soyle/stories/scene/Symbols-design.png") {
                this.isPreserveRatio = true
                this.isSmooth = true
                fitHeight = 260.0
            }
            label("Track Symbols in Scene") { addClass(fieldLabel) }
            textflow {
                textAlignment = TextAlignment.CENTER
                text("When you ")
                add(TextExt("@mention").apply {
                    addClass(ProseEditorView.Styles.mention)
                    style { fontWeight = FontWeight.BOLD }
                })
                text(" a symbol in the scene, it will automatically be added here.")
            }
        }
    }

    private fun Parent.hasSymbolsState(themes: List<SymbolsInSceneViewModel.ThemeInScene>): Node
    {
        return vbox {

            hbox {
                label("Tracked Symbols") { addClass(fieldLabel) }
            }
            vbox {
                addClass("symbol-list")
                themes.forEach { themeViewModel ->
                    fieldLabel(themeViewModel.themeName)
                    themeViewModel.symbolsInScene.forEach {
                        trackedSymbolChip(it.symbolName.toProperty())
                    }
                }
            }
        }
    }

    init {

    }
}