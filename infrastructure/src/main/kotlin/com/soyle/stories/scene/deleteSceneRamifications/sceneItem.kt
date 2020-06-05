package com.soyle.stories.scene.deleteSceneRamifications

import javafx.event.EventTarget
import tornadofx.*

internal fun EventTarget.sceneItem(viewModel: SceneRamificationsViewModel) = titledpane(viewModel.sceneName, collapsible = true) {
	addClass("scene-item")
	id = viewModel.sceneId
	isExpanded = true
	content = form{
		vbox {
			viewModel.characters.forEach {
				fieldset(it.characterName) {
					addClass("character-item")
					id = it.characterId
					hbox {
						field("Current Motivation") {
							label(it.currentMotivation) {
								addClass("current")
							}
						}
						field("Changed Motivation") {
							label(it.changedMotivation) {
								addClass("changed")
							}
						}
					}
				}
			}
		}
	}
}