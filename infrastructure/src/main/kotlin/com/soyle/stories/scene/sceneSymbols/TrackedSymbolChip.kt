package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView.Styles.Companion.pinned
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView.Styles.Companion.unused
import javafx.scene.Parent
import javafx.scene.control.Button
import tornadofx.*

fun Parent.trackedSymbolChip(
    symbolItem: SymbolsInSceneViewModel.SymbolInScene,
    onTogglePin: (SymbolsInSceneViewModel.SymbolInScene) -> Unit
) = label(symbolItem.symbolName) {
    id = symbolItem.symbolId.toString()
    addClass(SymbolsInSceneView.Styles.trackedSymbolChip)
    toggleClass(pinned, symbolItem.isPinned)
    toggleClass(unused, symbolItem.isUnused)
    if (symbolItem.isUnused) {
        tooltip("This symbol has not been mentioned in this scene.  Don't forget to mention it in the prose!  If you unpin it, it will be removed.")
    }
    graphic = Button().apply {
        if (symbolItem.isPinned) {
            tooltip("This symbol is currently pinned.  If you unpin it, if there are no mentions of this symbol in the scene's prose, it will no longer be tracked in this scene.")
            graphic = imageview("com/soyle/stories/scene/Pin Icon - Pinned.png")
        } else {
            tooltip("This symbol is not currently pinned.  If you pin it, it will stay tracked in this scene, regardless of if it is mentioned in this scene's prose.")
            graphic = imageview("com/soyle/stories/scene/Pin Icon - Unpinned.png")
        }
        action { onTogglePin(symbolItem) }
    }
}