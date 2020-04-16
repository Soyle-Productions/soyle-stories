package com.soyle.stories.location.createLocationDialog

import com.soyle.stories.common.ThreadTransformerImpl
import com.soyle.stories.project.WorkBenchModel
import com.soyle.stories.project.layout.Dialog
import tornadofx.ItemViewModel
import tornadofx.select
import tornadofx.toProperty

class CreateLocationDialogModel : CreateLocationDialogView, ItemViewModel<CreateLocationDialogViewModel>() {

	val isOpen = find<WorkBenchModel>().openDialogs.select { it.contains(Dialog.CreateLocation::class).toProperty() }

	val errorMessage = bind(CreateLocationDialogViewModel::errorMessage)

	override fun update(update: CreateLocationDialogViewModel?.() -> CreateLocationDialogViewModel) {
		ThreadTransformerImpl.gui {
			item = item.update()
		}
	}

	override fun updateOrInvalidated(update: CreateLocationDialogViewModel.() -> CreateLocationDialogViewModel) {
		ThreadTransformerImpl.gui {
			item = item?.update()
		}
	}

}