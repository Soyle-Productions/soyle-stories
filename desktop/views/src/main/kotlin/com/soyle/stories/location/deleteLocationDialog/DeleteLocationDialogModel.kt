package com.soyle.stories.location.deleteLocationDialog


import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.resolveLater
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogViewModel
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBenchModel
import com.soyle.stories.project.layout.Dialog
import javafx.beans.property.SimpleObjectProperty
import tornadofx.ItemViewModel
import tornadofx.select
import tornadofx.toProperty

class DeleteLocationDialogModel : DeleteLocationDialogView, ItemViewModel<DeleteLocationDialogViewModel>() {

	override val scope: ProjectScope = super.scope as ProjectScope

	val dialog = find<WorkBenchModel>().openDialogs.select { (it[Dialog.DeleteLocation::class] as? Dialog.DeleteLocation?).toProperty() } as SimpleObjectProperty
	val isOpen = dialog.isNotNull

	val locationId = bind(DeleteLocationDialogViewModel::locationId)
	val locationName = bind(DeleteLocationDialogViewModel::locationName)

	val threadTransformer: ThreadTransformer by resolveLater(scope.applicationScope)

	override val viewModel: DeleteLocationDialogViewModel? = item

	override fun update(update: DeleteLocationDialogViewModel?.() -> DeleteLocationDialogViewModel) {
		threadTransformer.gui {
			item = item.update()
		}
	}

	override fun updateOrInvalidated(update: DeleteLocationDialogViewModel.() -> DeleteLocationDialogViewModel) {
		threadTransformer.gui {
			item = item?.update()
		}
	}

	override fun updateIf(
		condition: DeleteLocationDialogViewModel.() -> Boolean,
		update: DeleteLocationDialogViewModel.() -> DeleteLocationDialogViewModel
	) {
		threadTransformer.gui {
			if (item.condition()) {
				item = item?.update()
			}
		}
	}

}