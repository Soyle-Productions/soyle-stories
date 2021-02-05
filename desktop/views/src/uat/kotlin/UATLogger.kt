package com.soyle.stories

object UATLogger {
	private val props = object : ThreadLocal<Boolean>() {
		override fun initialValue(): Boolean = true
	}

	var silent: Boolean
		get() = props.get()
		set(value) {
			props.set(value)
		}

	fun log(message: Any?) {
		if (! silent) println(message)
	}

	fun enableLogging(block: () -> Unit) {
		silent = false
		block()
		silent = true
	}
}