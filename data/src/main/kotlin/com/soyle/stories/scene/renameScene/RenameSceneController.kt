package com.soyle.stories.scene.renameScene

import com.soyle.stories.common.NonBlankString

interface RenameSceneController {

	fun renameScene(sceneId: String, newName: NonBlankString)

}