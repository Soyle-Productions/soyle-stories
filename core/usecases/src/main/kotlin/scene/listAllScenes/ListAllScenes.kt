package com.soyle.stories.usecase.scene.listAllScenes

interface ListAllScenes {

	suspend operator fun invoke(output: OutputPort)

	class ResponseModel(val scenes: List<SceneItem>)

	fun interface OutputPort
	{
		suspend fun receiveListAllScenesResponse(response: ResponseModel)
	}

}