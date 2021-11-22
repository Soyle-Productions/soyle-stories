package com.soyle.stories.scene.delete.ramifications

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.di.resolveLater
import com.soyle.stories.gui.View
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogViewModel
import com.soyle.stories.usecase.scene.common.AffectedScene
import javafx.collections.ObservableList
import tornadofx.ItemViewModel
import tornadofx.observableListOf
import tornadofx.rebind

class DeleteSceneRamificationsReportViewModel {

	val affectedScenes: ObservableList<AffectedScene> = observableListOf()

	private var onDelete: () -> Unit = {}

	fun setOnDelete(handler: () -> Unit) {
		onDelete = handler
	}

	fun delete() {
		onDelete()
	}

	private var onCancel: () -> Unit = {}

	fun setOnCancel(handler: () -> Unit) {
		onCancel = handler
	}

	fun cancel() {
		onCancel()
	}

}