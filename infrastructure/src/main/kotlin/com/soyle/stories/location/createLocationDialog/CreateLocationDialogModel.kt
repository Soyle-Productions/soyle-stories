package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.di.resolveLater
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBenchModel
import com.soyle.stories.project.layout.Dialog
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.select
import tornadofx.toProperty

class CreateLocationDialogModel : CreateLocationDialogView, ItemViewModel<CreateLocationDialogViewModel>() {

	override val scope: ProjectScope = super.scope as ProjectScope

	val isOpen = find<WorkBenchModel>().openDialogs.select { it.contains(Dialog.CreateLocation::class).toProperty() }
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