package com.soyle.stories.prose.proseEditor

import com.soyle.stories.di.get
import javafx.scene.Parent
import javafx.scene.text.FontWeight
import tornadofx.*

class MentionSuggestion : ListCellFragment<MatchingStoryElementViewModel>() {

    private val namePartBeforeMatch = itemProperty.stringBinding { it?.name?.substring(0, it.matchingRange.first) ?: "" }
    private val namePartMatching = itemProperty.stringBinding { it?.name?.substring(it.matchingRange) ?: "" }
    private val namePartAfterMatch = itemProperty.stringBinding { it?.name?.substring(it.matchingRange.last+1, it.name.length) ?: "" }
    private val addendum = itemProperty.stringBinding { item -> item?.addendum?.let { " - $it" } ?: "" }

    override val root: Parent = hbox {
        textflow {
            label(namePartBeforeMatch)
            label(namePartMatching) {
                style { fontWeight = FontWeight.BLACK }
            }
            label(namePartAfterMatch)
            label(addendum) {
                style { opacity = 0.4 }
            }
        }
        spacer()
        label(itemProperty.stringBinding { it?.type ?: "" })
    }

    init {
        root.setOnMouseClicked { mouseEvent ->
            if (mouseEvent.clickCount == 2) {
                cell?.index?.let {
                    scope.get<ProseEditorViewListener>().selectStoryElement(it, false)
                }
            }
        }
    }

}