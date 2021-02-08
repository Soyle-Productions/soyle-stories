package com.soyle.stories.scene.renameScene

import com.soyle.stories.domain.validation.NonBlankString

interface RenameSceneController {

	fun renameScene(sceneId: String, newName: NonBlankString)

}