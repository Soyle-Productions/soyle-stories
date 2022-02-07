package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.usecase.scene.list.ListAllScenes
import com.soyle.stories.usecase.scene.list.ListAllScenesUseCase
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `List All Scenes Unit Test` {

	// Prerequisites
	private val projectId = Project.Id()

	// Post Requisites
	private var response: ListAllScenes.ListOfScenesInStory? = null

	private val sceneRepository = SceneRepositoryDouble()

	private fun listScenes() {
		val useCase: ListAllScenes = ListAllScenesUseCase(sceneRepository)
		runBlocking { useCase(projectId) { response = it } }
	}

	@Test
	fun `should output empty output`() {
		listScenes()

		response!!.project.mustEqual(projectId)
		response!!.isEmpty().mustEqual(true)
	}

	@Nested
	inner class `Given Scenes Exist in Project` {

		private val scenesInProject = List(4) { makeScene(projectId = projectId) }
			.onEach(sceneRepository::givenScene)
		private val otherScenes = List(5) { makeScene() }
			.onEach(sceneRepository::givenScene)

		@Test
		fun `should output all scenes in project`() {
			listScenes()

			response!!.project.mustEqual(projectId)
			response!!.size.mustEqual(4)
			response!!.map {it.scene}.toSet().mustEqual(scenesInProject.map{it.id}.toSet())
			response!!.forEach { sceneListItem ->
				val backingScene = scenesInProject.single{ it.id == sceneListItem.scene }
				sceneListItem.name.mustEqual(backingScene.name)
				sceneListItem.prose.mustEqual(backingScene.proseId)
			}
		}

		@Test
		fun `should output in scene order`() {
			val newOrder = scenesInProject.drop(1).shuffled() + scenesInProject.first()
			sceneRepository.sceneOrders[projectId] = SceneOrder.reInstantiate(projectId, newOrder.map{ it.id })

			listScenes()

			response!!.withIndex().forEach {
				it.value.scene.mustEqual(newOrder[it.index].id)
			}
		}

	}
}