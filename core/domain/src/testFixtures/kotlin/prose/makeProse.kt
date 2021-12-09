package com.soyle.stories.domain.prose

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.validation.SingleLine
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.validation.countLines
import java.util.*

fun makeProse(
    id: Prose.Id = Prose.Id(),
    projectId: Project.Id = Project.Id(),
    content: List<ProseContent> = listOf(),
    revision: Long = LongRange(0L, Long.MAX_VALUE).random()
): Prose {
    return Prose.build(
        id,
        projectId,
        content,
        revision
    )
}

fun buildProse(
    id: Prose.Id = Prose.Id(),
    projectId: Project.Id = Project.Id(),
    revision: Long = 0,
    builder: ProseBuilder.() -> String
): Prose {
    return Prose.build(
        id,
        projectId,
        ProseBuilder(builder).toContent(),
        revision
    )
}

class ProseBuilder(builder: ProseBuilder.() -> String) {

    operator fun Character.invoke(selector: Character.() -> NonBlankString): String
    {
        return "_$id{${selector().value}}_\$"
    }
    operator fun Location.invoke(selector: Location.() -> SingleNonBlankLine): String
    {
        return "_$id{${selector().value}}_\$"
    }

    private val builderText = builder()

    fun toContent(): List<ProseContent> {
        val mentionLines = builderText.split("\$")
        return mentionLines.map {
            if (it.endsWith("_")) {
                val parts = it.split("_")
                val plainTest = parts[0]
                val mentionText = parts[1]
                // parts[2] should be empty

                ProseContent(
                    plainTest,
                    when {
                        mentionText.startsWith("Character") -> {
                            val uuidStr = mentionText.substring(10, UUID.randomUUID().toString().length + 10)
                            val nameValue = mentionText.substring(UUID.randomUUID().toString().length + 12).takeWhile { it != '}' }
                            Character.Id(UUID.fromString(uuidStr)).mentioned() to countLines(nameValue) as SingleLine
                        }
                        mentionText.startsWith("Location") -> {
                            val uuidStr = mentionText.substring(9, UUID.randomUUID().toString().length + 9)
                            val nameValue = mentionText.substring(UUID.randomUUID().toString().length + 11).takeWhile { it != '}' }
                            Location.Id(UUID.fromString(uuidStr)).mentioned() to countLines(nameValue) as SingleLine
                        }
                        mentionText.startsWith("Symbol") -> {
                            val uuidStr = mentionText.substring(7, UUID.randomUUID().toString().length + 7)
                            val nameValue = mentionText.substring(UUID.randomUUID().toString().length + 9).takeWhile { it != '}' }
                            Symbol.Id(UUID.fromString(uuidStr)).mentioned(Theme.Id()) to countLines(nameValue) as SingleLine
                        }
                        else -> error("unrecognized mention id")
                    }
                )
            } else {
                ProseContent(it, null)
            }
        }
    }

}