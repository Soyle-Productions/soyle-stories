package com.soyle.stories.scene.deleteSceneRamifications

import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

internal fun EventTarget.sceneItem(viewModel: SceneRamificationsViewModel) = titledpane(viewModel.sceneName, collapsible = true) {
	addClass("scene-item")
	id = viewModel.sceneId
	isExpanded = true
	content = form{
		vbox {
			viewModel.characters.forEach {
				fieldset(it.characterName, labelPosition = Orientation.VERTICAL) {
					addClass("character-item")
					id = it.characterId
					hbox {
						field("Current Motivation") {
							hgrow = Priority.ALWAYS
							textfield(it.currentMotivation) {
								isDisable = true
								addClass("current")
							}
						}
						field("Changed Motivation") {
							hgrow = Priority.ALWAYS
							textfield(it.changedMotivation) {
								isDisable = true
								addClass("changed")
							}
						}
					}
				}
			}
		}
	}
}