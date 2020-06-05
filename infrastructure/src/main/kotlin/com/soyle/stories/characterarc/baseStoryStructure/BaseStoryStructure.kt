package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
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

    private val linkedLocationContextMenuSection: SimpleObjectProperty<StoryStructureSectionViewModel?> = SimpleObjectProperty(null)
    private val linkedLocationContextMenu = ContextMenu().apply {
        isAutoHide = true
        items.bind(model.availableLocations) {
            checkmenuitem(it.name) {
                id = it.id
                linkedLocationContextMenuSection.select { section -> (section?.linkedLocation?.id == it.id).toProperty() }.onChange {
                    isSelected = it == true
                }
                action {
                    val section = linkedLocationContextMenuSection.get()
                    val sectionId = section?.sectionId ?: return@action
                    if (section.linkedLocation?.id == it.id) {
                        baseStoryStructureViewListener.unlinkLocation(sectionId)
                        this@apply.hide()
                        return@action
                    }
                    val locationId = it?.id ?: return@action
                    baseStoryStructureViewListener.linkLocation(sectionId, locationId)
                    this@apply.hide()
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
                button {
                    textProperty().bind(section.select { (it.linkedLocation?.name ?: "[link]").toProperty() })
                    addClass("location-select")
                    enableWhen { model.locationsAvailable }
                    fitToParentWidth()
                    action {
                        contextMenu = linkedLocationContextMenu
                        linkedLocationContextMenuSection.set(section.get())
                        contextMenu.show(this, Side.BOTTOM, 0.0, 0.0)
                    }
                }
            }
        }
        section.onChange {
            if (it == null) itemRoot.removeFromParent()
        }
    }
}

fun TabPane.baseStoryStructureTab(projectScope: ProjectScope, toolId: String, baseStoryStructure: com.soyle.stories.layout.tools.dynamic.BaseStoryStructure): Tab {
    val scope = BaseStoryStructureScope(projectScope, toolId, baseStoryStructure)
    val structure = find<BaseStoryStructure>(scope = scope)
    val tab = tab(structure)
    tab.tabPaneProperty().onChange {
        if (it == null) {
            scope.close()
        }
    }
    return tab
}