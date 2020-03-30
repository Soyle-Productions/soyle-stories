/**
 * Created by Brendan
 * Date: 3/12/2020
 * Time: 3:37 PM
 */
package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.common.launchTask
import com.soyle.stories.common.wrapEditable
import com.soyle.stories.di.characterarc.CharacterComparisonComponent
import javafx.beans.property.Property
import javafx.geometry.Orientation
import javafx.scene.Parent
import tornadofx.*

class MoralProblemSubTool : Fragment() {

    override val scope = super.scope as CharacterComparisonScope
    val model = find<CharacterComparisonModel>()
    val characterComparisonViewListener: CharacterComparisonViewListener =
        find<CharacterComparisonComponent>().characterComparisonViewListener

    private val moralProblemSubTool: Property<MoralProblemSubToolViewModel> =
        model.subTools.select { (it?.getOrNull(1) as? MoralProblemSubToolViewModel).toProperty() }

    private val sections = moralProblemSubTool.select { it?.sections?.toObservable().toProperty() }
    private val items = moralProblemSubTool.select { it?.items?.toObservable().toProperty() }

    override val root: Parent = form {
        fieldset(labelPosition = Orientation.VERTICAL) {
            field("Central Moral Question") {
                textfield {
                    text = moralProblemSubTool.value?.centralMoralQuestion ?: ""
                    moralProblemSubTool.stringBinding { it?.centralMoralQuestion }.onChange {
                        text = it ?: ""
                    }
                    focusedProperty().onChange { focused ->
                        if (! focused) {
                            val currentQuestion = moralProblemSubTool.value?.centralMoralQuestion ?: return@onChange
                            val text = text
                            if (text != currentQuestion) {
                                launchTask {
                                    characterComparisonViewListener.updateCentralMoralQuestion(scope.themeId, text)
                                }
                            }
                        }
                    }
                }
            }
            field {
                tableview<ComparisonItem> {
                    columnResizePolicy = SmartResize.POLICY
                    itemsProperty().bind(this@MoralProblemSubTool.items)
                    enableCellEditing()
                    sections.onChange {
                        columns.clear()
                        readonlyColumn("", ComparisonItem::characterName) {
                            isSortable = false
                            isReorderable = false
                        }
                        it?.forEach { columnName ->
                            column<ComparisonItem, String>(columnName) {
                                it.value.compSections.getValue(columnName).value.toProperty()
                            }.apply {
                                wrapEditable()
                                isSortable = false
                                isReorderable = false
                                setOnEditCancel {

                                }
                                onEditCommit {
                                    println("Edit commit: $newValue, $oldValue")
                                    if (newValue != oldValue) {
                                        val sectionValue = it.compSections.getValue(columnName)
                                        launchTask {
                                            when (sectionValue) {
                                                is CharacterArcSectionValue -> characterComparisonViewListener.updateValue(sectionValue.sectionId, newValue as String)
                                                is PropertyValue -> when {
                                                    sectionValue.isShared -> characterComparisonViewListener.changeSharedPropertyValue(scope.themeId, model.focusedCharacter.value.characterId, rowValue.characterId, sectionValue.propertyName, newValue as String)
                                                    else -> characterComparisonViewListener.changeCharacterPropertyValue(scope.themeId, rowValue.characterId, sectionValue.propertyName, newValue as String)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}