package com.soyle.stories.usecase.prose.bulkUpdateProse

import com.soyle.stories.domain.prose.*


sealed class Operation {
    abstract fun transform(event: ProseEvent): Operation?
}

class InsertText(val text: String, val index: Int) : Operation() {
    override fun transform(event: ProseEvent): Operation? {
        return when (event) {
            is ContentReplaced -> error("Content was replaced.  Cannot synchronize operations after this point.")
            is ProseCreated -> this
            is TextInsertedIntoProse -> if (event.index <= index) {
                InsertText(text, index + event.insertedText.length)
            } else this
            is EntityMentionedInProse -> if (event.position.isBisectedBy(index)) {
                null
            } else this
            is TextRemovedFromProse -> if (event.index <= index) {
                InsertText(text, index - event.deletedText.length)
            } else this
            is MentionRemovedFromProse -> this
            is MentionTextReplaced -> {/*
                val lengthAdjustment = event.insertedText.length - event.deletedText.length
                val numberOfUpdatesBeforeThisOperation = event.positionUpdates.filter { it.second < index }.size
                if (numberOfUpdatesBeforeThisOperation == 0) this
                else InsertText(text, index + (lengthAdjustment * numberOfUpdatesBeforeThisOperation))*/
                error("Operational Transformation no longer supported")
            }
        }
    }
}
class MentionEntity(val entityId: MentionedEntityId<*>, val index: Int, val length: Int) : Operation() {
    override fun transform(event: ProseEvent): Operation? {
        return when (event) {
            is ContentReplaced -> error("Content was replaced.  Cannot synchronize operations after this point.")
            is ProseCreated -> this
            is TextInsertedIntoProse -> if (event.index <= index) {
                MentionEntity(entityId, index + event.insertedText.length, length)
            } else this
            is EntityMentionedInProse -> if (event.position.isBisectedBy(index) || event.position.isBisectedBy(index + length)) {
                null
            } else this
            is TextRemovedFromProse -> if (event.index <= index) {
                MentionEntity(entityId, index - event.deletedText.length, length)
            } else this
            is MentionRemovedFromProse -> this
            is MentionTextReplaced -> {/*
                val lengthAdjustment = event.insertedText.length - event.deletedText.length
                val numberOfUpdatesBeforeThisOperation = event.positionUpdates.filter { it.second < index }.size
                if (numberOfUpdatesBeforeThisOperation == 0) this
                else MentionEntity(entityId, index - (lengthAdjustment * numberOfUpdatesBeforeThisOperation), length)*/
                error("Operational Transformation no longer supported")
            }
        }
    }
}