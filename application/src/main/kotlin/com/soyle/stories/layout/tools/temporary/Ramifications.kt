package com.soyle.stories.layout.tools.temporary

import com.soyle.stories.entities.Scene
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.SceneDoesNotExist
import java.util.*

sealed class Ramifications : TemporaryTool() {
	class DeleteSceneRamifications(val sceneId: UUID, private val locale: Locale) : Ramifications() {
		override suspend fun validate(context: OpenToolContext) {
			context.sceneRepository.getSceneById(Scene.Id(sceneId))
			  ?: throw SceneDoesNotExist(locale, sceneId)
		}

		override fun identifiedWithId(id: UUID): Boolean =
		  id == sceneId
	}
}