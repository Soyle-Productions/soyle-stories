package com.soyle.stories.prose.editProse

import com.soyle.stories.prose.EntityMentionedInProse
import com.soyle.stories.prose.ProseCreated
import com.soyle.stories.prose.TextInsertedIntoProse
import com.soyle.stories.prose.entityMentionedInProse.EntityMentionedInProseReceiver
import com.soyle.stories.prose.proseCreated.ProseCreatedReceiver
import com.soyle.stories.prose.textInsertedIntoProse.TextInsertedIntoProseReceiver
import com.soyle.stories.prose.usecases.bulkUpdateProse.BulkUpdateProse

class BulkUpdateProseOutput(
    private val proseCreatedReceiver: ProseCreatedReceiver,
    private val textInsertedIntoProseReceiver: TextInsertedIntoProseReceiver,
    private val entityMentionedInProseReceiver: EntityMentionedInProseReceiver
) : BulkUpdateProse.OutputPort {

    override suspend fun receiveBulkUpdateResponse(response: BulkUpdateProse.ResponseModel) {
        response.events.forEach {
            when (it) {
                is ProseCreated -> proseCreatedReceiver.receiveProseCreated(it)
                is TextInsertedIntoProse -> textInsertedIntoProseReceiver.receiveTextInsertedIntoProse(it)
                is EntityMentionedInProse -> entityMentionedInProseReceiver.receiveEntityMentionedInProse(it)
            }
        }
    }

}