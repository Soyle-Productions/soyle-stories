package com.soyle.stories.common.markdown

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.text.TextStyles
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import tornadofx.*
import java.util.regex.Pattern
import kotlin.streams.toList

@ViewBuilder
fun Parent.markdown(
    source: ObservableValue<String>
): Node {
    return textflow {
        contentAsMarkdown(source)
    }
}

private val patterns = listOf(
    Pattern.compile("([*_])(.*?)\\1"), // italics
    Pattern.compile("(\\*\\*|__)(.*?)\\1"), // bold
    Pattern.compile("\\[([^\\[]+)]\\(([^)]+)\\)") // links
)

fun Parent.contentAsMarkdown(source: ObservableValue<String>) {
    dynamicContent(source) {
        val raw = it.orEmpty()
        var last = 0
        patterns[2].matcher(raw).results().toList()
            .forEach {
                raw.substring(last, it.start()).let { if (it.isNotEmpty()) text(it) }
//                repeat(it.groupCount()) { index ->
//                    println("group $index in link: ${it.group(index)}")
//                }
                val text = it.group(1)
                if (text.length > 1) {
                    hyperlink(text)
                }
                last = it.end()
            }
        raw.substring(last, raw.length).let { if (it.isNotEmpty()) text(it) }
    }
}
