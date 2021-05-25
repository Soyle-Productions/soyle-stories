package com.soyle.stories.prose.editProse

import com.soyle.stories.domain.prose.events.EntityMentionedInProse
import com.soyle.stories.domain.prose.events.ProseCreated
import com.soyle.stories.domain.prose.events.TextInsertedIntoProse
import com.soyle.stories.prose.entityMentionedInProse.EntityMentionedInProseReceiver
import com.soyle.stories.prose.proseCreated.ProseCreatedReceiver
import com.soyle.stories.prose.textInsertedIntoProse.TextInsertedIntoProseReceiver
import com.soyle.stories.usecase.prose.bulkUpdateProse.BulkUpdateProse

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