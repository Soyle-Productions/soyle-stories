package com.soyle.stories.domain

import com.soyle.stories.domain.validation.MultiLine
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.validation.SingleLine
import com.soyle.stories.domain.validation.countLines
import java.nio.charset.Charset
import kotlin.random.Random
import kotlin.text.Charsets.US_ASCII

private val strRand = Random((Math.random() * Int.MAX_VALUE).toInt())
fun str(): String {
    return strRand.nextInt().toString()
}
fun nonBlankStr(value: String = str()) = NonBlankString.create(value)!!
fun singleLine(text: String) = countLines(text) as SingleLine
fun multiLine(text: String) = countLines(text) as MultiLine
