package com.soyle.stories.domain.writer

data class Preferences internal constructor(internal val values: Map<String, Any>) {

	constructor() : this(emptyMap())

	operator fun get(preference: String): Any? = values[preference]

	internal fun withPreference(key: String, value: Any?): Preferences {
		return if (value == null) {
			if (values.containsKey(key)) Preferences(values - key)
			else this
		} else Preferences(values + (key to value))
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Preferences

		if (values != other.values) return false

		return true
	}

	override fun hashCode(): Int {
		return values.hashCode()
	}

	override fun toString(): String {
		return "Preferences($values)"
	}


}