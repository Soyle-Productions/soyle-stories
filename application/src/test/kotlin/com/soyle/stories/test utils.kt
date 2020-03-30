package com.soyle.stories

import org.junit.jupiter.api.Assertions.assertEquals

inline fun Any?.mustEqual(expected: Any?, crossinline message: () -> String = {""}) = assertEquals(expected, this) { message() }