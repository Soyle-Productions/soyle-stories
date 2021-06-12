package com.soyle.stories.desktop.view.location.details

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.details.components.HostSceneButton
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem

class `Host Scene Button Access`(val button: HostSceneButton) {

    companion object {
        fun HostSceneButton.access() = `Host Scene Button Access`(this)
    }

    val createSceneItem: MenuItem?
        get() = button.items.find { it.id == "createScene" }

    val availableSceneItems: List<MenuItem>
        get() = button.items.asSequence()
            .filterNot { it is SeparatorMenuItem }
            .filterNot { it.id == "createScene" || it.id == "loading" }
            .toList()

    fun getSceneItemById(sceneId: Scene.Id): MenuItem?
    {
        return availableSceneItems.find { it.id == sceneId.toString() }
    }

}