package com.soyle.stories.desktop.config.features

private const val symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.`[]\\;'/-="

fun getParagraphs(count: Int, lengthLimit: Int = 300): List<String> = sequence {
    repeat(count) {
        yield(List((10..lengthLimit).random()) { symbols.random() }.joinToString(""))
    }
}.toList()