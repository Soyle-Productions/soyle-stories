package com.soyle.stories.desktop.config.features.writer


import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.writer.WriterDriver
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.writer.DialogType
import io.cucumber.java8.En

class `Writer Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("I have requested to not be prompted to confirm deleting a scene") {
            WriterDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenDialogShouldNotBeShown(DialogType.DeleteScene)
        }
        Given("I have requested to not be prompted to confirm removing a story event") {
            WriterDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenDialogShouldNotBeShown(DialogType.DeleteStoryEvent)
        }
    }

    private fun whens() {

    }

    private fun thens() {

    }

}