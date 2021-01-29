package com.soyle.stories.scene.sceneSymbols

import javafx.beans.value.ObservableValue
import javafx.scene.Parent
import tornadofx.addClass
import tornadofx.label

fun Parent.trackedSymbolChip(
    symbolName: ObservableValue<String>
) = label(symbolName) {
    addClass(SymbolsInSceneView.Styles.trackedSymbolChip)
}