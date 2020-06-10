package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.characterarc.components.characterNameModel
import com.soyle.stories.di.resolve
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.layout.Priority
import tornadofx.*

class SceneDetails : View("Scene") {

	override val scope: SceneDetailsScope = super.scope as SceneDetailsScope
	private val viewListener = resolve<SceneDetailsViewListener>()
	private val model = resolve<SceneDetailsModel>()

	override val root: Parent = form {
		fieldset {
			textProperty.bind(model.locationSectionLabel)
			field {
				button {
					addClass("location-select")
					enableWhen {
						model.availableLocations.select {
							(!it.isNullOrEmpty() || model.selectedLocation.value != null).toProperty()
						}
					}
					textProperty().bind(model.select {
						(it.selectedLocation?.name ?: it.locationDropDownEmptyLabel).toProperty()
					})
					contextMenu = ContextMenu().apply {
						items.bind(model.availableLocations) {
							item(it.name) {
								action {
									viewListener.linkLocation(it.id)
								}
							}
						}
					}
					action {
						contextMenu?.show(this, Side.BOTTOM, 0.0, 0.0)
					}
				}
			}
		}
		stackpane {
			fitToParentWidth()
			fieldset(labelPosition = Orientation.VERTICAL) {
				textProperty.bind(model.charactersSectionLabel)
				fitToParentWidth()
				vbox {
					fitToParentWidth()
					bindChildren(model.includedCharacters) {
						field {
							textProperty.bind(scope.projectScope.characterNameModel(it.characterId, it.characterName))
							addClass("included-character")
							fitToParentWidth()
							hbox(spacing = 10) {
								fitToParentWidth()
								vbox {
									hgrow = Priority.ALWAYS/*
									textfield(it.motivation) {

									}*/
									hbox {
										hyperlink(model.resentButtonLabel) {
											visibleWhen { it.canReset.toProperty() }
											managedProperty().bind(visibleProperty())
										}
										label(model.lastChangedTipLabel) {
											visibleWhen { (it.previousMotivationSource != null).toProperty() }
											managedProperty().bind(visibleProperty())

										}
									}
								}
								button(model.removeCharacterButtonLabel) {
									hgrow = Priority.NEVER
									action {
										viewListener.removeCharacter(model.storyEventId.value!!, it.characterId)
									}
								}
							}
						}
					}
				}
			}
			button(model.addCharacterButtonLabel) {
				addClass("add-character")
				enableWhen {
					model.availableCharacters.select {
						(!it.isNullOrEmpty()).toProperty()
					}
				}
				stackpaneConstraints {
					alignment = Pos.TOP_RIGHT
				}
				contextMenu = ContextMenu().apply {
					items.bind(model.availableCharacters) {
						item(it.characterName) {
							action {
								viewListener.addCharacter(model.storyEventId.value!!, it.characterId)
							}
						}
					}
				}
				action {
					contextMenu?.show(this, Side.BOTTOM, 0.0, 0.0)
				}
			}
		}
	}

	init {
		viewListener.getValidState()
	}

}