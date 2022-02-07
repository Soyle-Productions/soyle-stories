package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.existsWhen
import com.soyle.stories.common.sizeToFitItems
import com.soyle.stories.di.get
import javafx.beans.value.ObservableValue
import javafx.scene.control.ListView
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import javafx.stage.Popup
import tornadofx.*

class MatchingStoryElementsPopup(
    private val scope: Scope,
    private val mentionQueryState: ObservableValue<MentionQueryState>
) : Popup() {

    private val viewListener = scope.get<ProseEditorViewListener>()

    private val itemList = itemList()
    private val root = VBox().apply {
        isFillWidth = true
        label("Loading...") {
            existsWhen(mentionQueryStateIsLoading())
        }
        add(itemList)
        label("")
    }

    init {
        content.setAll(root)
        FX.applyStylesheetsTo(root.scene)

        itemList.setOnKeyPressed {
            if (it.code == KeyCode.ENTER) {
                viewListener.selectStoryElement(itemList.selectionModel.selectedIndex, it.isShiftDown)
            }
        }
    }

    private fun itemList(): ListView<MatchingStoryElementViewModel>
    {
        return ListView<MatchingStoryElementViewModel>().apply {
            cellFragment(scope, MentionSuggestion::class)
            whenMentionQueryStateIsLoaded {
                items.setAll(it.items)
                selectionModel.select(0)
                runLater { sizeToFitItems() }
            }
        }
    }

    private fun mentionQueryStateIsLoading() = mentionQueryState.booleanBinding { it is MentionQueryLoading }
    private fun whenMentionQueryStateIsLoaded(observer: (MentionQueryLoaded) -> Unit)
    {
        mentionQueryState.onChange {
            if (it is MentionQueryLoaded) observer(it)
        }
    }

}