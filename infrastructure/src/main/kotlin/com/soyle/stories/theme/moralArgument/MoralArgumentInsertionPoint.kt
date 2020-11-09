package com.soyle.stories.theme.moralArgument

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.asyncMenuButton.AsyncMenuButton.Companion.asyncMenuButton
import com.soyle.stories.di.resolveLater
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import tornadofx.*

class MoralArgumentInsertionPoint : Fragment() {

    companion object {
        fun Parent.insertionPoint(
            scope: Scope,
            addToTopOfSection: MoralArgumentSectionViewModel?,
            tryingToInsertProperty: BooleanBinding,
            op: Node.() -> Unit = {}
        ): Region {
            val params = mapOf(
                MoralArgumentInsertionPoint::addToTopOfSection to addToTopOfSection,
                MoralArgumentInsertionPoint::tryingToInsertProperty to tryingToInsertProperty
            )
            val addSectionButton = find<MoralArgumentInsertionPoint>(scope, params = params)
            this += addSectionButton
            addSectionButton.root.op()
            return addSectionButton.root
        }
    }

    private val addToTopOfSection: MoralArgumentSectionViewModel? by param()
    private val tryingToInsertProperty: BooleanBinding by param()
    private val notTryingToInsertProperty = tryingToInsertProperty.not()
    private val viewListener: MoralArgumentViewListener by resolveLater()
    private val state: MoralArgumentState by resolveLater()

    override val root: Region = asyncMenuButton<MoralArgumentSectionTypeViewModel> {
        root.addClass(Styles.sectionTypeSelection)
        root.useMaxWidth = true

        root.pane {

            visibleProperty().bind(
                root.hoverProperty()
                    .or(root.focusedProperty())
                    .or(tryingToInsertProperty)
            )

            line(0.0, 8.0, endY = 8.0) {
                stroke = com.soyle.stories.soylestories.Styles.Purple
                strokeWidth = 4.0
                endXProperty().bind(this@pane.widthProperty())
            }
            rectangle(8.0, 0.0, 16.0, 16.0) {
                visibleWhen(notTryingToInsertProperty)
                fill = com.soyle.stories.soylestories.Styles.Purple
            }
            line(16.0, 3.0, 16.0, 13.0) {
                visibleWhen(notTryingToInsertProperty)
                stroke = Color.WHITE
                strokeWidth = 2.0
            }
            line(11.0, 8.0, 21.0, 8.0) {
                visibleWhen(notTryingToInsertProperty)
                stroke = Color.WHITE
                strokeWidth = 2.0
            }
        }

        loadingLabelProperty.bind(state.loadingSectionTypesLabel)
        sourceProperty.bind(state.availableSectionTypes)
        onLoad = ::getAvailableSectionTypesToAdd
        itemsWhenLoaded(::createSectionTypeItems)

    }.root

    private fun getAvailableSectionTypesToAdd() {
        state.availableSectionTypes.value = null
        state.item?.selectedPerspectiveCharacter?.characterId?.let {
            viewListener.getAvailableArcSectionTypesToAdd(it)
        }
    }

    private fun createSectionTypeItems(sectionTypes: List<MoralArgumentSectionTypeViewModel>): List<MenuItem> {
        return sectionTypes.groupBy { it.canBeCreated }.withDefault { listOf() }
            .run {
                getValue(true).map(::createCreatableSectionValueTypeItem) +
                        getValue(false).map(::createUsedSectionValueTypeItem)
            }
    }

    private fun createCreatableSectionValueTypeItem(sectionType: MoralArgumentSectionTypeViewModel): MenuItem {
        return MenuItem(sectionType.sectionTypeName).apply {
            userData = sectionType
            action { addSectionType(sectionType.sectionTypeId) }
        }
    }

    private fun addSectionType(sectionTypeId: String) {
        val characterId = state.item?.selectedPerspectiveCharacter?.characterId ?: return
        val addIndex = addToTopOfSection?.let { addToTopOfSection ->
            state.sections.indexOf(addToTopOfSection).takeIf { it >= 0 }
        }
        if (addIndex == null) {
            viewListener.addCharacterArcSectionType(characterId, sectionTypeId)
        } else {
            viewListener.addCharacterArcSectionTypeAtIndex(characterId, sectionTypeId, addIndex)
        }
    }

    private fun createUsedSectionValueTypeItem(sectionType: MoralArgumentSectionTypeViewModel): MenuItem {
        return CustomMenuItem().apply {
            text = sectionType.sectionTypeName
            addClass(ComponentsStyles.discouragedSelection)
            userData = sectionType
            content = Label(sectionType.sectionTypeName).apply {
                tooltip {
                    textProperty().bind(usedSectionMessage(sectionType))
                }
            }
        }
    }

    private fun usedSectionMessage(sectionType: MoralArgumentSectionTypeViewModel): ObservableValue<String> {
        return state.unavailableSectionTypeMessage.stringBinding { it?.invoke(sectionType) }
    }

    class Styles : Stylesheet() {

        companion object {

            val sectionTypeSelection by cssclass()

            init {
                importStylesheet<Styles>()
            }
        }

        init {
            sectionTypeSelection {
                backgroundColor = multi(Color.TRANSPARENT)
                and(focused) {
                    unsafe("-fx-background-color", raw("-fx-focus-color, -fx-inner-border, transparent, -fx-faint-focus-color, transparent"))
                }
                label {
                    padding = box(0.px)
                }
                arrowButton {
                    visibility = FXVisibility.HIDDEN
                    padding = box(0.px)
                    arrow {
                        padding = box(0.px)
                        backgroundInsets = multi(box(0.px))
                        shape = ""
                    }
                }
            }
        }

    }

}