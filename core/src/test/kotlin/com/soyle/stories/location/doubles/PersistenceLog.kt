package com.soyle.stories.location.doubles

data class PersistenceLog(val type: String, val data: Any) {
	override fun toString(): String {
		return "$type -> $data)"
	}
}