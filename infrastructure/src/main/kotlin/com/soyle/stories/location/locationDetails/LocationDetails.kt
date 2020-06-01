package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.hideScrollbars
import com.soyle.stories.common.rowCountProperty
import com.soyle.stories.di.resolveLater
import com.soyle.stories.project.ProjectScope
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Region
import tornadofx.*

class LocationDetails : View() {

	override val scope: LocationDetailsScope = super.scope as LocationDetailsScope
	private val model by resolveLater<LocationDetailsModel>()
	private val locationDetailsViewListener by resolveLater<LocationDetailsViewListener>()

	override val root: Parent = form {
		fieldset(labelPosition = Orientation.VERTICAL) {
			field {
				textProperty.bind(model.descriptionLabel)
				textarea {
					id = "description"
					isWrapText = true
					minWidth = Region.USE_COMPUTED_SIZE
					fitToParentWidth()
					hideScrollbars()
					prefRowCountProperty().bind(rowCountProperty)
					model.description.onChange { text = it ?: "" }
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
	}

	init {
		titleProperty.bind(model.toolName)
		locationDetailsViewListener.getValidState()
	}
}

fun TabPane.locationDetailsTab(projectScope: ProjectScope, locationDetails: com.soyle.stories.layout.tools.dynamic.LocationDetails): Tab {
	val scope = LocationDetailsScope(projectScope, locationDetails)
	val structure = find<LocationDetails>(scope = scope)
	val tab = tab(structure)
	tab.tabPaneProperty().onChange {
		if (it == null) {
			scope.close()
		}
	}
	return tab
}