/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:00 PM
 */
package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.characterarc.characterComparison.CharacterComparison
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonScope
import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.di.characterarc.BaseStoryStructureComponent
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.*

class BaseStoryStructure : View("Base Story Structure") {

    override val scope = super.scope as BaseStoryStructureScope
    val model = find<BaseStoryStructureModel>()
    private val baseStoryStructureViewListener = find<BaseStoryStructureComponent>().baseStoryStructureViewListener

    override val root: Parent = form {
        fieldset(labelPosition = Orientation.VERTICAL) {
            model.sections.indices.forEach {
                addStoryStructureItem(it)
            }
            model.sections.select { SimpleIntegerProperty(it.size) }.addListener { _, i, i2 ->
                val originalSize = i?.toInt() ?: 0
                val newSize = i2?.toInt() ?: 0
                if (newSize > originalSize) {
                    (originalSize until newSize).forEach { addStoryStructureItem(it) }
                }
            }
        }
    }

    private fun Fieldset.addStoryStructureItem(index: Int) {
        val section = model.sections.select { it.getOrNull(index).toProperty() }
        val field = field {
            textProperty.bind(section.stringBinding { it?.sectionTemplateName ?: "" })
            textfield {
                section.stringBinding { it?.sectionValue ?: "" }.onChangeWithCurrent {
                    text = it ?: ""
                }
                focusedProperty().onChange { focused ->
                    val sectionValue = section.value?.sectionValue ?: return@onChange
                    if (!focused && text != sectionValue) {
                        baseStoryStructureViewListener.changeSectionValue(section.value!!.sectionId, text)
                    }
                }
            }
        }
        section.onChange {
            if (it == null) field.removeFromParent()
        }
    }
}

fun TabPane.baseStoryStructureTab(projectScope: ProjectScope, characterId: String, themeId: String): Tab {
    val scope = BaseStoryStructureScope(projectScope, characterId, themeId)
    val structure = find<BaseStoryStructure>(scope = scope)
    val tab = tab(structure)
    tab.tabPaneProperty().onChange {
        if (it == null) {
            scope.close()
        }
    }
    return tab
}