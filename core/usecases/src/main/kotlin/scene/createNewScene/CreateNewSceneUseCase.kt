package com.soyle.stories.usecase.scene.createNewScene

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import kotlinx.coroutines.Job
import java.util.*

class CreateNewSceneUseCase(
	projectId: UUID,
	private val sceneRepository: SceneRepository,
	private val storyEventRepository: StoryEventRepository,
	private val proseRepository: ProseRepository,
	private val createStoryEvent: CreateStoryEvent
) : CreateNewScene {

	private val projectId = Project.Id(projectId)

	override suspend fun invoke(request: CreateNewScene.RequestModel, output: CreateNewScene.OutputPort) {
		val (response, createdStoryEventResponse) = try { execute(request) }
		catch (e: Exception) { return outputException(output, e) }
		output.receiveCreateNewSceneResponse(response)
		if (createdStoryEventResponse != null) {
			output.createStoryEventOutputPort.receiveCreateStoryEventResponse(createdStoryEventResponse)
		}
	}

	private suspend fun execute(request: CreateNewScene.RequestModel): Pair<CreateNewScene.ResponseModel, CreateStoryEvent.ResponseModel?> {
		val (storyEvent, createdStoryEventResponse) = getOrCreateStoryEvent(request)
		val response = createNewScene(storyEvent, request)
		return response to createdStoryEventResponse
	}

	private suspend fun getOrCreateStoryEvent(request: CreateNewScene.RequestModel): Pair<StoryEvent, CreateStoryEvent.ResponseModel?> {
		return if (request.storyEventId != null) getStoryEvent(request.storyEventId) to null
		else createStoryEvent(request)
	}

	private suspend fun createStoryEvent(request: CreateNewScene.RequestModel): Pair<StoryEvent, CreateStoryEvent.ResponseModel>
	{
		val createStoryEventRequest = makeCreateStoryEventRequest(request)
		val response = callCreateStoryEventUseCase(createStoryEventRequest)
		return getStoryEvent(response.createdStoryEvent.storyEventId.uuid) to response
	}

	private suspend fun makeCreateStoryEventRequest(request: CreateNewScene.RequestModel): CreateStoryEvent.RequestModel
	{
		return when {
			request.relativeToScene != null -> {
				val relativeStoryEvent = getRelativeStoryEvent(request)
				when(request.relativeToScene.second) {
					true -> CreateStoryEvent.RequestModel(request.name, Project.Id(), relativeStoryEvent, -1)
					false -> CreateStoryEvent.RequestModel(request.name, Project.Id(), relativeStoryEvent, +1)
				}
			}
			else -> CreateStoryEvent.RequestModel(request.name, projectId)
		}
	}

	private suspend fun getRelativeStoryEvent(request: CreateNewScene.RequestModel): StoryEvent.Id
	{
		val relativeScene = sceneRepository.getSceneById(Scene.Id(request.relativeToScene!!.first))
		  ?: throw SceneDoesNotExist(request.locale, request.relativeToScene.first)
		val relativeStoryEvent = getStoryEvent(relativeScene.storyEventId.uuid)
		return relativeStoryEvent.id
	}

	private suspend fun callCreateStoryEventUseCase(request: CreateStoryEvent.RequestModel): CreateStoryEvent.ResponseModel
	{
		val job = Job()
		var createStoryEventResponse: CreateStoryEvent.ResponseModel? = null
		createStoryEvent.invoke(
		  request,
		  object : CreateStoryEvent.OutputPort {

			  override fun receiveCreateStoryEventResponse(response: CreateStoryEvent.ResponseModel) {
				  createStoryEventResponse = response
				  job.complete()
			  }
		  }
		)
		job.join()
		return createStoryEventResponse!!
	}

	private fun outputException(output: CreateNewScene.OutputPort, exception: Exception) {
		output.receiveCreateNewSceneFailure(exception)
	}

	private suspend fun createNewScene(storyEvent: StoryEvent, request: CreateNewScene.RequestModel): CreateNewScene.ResponseModel {
		val (prose, _) = Prose.create(projectId)
		proseRepository.addProse(prose)
		val scene = Scene(projectId, request.name, storyEvent.id, prose.id)
		return insertScene(scene, request)
	}

	private suspend fun insertScene(scene: Scene, request: CreateNewScene.RequestModel): CreateNewScene.ResponseModel
	{
		val idOrder = sceneRepository.getSceneIdsInOrder(projectId)
		val index = getInsertionIndex(idOrder, request)
		val affectedScenes = insertSceneAt(idOrder, scene, index)
		return CreateNewScene.ResponseModel(scene.id.uuid, scene.proseId, request.name.value, index, affectedScenes)
	}

	private fun getInsertionIndex(idOrder: List<Scene.Id>, request: CreateNewScene.RequestModel): Int
	{
		return if (request.relativeToScene != null) {
			val relativeIndex = idOrder.indexOf(Scene.Id(request.relativeToScene.first))
			if (relativeIndex == -1) throw Error("Repository does not contain index of Scene ${request.relativeToScene.first}")
			val insertIndex = relativeIndex + if (request.relativeToScene.second) 0 else 1
			insertIndex
		} else {
			idOrder.size
		}
	}

	private suspend fun insertSceneAt(idOrder: List<Scene.Id>, scene: Scene, index: Int): List<SceneItem>
	{
		sceneRepository.createNewScene(scene, idOrder.toMutableList().apply { add(index, scene.id) })
		return if (index < idOrder.size) {
			val affectedIds = idOrder.asSequence().withIndex().filter { it.index >= index }.associate { it.value to it.index }
			sceneRepository.listAllScenesInProject(projectId).filter { it.id in affectedIds }.map {
				SceneItem(it.id.uuid,it.proseId, it.name.value, affectedIds.getValue(it.id) + 1)
			}
		} else listOf()
	}

	private suspend fun getStoryEvent(storyEventId: UUID) =
	  (storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId))
		?: throw StoryEventDoesNotExist(storyEventId))

}