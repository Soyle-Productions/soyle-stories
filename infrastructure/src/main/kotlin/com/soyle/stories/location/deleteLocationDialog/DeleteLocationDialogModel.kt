package com.soyle.stories.location.deleteLocationDialog

import com.soyle.stories.common.ThreadTransformerImpl
import com.soyle.stories.project.WorkBenchModel
import com.soyle.stories.project.layout.Dialog
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ItemViewModel
import tornadofx.select
import tornadofx.toProperty

class DeleteLocationDialogModel : DeleteLocationDialogView, ItemViewModel<DeleteLocationDialogViewModel>() {

	val dialog = find<WorkBenchModel>().openDialogs.select { (it[Dialog.DeleteLocation::class] as? Dialog.DeleteLocation?).toProperty() } as SimpleObjectProperty
	val isOpen = dialog.isNotNull

	val locationId = bind(DeleteLocationDialogViewModel::locationId)
	val locationName = bind(DeleteLocationDialogViewModel::locationName)

	override fun update(update: DeleteLocationDialogViewModel?.() -> DeleteLocationDialogViewModel) {
		ThreadTransformerImpl.gui {
			item = item.update()
		}
	}

	override fun updateOrInvalidated(update: DeleteLocationDialogViewModel.() -> DeleteLocationDialogViewModel) {
		ThreadTransformerImpl.gui {
			item = item?.update()
		}
	}

}