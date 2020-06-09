package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import tornadofx.*

class SceneDetails : View() {

	override val scope: SceneDetailsScope = super.scope as SceneDetailsScope
	private val viewListener = resolve<SceneDetailsViewListener>()
	private val model = resolve<SceneDetailsModel>()

	override val root: Parent = form {
		fieldset {
			textProperty.bind(model.locationSectionLabel)
			field {
				button {
					textProperty().bind(model.select {
						(it.selectedLocation?.name ?: "[${it.locationDropDownEmptyLabel}]").toProperty()
					})
					contextMenu = ContextMenu().apply {
						items.bind(model.availableLocations) {
							item(it.name)
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
						field(it.characterName) {
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
								}/*
								button(model.removeCharacterButtonLabel) {
									hgrow = Priority.NEVER
								}*/
							}
						}
					}
				}
			}
			button(model.addCharacterButtonLabel) {
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