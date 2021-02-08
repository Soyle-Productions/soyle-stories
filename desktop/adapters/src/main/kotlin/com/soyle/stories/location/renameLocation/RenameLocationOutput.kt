package com.soyle.stories.location.renameLocation

import com.soyle.stories.usecase.location.renameLocation.RenameLocation
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver

class RenameLocationOutput(
    private val locationRenamedReceiver: LocationRenamedReceiver,
    private val mentionTextReplacedReceiver: MentionTextReplacedReceiver
) : RenameLocation.OutputPort {

    override suspend fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
        locationRenamedReceiver.receiveLocationRenamed(response.locationRenamed)
        response.mentionTextReplaced.forEach {
            mentionTextReplacedReceiver.receiveMentionTextReplaced(it)
        }
    }

}