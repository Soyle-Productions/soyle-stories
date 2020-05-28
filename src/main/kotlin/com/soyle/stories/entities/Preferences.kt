package com.soyle.stories.entities

class Preferences internal constructor(internal val values: Map<String, Any>) {

	constructor() : this(emptyMap())

	operator fun get(preference: String): Any? = values[preference]

	internal fun withPreference(key: String, value: Any?): Preferences {
		return if (value == null) {
			if (values.containsKey(key)) Preferences(values - key)
			else this
		} else Preferences(values + (key to value))
	}

}