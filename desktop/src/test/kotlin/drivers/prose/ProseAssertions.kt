package com.soyle.stories.desktop.config.drivers.prose

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.domain.prose.Prose
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals

object ProseAssertions {

    fun proseTextIs(proseId: Prose.Id, expectedText: String)
    {
        val prose = ProseDriver(soyleStories.getAnyOpenWorkbenchOrError())
            .getProseByIdOrError(proseId)
        assertEquals(expectedText, prose.content)
    }

    fun proseDoesContainMention(proseId: Prose.Id, mentionName: String)
    {
        val prose = ProseDriver(soyleStories.getAnyOpenWorkbenchOrError())
            .getProseByIdOrError(proseId)
        val mention = prose.mentions.find { prose.content.substring(it.start(), it.end()) == mentionName }
        Assertions.assertNotNull(mention) { "Prose doesn't contain mention with text $mentionName" }
    }

    fun proseDoesNotContainMention(proseId: Prose.Id, mentionName: String)
    {
        val prose = ProseDriver(soyleStories.getAnyOpenWorkbenchOrError())
            .getProseByIdOrError(proseId)
        val mention = prose.mentions.find { prose.content.substring(it.start(), it.end()) == mentionName }
        Assertions.assertNull(mention) { "Prose still contains mention with text $mentionName" }
    }

    fun proseDoesNotContainText(proseId: Prose.Id, text: String)
    {
        val prose = ProseDriver(soyleStories.getAnyOpenWorkbenchOrError())
            .getProseByIdOrError(proseId)
        Assertions.assertFalse(prose.content.contains(text))
    }

}