package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.*

class StoryEventDetails : View() {

	override val scope = super.scope as StoryEventDetailsScope

	private val viewListener = resolve<StoryEventDetailsViewListener>()
	private val model = resolve<StoryEventDetailsModel>()

	private val locationSelectionList = ContextMenu().apply {
		isAutoHide = true
		isAutoFix = true
		items.bind(model.locations) { location ->
			checkmenuitem(location.name) {
				model.selectedLocation.selectBoolean { (it?.id == location.id).toProperty() }.onChange {
					selectedProperty().set(it)
				}
				setOnAction {
					it.consume()
					if (model.selectedLocation.value?.id == location.id) {
						viewListener.deselectLocation()
					} else {
						viewListener.selectLocation(location.id)
					}
					this@apply.hide()
				}
			}
		}
	}

	private val characterSelectionList = ContextMenu().apply {
		items.bind(model.availableCharacters) { character ->
			item(character.characterName) {
				setOnAction {
					it.consume()
					viewListener.addCharacter(character.characterId)
					this@apply.hide()
				}
			}
		}
	}

	override val root: Parent = form {
		fieldset("Location") {
			field {
				button {

					textProperty().bind(model.itemProperty.select {
						(it.selectedLocation?.name ?: it.locationSelectionButtonLabel).toProperty()
					})

					id = "location-select"
					enableWhen { model.hasLocations }
					contextMenu = locationSelectionList
					setOnAction {
						it.consume()
						contextMenu.show(this, Side.BOTTOM, 0.0, 0.0)
					}

				}
			}
		}
		fieldset("Characters") {
			vbox {
				bindChildren(model.includedCharacters) {
					hbox(alignment = Pos.CENTER_LEFT, spacing = 5) {
						id = it.characterId
						addClass("included-character")
						label(it.characterName)
						button("Remove Character") {
							action {
								viewListener.removeCharacter(it.characterId)
							}
						}
					}
				}
			}
			field {
				button {
					addClass("character-select")
					enableWhen { model.hasCharacters }

					contextMenu = characterSelectionList
					setOnAction {
						it.consume()
						contextMenu.show(this, Side.BOTTOM, 0.0, 0.0)
					}
				}
			}
		}
	}

	init {
		titleProperty.bind(model.title)
		viewListener.getValidState()
	}

}

fun TabPane.storyEventDetailsTab(projectScope: ProjectScope, storyEventDetails: com.soyle.stories.layout.tools.dynamic.StoryEventDetails): Tab {
	val scope = StoryEventDetailsScope(projectScope, storyEventDetails)
	val structure = find<StoryEventDetails>(scope = scope)
	val tab = tab(structure)
	tab.tabPaneProperty().onChange {
		if (it == null) {
			scope.close()
		}
	}
	return tab
}