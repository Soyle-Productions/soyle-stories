package com.soyle.stories.location.locationDetails

import com.soyle.stories.di.resolveLater
import com.soyle.stories.gui.ThreadTransformer
import tornadofx.ItemViewModel

class LocationDetailsModel : ItemViewModel<LocationDetailsViewModel>(), LocationDetailsView {

	override val scope: LocationDetailsScope = super.scope as LocationDetailsScope

	val toolName = bind(LocationDetailsViewModel::toolName)
	val descriptionLabel = bind(LocationDetailsViewModel::descriptionLabel)
	val description = bind(LocationDetailsViewModel::description)

	private val threadTransformer by resolveLater<ThreadTransformer>(scope.projectScope.applicationScope)

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

}