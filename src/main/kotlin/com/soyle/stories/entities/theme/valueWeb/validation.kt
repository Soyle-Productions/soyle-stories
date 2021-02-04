package com.soyle.stories.entities.theme.valueWeb

import com.soyle.stories.common.ValidationException


object ValueWebNameCannotBeBlank : ValidationException()

fun validateValueWebName(name: String) {
    if (name.isBlank()) throw ValueWebNameCannotBeBlank
}