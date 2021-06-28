package com.soyle.stories.domain

import org.junit.jupiter.api.Assertions

infix fun <T : Any?> T.shouldBe(assertion: (T) -> Unit) = assertion(this)
fun Any?.mustEqual(expected: Any?, message: () -> String = { "" }) =
    Assertions.assertEquals(expected, this, message)