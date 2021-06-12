package com.soyle.stories.location.renameLocation

import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedReceiver
import com.soyle.stories.usecase.location.renameLocation.RenameLocation

class RenameLocationOutput(
    private val locationRenamedReceiver: LocationRenamedReceiver,
    private val mentionTextReplacedReceiver: MentionTextReplacedReceiver,
    private val sceneSettingLocationRenamedReceiver: SceneSettingLocationRenamedReceiver
) : RenameLocation.OutputPort {

    override suspend fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
        locationRenamedReceiver.receiveLocationRenamed(response.locationRenamed)
        response.mentionTextReplaced.forEach {
            mentionTextReplacedReceiver.receiveMentionTextReplaced(it)
        }
        sceneSettingLocationRenamedReceiver.receiveSceneSettingLocaitonsRenamed(response.sceneSettingLocationsRenamed)
    }
}
