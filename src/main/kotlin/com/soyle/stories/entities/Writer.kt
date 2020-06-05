package com.soyle.stories.entities

import com.soyle.stories.common.Entity
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

	data class Id(val uuid: UUID = UUID.randomUUID()) {
		override fun toString(): String = "Writer($uuid)"
	}

}