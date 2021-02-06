package com.soyle.stories.domain

import org.junit.jupiter.api.Assertions


fun Any?.mustEqual(expected: Any?, message: () -> String = { "" }) =
    Assertions.assertEquals(expected, this, message)