package com.soyle.stories.prose

import com.soyle.stories.common.EntityId
import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseMentionPosition
import com.soyle.stories.entities.ProseParagraph

sealed class ProseEvent {
    abstract val proseId: Prose.Id
}

class ProseCreated(override val proseId: Prose.Id, val paragraphCreated: ParagraphCreated) : ProseEvent()

data class ProseParagraphTextModified(override val proseId: Prose.Id, val newText: String, val mentionsMoved: List<ProseMentionMoved>) : ProseEvent()
data class ParagraphCreated(override val proseId: Prose.Id, val paragraphId: ProseParagraph.Id, val content: String) : ProseEvent()
data class ParagraphOrderChanged(override val proseId: Prose.Id, val newOrder: List<ProseParagraph.Id>, val paragraphCreated: ParagraphCreated?): ProseEvent()
data class ProseMentionAdded(override val proseId: Prose.Id, val entityId: EntityId<*>, val position: ProseMentionPosition) : ProseEvent()
data class ProseMentionMoved(override val proseId: Prose.Id, val entityId: EntityId<*>, val position: ProseMentionPosition) : ProseEvent()
data class ProseMentionRemoved(override val proseId: Prose.Id, val entityId: EntityId<*>, val position: ProseMentionPosition) : ProseEvent()