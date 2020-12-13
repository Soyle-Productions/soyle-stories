package com.soyle.stories.desktop.config.features

private const val symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.?!@#$%^&*()`~[]{}\\|;:\"'<>/-=_+"

fun getParagraphs(count: Int): List<String> = sequence {
    repeat(count) {
        yield(List((10..300).random()) { symbols.random() }.joinToString(""))
    }
}.toList()