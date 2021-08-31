package com.soyle.stories.storyevent

import javafx.scene.control.SpinnerValueFactory
import javafx.util.StringConverter

class NullableLongSpinnerValueFactory : SpinnerValueFactory<Long?>() {
    override fun increment(steps: Int) {
        value = value?.plus(steps)
    }

    override fun decrement(steps: Int) = increment(-steps)

    init {
        value = null
        converter = object : StringConverter<Long?>() {
            override fun toString(`object`: Long?): String = `object`?.toString() ?: ""
            override fun fromString(string: String?): Long? = string?.toLongOrNull()
        }
    }
}