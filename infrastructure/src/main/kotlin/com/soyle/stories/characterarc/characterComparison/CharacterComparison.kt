package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.common.async
import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.LayoutViewListener
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import kotlinx.coroutines.runBlocking
import tornadofx.*

class CharacterComparison : View("Character Comparison") {

    override val scope = super.scope as CharacterComparisonScope
    val model = find<CharacterComparisonModel>()
    //val characterComparisonViewListener: CharacterComparisonViewListener = resolve()
    //val layoutViewListener = resolve<LayoutViewListener>(scope.projectScope)

    init {/*
        model.subTools.onChangeUntil({ !it.isNullOrEmpty() }) {
            if (it == null || it.isEmpty()) return@onChangeUntil
            model.pageSelection.set(it.first().label)
        }*/
    }


    override val root = stackpane {
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
        borderpane {
            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS
            isDisable = true
            addClass(WizardStyles.wizard)
            top {
                hbox {
                    addClass(WizardStyles.header)
                    label {
                        model.pageSelection.onChange {
                            text = if (it == "Character Change") "${model.focusedCharacter.value?.characterName}'s Character Change"
                            else it
                        }
                        model.focusedCharacter.onChange {
                            text = if (model.pageSelection.value == "Character Change") "${it?.characterName}'s Character Change"
                            else model.pageSelection.value
                        }
                    }
                    spacer()
                    menubutton {
                        textProperty().bind(model.focusedCharacter.select { (it?.characterName ?: "<No Focused Character>").toProperty() })
                        items.bind(model.characterOptions) {
                            item(it.characterName) {
                                action {
                                    async(scope.projectScope) {
                                       // characterComparisonViewListener.getCharacterComparison(it.characterId)
                                    }
                                }
                            }
                        }
                    }
                    menubutton("Add Character") {
                        enableWhen { model.availableCharactersToAdd.emptyProperty().not() }
                        model.availableCharactersToAdd.onChange { it: ObservableList<CharacterItemViewModel>? ->
                            items.clear()
                            it?.forEach {
                                item(it.characterName) {
                                    action {
                                        async(scope.projectScope) {
                                           // characterComparisonViewListener.addCharacterToComparison(it.characterId)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            left {
                vbox {
                    addClass(WizardStyles.stepInfo)
                    bindChildren(model.subTools) {
                        hyperlink(it.label) {
                            toggleClass(WizardStyles.bold, model.pageSelection.isEqualTo(it.label))
                            action {
                                model.pageSelection.set(it.label)
                            }
                        }
                    }
                }
            }
            center {
                stackpane {
                    addClass(WizardStyles.content)
                    this += find<ComparisonSubTool>(scope = scope).apply {
                        this.root.visibleWhen { model.pageSelection.isEqualTo("Comparisons") }
                    }
                    this += find<MoralProblemSubTool>(scope = scope).apply {
                        this.root.visibleWhen { model.pageSelection.isEqualTo("Moral Problem") }
                    }
                    this += find<CharacterChangeSubTool>(scope = scope).apply {
                        this.root.visibleWhen { model.pageSelection.isEqualTo("Character Change") }
                    }
                }
            }
        }
        stackpane {
            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS
            style {
                backgroundColor += Color.WHITESMOKE
                opacity = 0.5
            }
            padding = Insets(50.0, 50.0, 50.0, 50.0)
            stackpaneConstraints {
                alignment = Pos.CENTER
            }
            label {
                style {
                    fontSize = 16.pt
                }
                isWrapText = true
                text = """
                Tool is temporarily disabled.  Too many changes were needed to make it work without a selected
                 character arc.  To be re-designed in the next feature planning meeting.
            """.trimIndent().replace("\n", "")
                stackpaneConstraints {
                    alignment = Pos.CENTER
                }
            }
        }
    }



    /*override val root = */

    val optionMenu = ContextMenu().apply {
        isAutoFix = true
        isAutoHide = true
        model.availableCharactersToAdd.onChange { it: ObservableList<CharacterItemViewModel>? ->
            items.clear()
            it?.forEach {
                item(it.characterName) {
                    action {
                        runAsync {
                            runBlocking {
                               // characterComparisonViewListener.addCharacterToComparison(it.characterId)
                            }
                        }
                    }
                }
            }
        }
    }
}