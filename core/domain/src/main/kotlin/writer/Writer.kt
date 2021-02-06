package com.soyle.stories.domain.writer

import com.soyle.stories.domain.entities.Entity
import java.util.*

class Writer(
  override val id: Id,
  val preferences: Preferences
) : Entity<Writer.Id> {

	constructor() : this(Id(), Preferences())

	private fun copy(
	  preferences: Preferences = this.preferences
	) = Writer(id, preferences)

	fun withPreferenceFor(preferenceKey: String, value: Any?) = copy(preferences = preferences.withPreference(preferenceKey, value))

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Writer

		if (id != other.id) return false
		if (preferences != other.preferences) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + preferences.hashCode()
		return result
	}

	override fun toString(): String {
		return "Writer(id=$id, preferences=$preferences)"
	}


	data class Id(val uuid: UUID = UUID.randomUUID()) {
		override fun toString(): String = "Writer($uuid)"
	}

}