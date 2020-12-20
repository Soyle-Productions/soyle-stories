package com.soyle.stories.prose.proseEditor

import javafx.scene.Parent
import tornadofx.*

class MentionSuggestion : ListCellFragment<MatchingStoryElementViewModel>() {

    private val namePartBeforeMatch = itemProperty.stringBinding { it?.name?.substring(0, it.matchingRange.first) ?: "" }
    private val namePartMatching = itemProperty.stringBinding { it?.name?.substring(it.matchingRange) ?: "" }
    private val namePartAfterMatch = itemProperty.stringBinding { it?.name?.substring(it.matchingRange.last+1, it.name.length) ?: "" }

    override val root: Parent = hbox {
        textflow {
            text(namePartBeforeMatch)
            text(namePartMatching)
            text(namePartAfterMatch)
        }
        spacer()
        label(itemProperty.stringBinding { it?.type ?: "" })
    }

}