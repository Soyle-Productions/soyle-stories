package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.*
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.asyncMenuButton.AsyncMenuButton.Companion.asyncMenuButton
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.components.text.SectionTitle.Companion.section
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.di.resolveLater
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Parent
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Region
import tornadofx.*

class LocationDetails : View() {

	override val scope: LocationDetailsScope = super.scope as LocationDetailsScope
	private val model by resolveLater<LocationDetailsModel>()
	private val viewListener by resolveLater<LocationDetailsViewListener>()

	override val root: Parent = locationDetailsRoot {
		descriptionField()
		hostedScenesList()
	}

	private fun locationDetailsRoot(createChildren: Parent.() -> Unit) = scrollpane {
		hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
		vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
		isFitToWidth = true
		content = vbox { createChildren() }
	}

	@ViewBuilder
	private fun Parent.descriptionField() {
		section(model.descriptionLabelProperty) {
			style { padding = box(16.px) }
			descriptionInput()
		}
	}

	@ViewBuilder
	private fun Parent.descriptionInput() {
		textarea {
			id = "description"
			isWrapText = true
			minWidth = Region.USE_COMPUTED_SIZE
			fitToParentWidth()
			prefRowCount = 10
			textProperty().softBind(model.descriptionProperty) { it }
			focusedProperty().onChange {
				if (! it) {
					if (text != model.descriptionProperty.value) {
						viewListener.reDescribeLocation(text)
					}
				}
			}
		}
	}

	@ViewBuilder
	private fun Parent.hostedScenesList() {
		vbox {
			addClass(TextStyles.section)
			dynamicContent(model.hostedScenesProperty.emptyProperty()) {
				hostedScenesListHeader(it == false)
				hostedScenesListBody(it == false)
			}
		}
	}

	@ViewBuilder
	private fun Parent.hostedScenesListHeader(hasScenes: Boolean) {
		hbox {
			sectionTitle("Hosted Scenes")
			if (hasScenes) {
				addSceneButton(secondary = true)
			}
		}
	}
	@ViewBuilder
	private fun Parent.hostedScenesListBody(hasScenes: Boolean) {
		if (hasScenes) {
			hostedSceneItems()
		} else {
			addSceneInvitation()
		}
	}
	@ViewBuilder
	private fun Parent.addSceneInvitation() {
		vbox {
			fieldLabel("Nothing has ever or will ever happen here.  Sounds boring.  Why not spice this place up by adding a scene or five?")
			addSceneButton()
		}
	}
	@ViewBuilder
	private fun Parent.addSceneButton(secondary: Boolean = false) {
		asyncMenuButton<AvailableSceneToHostViewModel> {
			root.apply {
				id = "add-scene"
				addClass(ComponentsStyles.outlined)
				addClass(ComponentsStyles.secondary)
				if (!secondary) addClass(ButtonStyles.inviteButton)
			}
			sourceProperty.bind(model.availableScenesToHostProperty)
			itemsWhenLoaded {
				it.map { MenuItem(it.sceneName).apply { id = it.sceneId.toString() } }
			}
			onLoad = { viewListener.getAvailableScenesToHost() }
		}
	}
	@ViewBuilder
	private fun Parent.hostedSceneItems() {
		vbox {
			bindChildren(model.hostedScenesProperty) {
				label {
					id = it.sceneId.toString()
					textProperty().bind(it.nameProperty)
					addClass("hosted-scene-item")
				}
			}
		}
	}

	init {
		titleProperty.bind(model.toolNameProperty)
		viewListener.getValidState()
	}
}
