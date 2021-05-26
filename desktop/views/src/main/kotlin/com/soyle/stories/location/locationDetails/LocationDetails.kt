package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.components.text.SectionTitle.Companion.section
import com.soyle.stories.common.hideScrollbars
import com.soyle.stories.common.rowCountProperty
import com.soyle.stories.common.softBind
import com.soyle.stories.di.resolveLater
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Region
import tornadofx.*

class LocationDetails : View() {

	override val scope: LocationDetailsScope = super.scope as LocationDetailsScope
	private val model by resolveLater<LocationDetailsModel>()
	private val locationDetailsViewListener by resolveLater<LocationDetailsViewListener>()

	override val root: Parent = scrollpane {
		hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
		vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
		isFitToWidth = true
		content = section(model.descriptionLabel) {
			style { padding = box(16.px) }
			textarea {
				id = "description"
				isWrapText = true
				minWidth = Region.USE_COMPUTED_SIZE
				fitToParentWidth()
				prefRowCount = 10
				textProperty().softBind(model.description) { it }
				focusedProperty().onChange {
					if (! it) {
						if (text != model.description.value) {
							locationDetailsViewListener.reDescribeLocation(text)
						}
					}
				}
			}
		}
	}

	init {
		titleProperty.bind(model.toolName)
		locationDetailsViewListener.getValidState()
	}
}