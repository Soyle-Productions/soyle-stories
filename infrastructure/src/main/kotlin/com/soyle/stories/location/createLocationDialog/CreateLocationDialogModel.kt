package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.resolveLater
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel

class CreateLocationDialogModel : CreateLocationDialogView, ItemViewModel<CreateLocationDialogViewModel>() {

	override val scope: ProjectScope = super.scope as ProjectScope

	val isOpen = bind(CreateLocationDialogViewModel::isOpen)
	val threadTransformer: ThreadTransformer by resolveLater(scope.applicationScope)

	val errorMessage = bind(CreateLocationDialogViewModel::errorMessage) as SimpleStringProperty

	override fun update(update: CreateLocationDialogViewModel?.() -> CreateLocationDialogViewModel) {
		val threadTransformer = threadTransformer
		threadTransformer.gui {
			item = item.update()
		}
	}

	override fun updateOrInvalidated(update: CreateLocationDialogViewModel.() -> CreateLocationDialogViewModel) {
		val threadTransformer = threadTransformer
		threadTransformer.gui {
			item = item?.update()
		}
	}

}