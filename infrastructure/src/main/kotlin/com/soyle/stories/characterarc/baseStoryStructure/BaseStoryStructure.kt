/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:00 PM
 */
package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.di.resolve
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.BaseStoryStructureToolViewModel
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import tornadofx.*

class BaseStoryStructure : View("Base Story Structure") {

    override val scope = super.scope as BaseStoryStructureScope
    val model = find<BaseStoryStructureModel>()
    private val baseStoryStructureViewListener = resolve<BaseStoryStructureViewListener>()

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

    init {
        baseStoryStructureViewListener.getBaseStoryStructure()
    }

    private fun Fieldset.addStoryStructureItem(index: Int) {
        val section = model.sections.select { it.getOrNull(index).toProperty() }
        val itemRoot = hbox(spacing = 5.0) {
            field {
                hgrow = Priority.ALWAYS
                textProperty.bind(section.stringBinding { it?.sectionTemplateName ?: "" })
                textfield {
                    hgrow = Priority.ALWAYS
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
            field("Linked Location") {
                hgrow = Priority.SOMETIMES
                combobox<LocationItemViewModel> {
                    itemsProperty().bind(model.availableLocations)
                    cellFormat {
                        text = it.name
                    }
                    addClass("location-select")
                    enableWhen { model.locationsAvailable }
                    fitToParentWidth()
                }
            }
        }
        section.onChange {
            if (it == null) itemRoot.removeFromParent()
        }
    }
}

fun TabPane.baseStoryStructureTab(projectScope: ProjectScope, baseStoryStructureTool: BaseStoryStructureToolViewModel): Tab {
    val scope = BaseStoryStructureScope(projectScope, baseStoryStructureTool)
    val structure = find<BaseStoryStructure>(scope = scope)
    val tab = tab(structure)
    tab.tabPaneProperty().onChange {
        if (it == null) {
            scope.close()
        }
    }
    return tab
}