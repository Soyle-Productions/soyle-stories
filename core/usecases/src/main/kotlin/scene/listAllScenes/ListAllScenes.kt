package com.soyle.stories.usecase.scene.listAllScenes

interface ListAllScenes {

	suspend operator fun invoke(output: OutputPort)

	class ResponseModel(val scenes: List<SceneItem>)

	interface OutputPort
	{
		fun receiveListAllScenesResponse(response: ResponseModel)
	}

}