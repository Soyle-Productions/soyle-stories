package com.soyle.stories.prose.usecases.bulkUpdateProse

import com.soyle.stories.common.EntityId
import com.soyle.stories.prose.EntityMentionedInProse
import com.soyle.stories.prose.ProseCreated
import com.soyle.stories.prose.ProseEvent
import com.soyle.stories.prose.TextInsertedIntoProse

sealed class Operation {
    abstract fun transform(event: ProseEvent): Operation?
}

class InsertText(val text: String, val index: Int) : Operation() {
    override fun transform(event: ProseEvent): Operation? {
        return when (event) {
            is ProseCreated -> this
            is TextInsertedIntoProse -> if (event.index <= index) {
                InsertText(text, index + event.insertedText.length)
            } else this
            is EntityMentionedInProse -> if (event.position.isBisectedBy(index)) {
                null
            } else this
        }
    }
}
class MentionEntity(val entityId: EntityId<*>, val index: Int, val length: Int) : Operation() {
    override fun transform(event: ProseEvent): Operation? {
        return when (event) {
            is ProseCreated -> this
            is TextInsertedIntoProse -> if (event.index <= index) {
                MentionEntity(entityId, index + event.insertedText.length, length)
            } else this
            is EntityMentionedInProse -> if (event.position.isBisectedBy(index) || event.position.isBisectedBy(index + length)) {
                null
            } else this
        }
    }
}