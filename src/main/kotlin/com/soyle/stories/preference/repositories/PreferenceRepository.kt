package com.soyle.stories.preference.repositories

import com.soyle.stories.entities.UserPreferences

interface PreferenceRepository {

	suspend fun getPreferences(preferencesId: UserPreferences.Id): UserPreferences?

}