package com.soyle.stories.domain.theme

import com.soyle.stories.domain.str

fun symbolName() = "Symbol ${str()}"

fun makeSymbol(
    id: Symbol.Id = Symbol.Id(),
    name: String = symbolName()
) = Symbol(
    id, name
)