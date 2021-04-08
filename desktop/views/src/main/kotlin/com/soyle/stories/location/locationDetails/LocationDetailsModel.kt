package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.resolveLater
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogViewModel
import tornadofx.ItemViewModel

class LocationDetailsModel : ItemViewModel<LocationDetailsViewModel>(), LocationDetailsView {

	override val scope: LocationDetailsScope = super.scope as LocationDetailsScope

	val toolName = bind(LocationDetailsViewModel::toolName)
	val descriptionLabel = bind(LocationDetailsViewModel::descriptionLabel)
	val description = bind(LocationDetailsViewModel::description)

	private val threadTransformer by resolveLater<ThreadTransformer>(scope.projectScope.applicationScope)

	override val viewModel: LocationDetailsViewModel? = item

	override fun update(update: LocationDetailsViewModel?.() -> LocationDetailsViewModel) {
		threadTransformer.gui {
			item = item.update()
		}
	}

	override fun updateOrInvalidated(update: LocationDetailsViewModel.() -> LocationDetailsViewModel) {
		threadTransformer.gui {
			item = item?.update() ?: return@gui invalidated()
		}
	}

	private fun invalidated() {}

	override fun updateIf(
		condition: LocationDetailsViewModel.() -> Boolean,
		update: LocationDetailsViewModel.() -> LocationDetailsViewModel
	) {
		threadTransformer.gui {
			if (item.condition()) {
				item = item?.update()
			}
		}
	}

}