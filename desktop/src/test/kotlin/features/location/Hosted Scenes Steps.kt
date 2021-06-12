package com.soyle.stories.desktop.config.features.location

import com.soyle.stories.desktop.config.drivers.location.*
import com.soyle.stories.desktop.config.drivers.scene.createSceneWithName
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import io.cucumber.java8.En

class `Hosted Scenes Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {

    }

    private fun whens() {
        When("I create a scene named {string} to host in the {location}") { sceneName: String, location: Location ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenLocationListToolHasBeenOpened()
                .givenLocationDetailsToolHasBeenOpenedFor(location)
                .givenRequestedSceneToHost()
                .selectCreateSceneOption()
                .createSceneWithName(sceneName)
        }
        When("I remove the {scene} from the {location}") { scene: Scene, location: Location ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenLocationListToolHasBeenOpened()
                .givenLocationDetailsToolHasBeenOpenedFor(location)
                .removeScene(scene.id)
        }
    }

    private fun thens() {

    }

}