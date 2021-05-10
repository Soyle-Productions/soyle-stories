package com.soyle.stories.characterarc.components

import com.soyle.stories.characterarc.CharacterArcStyles
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import tornadofx.*
import kotlin.reflect.KProperty
import com.soyle.stories.common.getValue
import com.soyle.stories.common.onChangeWithCurrent
import javafx.beans.value.ObservableValue
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import java.lang.ref.WeakReference

fun characterIcon(sourceProperty: ObservableValue<String?>): Node {
    val source = sourceProperty.value
    val node = if (source.isNullOrBlank()) defaultIcon()
    else {
        try {
            ImageView(source)
        } catch (e: Exception) {
            defaultIcon()
        }
    }.let { StackPane(it) }
    node.addClass(CharacterArcStyles.characterIcon)
    val nodeRef = WeakReference(node)
    sourceProperty.onChangeOnce {
        val node = nodeRef.get() ?: return@onChangeOnce
        val nodeIndexInParent = node.indexInParent.takeIf { it >= 0 } ?: return@onChangeOnce
        node.parent?.getChildList()?.add(nodeIndexInParent, characterIcon(sourceProperty))
        node.removeFromParent()
    }
    return node
}

private fun defaultIcon() = MaterialIconView(CharacterArcStyles.defaultCharacterImage, "1.5em")