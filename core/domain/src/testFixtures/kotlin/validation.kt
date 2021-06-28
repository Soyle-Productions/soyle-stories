package com.soyle.stories.domain

import com.soyle.stories.domain.validation.MultiLine
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.validation.SingleLine
import com.soyle.stories.domain.validation.countLines


fun str() = (Math.random() * Int.MAX_VALUE).toInt().toString(16).take(3)
fun nonBlankStr(value: String = str()) = NonBlankString.create(value)!!
fun singleLine(text: String) = countLines(text) as SingleLine
fun multiLine(text: String) = countLines(text) as MultiLine