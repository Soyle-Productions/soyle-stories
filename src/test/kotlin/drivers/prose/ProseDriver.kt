package com.soyle.stories.desktop.config.drivers.prose

import com.soyle.stories.common.SingleLine
import com.soyle.stories.common.countLines
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.*
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.prose.ProseDoesNotExist
import com.soyle.stories.prose.editProse.EditProseController
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.prose.proseEditor.ProseEditorState
import com.soyle.stories.prose.repositories.ProseRepository
import kotlinx.coroutines.runBlocking

class ProseDriver private constructor(private val projectScope: ProjectScope) {

    fun getProseByIdOrError(proseId: Prose.Id): Prose = getProseById(proseId) ?: throw ProseDoesNotExist(proseId)
    fun getProseById(proseId: Prose.Id): Prose? {
        val repo = projectScope.get<ProseRepository>()
        return runBlocking {
            repo.getProseById(proseId)
        }
    }

    fun givenProseMentionsEntity(prose: Prose, entityId: MentionedEntityId<*>, index: Int, length: Int) {
        val scope = ProseEditorScope(projectScope, prose.id, { _, _ -> }, {}) { _, _ -> }.apply {
            get<ProseEditorState>().versionNumber.set(prose.revision)
        }
        val controller = scope.get<EditProseController>()
        var lastMentionEnd = 0
        val content = (prose.mentions + ProseMention(entityId, ProseMentionRange(index, length))).map { mention ->
            ProseContent(
                prose.content.substring(lastMentionEnd, mention.start()),
                mention.entityId to (countLines(prose.content.substring(mention.start(), mention.end())) as SingleLine)
            ).also {
                lastMentionEnd = mention.end()
            }
        } + ProseContent(prose.content.substring(lastMentionEnd), null)
        controller.updateProse(prose.id, content)
    }
    fun givenProseMentionsEntity(prose: Prose, entityId: MentionedEntityId<*>, name: String) {
        val scope = ProseEditorScope(projectScope, prose.id, { _, _ -> }, {}) { _, _ -> }.apply {
            get<ProseEditorState>().versionNumber.set(prose.revision)
        }
        val controller = scope.get<EditProseController>()
        val appendedText = prose.content + name
        var lastMentionEnd = 0
        val content = (prose.mentions + ProseMention(entityId, ProseMentionRange(prose.content.length, name.length))).map { mention ->
            ProseContent(
                appendedText.substring(lastMentionEnd, mention.start()),
                mention.entityId to (countLines(appendedText.substring(mention.start(), mention.end())) as SingleLine)
            ).also {
                lastMentionEnd = mention.end()
            }
        } + ProseContent(appendedText.substring(lastMentionEnd), null)
        controller.updateProse(prose.id, content)
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { ProseDriver(this) } }
        }

        operator fun invoke(workbench: WorkBench): ProseDriver = workbench.scope.get()
    }

}

fun <ID : Any> Prose.getMentionByEntityIdOrError(id: ID): ProseMention<ID>
{
    val mention =  mentions
        .find { it.entityId.id == id }
        ?: throw AssertionError("No mention in prose with id $id")
    return mention as ProseMention<ID>
}

fun Prose.getMentionByText(text: String): ProseMention<*>?
{
    return mentions.find { content.substring(it.start(), it.end()) == text }
}