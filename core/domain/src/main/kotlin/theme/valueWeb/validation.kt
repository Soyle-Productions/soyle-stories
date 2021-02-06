package com.soyle.stories.domain.theme.valueWeb

import com.soyle.stories.domain.validation.ValidationException


object ValueWebNameCannotBeBlank : ValidationException()

fun validateValueWebName(name: String) {
    if (name.isBlank()) throw ValueWebNameCannotBeBlank
}