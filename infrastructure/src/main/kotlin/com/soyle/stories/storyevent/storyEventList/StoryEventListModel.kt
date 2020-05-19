package com.soyle.stories.storyevent.storyEventList

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.di.resolveLater
import com.soyle.stories.gui.View
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.items.StoryEventListItemViewModel
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ItemViewModel
import tornadofx.rebind
import tornadofx.toProperty

class StoryEventListModel : ItemViewModel<StoryEventListViewModel>(), View.Nullable<StoryEventListViewModel> {

	override val scope = super.scope as ProjectScope

	val toolTitle = bind(StoryEventListViewModel::toolTitle)
	val emptyLabel = bind(StoryEventListViewModel::emptyLabel)
	val createStoryEventButtonLabel = bind(StoryEventListViewModel::createStoryEventButtonLabel)
	val storyEvents = bindImmutableList(StoryEventListViewModel::storyEvents)
	val hasStoryEvents = bind { (! item?.storyEvents.isNullOrEmpty()).toProperty() }
	val selectedItem = SimpleObjectProperty<StoryEventListItemViewModel?>(null)
	val renameStoryEventFailureMessage = bind(StoryEventListViewModel::renameStoryEventFailureMessage)

	private val threadTransformer by resolveLater<ThreadTransformer>(scope.applicationScope)

	override fun update(update: StoryEventListViewModel?.() -> StoryEventListViewModel) {
		threadTransformer.gui {
			rebind { item = item.update() }
		}
	}

	override fun updateOrInvalidated(update: StoryEventListViewModel.() -> StoryEventListViewModel) {
		threadTransformer.gui {
			rebind { item = item?.update() }
		}
	}
}