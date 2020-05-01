package com.soyle.stories.soylestories

import com.soyle.stories.project.WorkBench
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Component
import tornadofx.ScopedInstance
import tornadofx.bind
import tornadofx.find

class SoyleStoriesView : Component(), ScopedInstance {

	override val scope = super.scope as ApplicationScope

	val projectViews: ObservableList<WorkBench> = FXCollections.observableArrayList()

	init {
		projectViews.bind(scope.projectScopesProperty) {
			find(scope = it)
		}
	}

}