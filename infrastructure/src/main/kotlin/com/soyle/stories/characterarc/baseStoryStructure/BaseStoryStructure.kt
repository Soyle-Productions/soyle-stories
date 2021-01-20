package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.common.ToolView
import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.di.resolve
import com.soyle.stories.location.createLocationDialog.createLocationDialog
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.MenuItem
import javafx.scene.layout.Priority
import tornadofx.*

class BaseStoryStructure : ToolView() {

    override val scope: BaseStoryStructureScope = super.scope as BaseStoryStructureScope

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

    private fun linkedLocationOptions(section: StoryStructureSectionViewModel): List<MenuItem>
    {
        return listOf(
            MenuItem("Create Location").apply {
                action {
                    createLocationDialog(scope.projectScope) {
                        baseStoryStructureViewListener.linkLocation(section.sectionId, it.locationId.toString())
                    }
                }
            }
        ) + model.availableLocations.map {
            CheckMenuItem(it.name).apply {
                id = it.id
                isSelected = section.linkedLocation?.id == it.id
                action {
                    if (section.linkedLocation?.id == it.id) {
                        baseStoryStructureViewListener.unlinkLocation(section.sectionId)
                        return@action
                    }
                    val locationId = it?.id ?: return@action
                    baseStoryStructureViewListener.linkLocation(section.sectionId, locationId)
                }
            }
        }
    }

    private fun Fieldset.addStoryStructureItem(index: Int) {
        val section = model.sections.select { it.getOrNull(index).toProperty() } as SimpleObjectProperty
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
                        val sectionValue = section.get()?.sectionValue ?: return@onChange
                        if (!focused && text != sectionValue) {
                            baseStoryStructureViewListener.changeSectionValue(section.value!!.sectionId, text)
                        }
                    }
                }
            }
            field("Linked Location") {
                hgrow = Priority.SOMETIMES
                menubutton {
                    textProperty().bind(section.select { (it.linkedLocation?.name ?: "[link]").toProperty() })
                    addClass("location-select")
                    fitToParentWidth()
                    setOnShowing {
                        val sectionValue = section.get() ?: return@setOnShowing
                        items.setAll(linkedLocationOptions(sectionValue))
                    }
                    setOnHidden { items.clear() }
                }
            }
        }
        section.onChange {
            if (it == null) itemRoot.removeFromParent()
        }
    }

    init {
    	title = "Base Story Structure"
    }
}