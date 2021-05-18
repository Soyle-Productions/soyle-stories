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
import com.soyle.stories.common.scopedListener
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import java.lang.ref.WeakReference
import java.util.*

fun characterIcon(sourceProperty: ObservableValue<String?>) = CharacterIcon().apply { this.sourceProperty.bind(sourceProperty) }

private fun defaultIcon() = MaterialIconView(CharacterArcStyles.defaultCharacterImage, "1.5em")

class CharacterIcon : StackPane() {

    val sourceProperty = SimpleStringProperty(null)

    init {
        addClass(CharacterArcStyles.characterIcon)
        scopedListener(sourceProperty) {
            val icon = if (it.isNullOrBlank()) defaultIcon()
            else {
                try {
                    ImageView(Image(it))
                } catch (e: java.lang.Exception) {
                    defaultIcon()
                }
            }

            children.setAll(icon)
        }
    }

}