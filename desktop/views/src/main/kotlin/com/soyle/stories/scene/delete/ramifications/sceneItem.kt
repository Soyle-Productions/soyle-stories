package com.soyle.stories.scene.delete.ramifications

import com.soyle.stories.usecase.scene.common.AffectedScene
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

internal fun EventTarget.sceneItem(viewModel: AffectedScene) = titledpane(viewModel.sceneName, collapsible = true) {
	addClass("scene-item")
	id = viewModel.sceneId.toString()
	isExpanded = true
	content = form{
		vbox {
			viewModel.characters.forEach {
				fieldset(it.characterName, labelPosition = Orientation.VERTICAL) {
					addClass("character-item")
					id = it.characterId.toString()
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
							textfield(it.potentialMotivation) {
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