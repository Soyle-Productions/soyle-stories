/**
 * Created by Brendan
 * Date: 3/12/2020
 * Time: 7:22 PM
 */
package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.common.async
import com.soyle.stories.common.wrapEditable
import com.soyle.stories.di.resolve
import javafx.beans.property.Property
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*

class CharacterChangeSubTool : Fragment() {

    override val scope = super.scope as CharacterComparisonScope
    val model = find<CharacterComparisonModel>()
    val characterComparisonViewListener: CharacterComparisonViewListener = resolve()

    private val characterChangeTool: Property<CharacterChangeSubToolViewModel> =
        model.subTools.select { (it.getOrNull(2) as? CharacterChangeSubToolViewModel).toProperty() }

    override val root: Parent = form {
        fieldset(labelPosition = Orientation.VERTICAL) {
            hbox(5) {
                field("Psychological Weakness") {
                    hgrow = Priority.SOMETIMES
                    textValueField(CharacterChangeSubToolViewModel::psychWeakness)
                }
                field("Moral Weakness") {
                    hgrow = Priority.SOMETIMES
                    textValueField(CharacterChangeSubToolViewModel::moralWeakness)
                }
                field("Change") {
                    hgrow = Priority.SOMETIMES
                    tooltip("Functionality not yet completed") {
                        showDelay = (0).seconds
                    }
                    textfield(characterChangeTool.select { (it?.change ?: "").toProperty() }) {
                        fitToParentWidth()
                        isDisable = true
                    }
                }
            }
            field("Desire") {
                textValueField(CharacterChangeSubToolViewModel::desire)
            }
            val itemsProperty = characterChangeTool.select { (it?.items ?: emptyList()).toObservable().toProperty() }
            vbox {
                itemsProperty.onChange {
                    visibleProperty().set(it.isNullOrEmpty())
                }
                managedProperty().bind(visibleProperty())
                label("No opponents to challenge your hero!  To give your hero an opponent (or two), add characters to the character comparison, promote them to a Major Character, then give them the 'Antagonist' or 'Fake-Ally Antagonist' story function.") {
                    fitToParentWidth()
                    isWrapText = true
                }
                button("Go to Comparisons") {
                    action {
                        model.pageSelection.set("Comparisons")
                    }
                }
            }
            field {
                tableview<ComparisonItem> {
                    itemsProperty.onChange {
                        visibleProperty().set(! it.isNullOrEmpty())
                        requestResize()
                    }
                    managedProperty().bind(visibleProperty())
                    itemsProperty().bind(itemsProperty)
                    smartResize()
                    enableCellEditing()
                    characterChangeTool.select { it.sections.toProperty() }.onChange {
                        columns.clear()
                        if (it == null) return@onChange
                        readonlyColumn("", ComparisonItem::characterName) {
                            isReorderable = false
                            isSortable = false
                            contentWidth(useAsMax = true)
                        }
                        it.forEach { columnName ->
                            column<ComparisonItem, String>(columnName) {
                                it.value.compSections.getValue(columnName).value.toProperty()
                            }.apply {
                                wrapEditable()
                                remainingWidth()
                                isReorderable = false
                                isSortable = false

                            }
                        }
                    }
                }
            }
        }
    }

    private fun Field.textValueField(prop: CharacterChangeSubToolViewModel.() -> SectionValue) {
        textfield(characterChangeTool.select { (it?.prop()?.value ?: "").toProperty() }) {
            fitToParentWidth()
            focusedProperty().onChange { focused ->
                val sectionValue = characterChangeTool.value?.prop() ?: return@onChange
                if (! focused && text != sectionValue.value) {
                    async(scope.projectScope) {
                        when (sectionValue) {
                            is PropertyValue -> if (sectionValue.isShared) {
                                // intentionally left blank.  These text fields can only relate to properties on the focus character.
                            } else {
                                characterComparisonViewListener.changeCharacterPropertyValue(model.focusedCharacter.value.characterId, sectionValue.propertyName, text)
                            }
                            is CharacterArcSectionValue -> characterComparisonViewListener.updateValue(
                                sectionValue.sectionId,
                                text
                            )
                        }
                    }
                }
            }
        }
    }
}