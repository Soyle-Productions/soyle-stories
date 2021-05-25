package com.soyle.stories.desktop.config.drivers.prose

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.prose.*
import com.soyle.stories.domain.prose.content.ProseContent
import com.soyle.stories.domain.validation.SingleLine
import com.soyle.stories.domain.validation.countLines
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.prose.editProse.EditProseController
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.prose.proseEditor.ProseEditorState
import com.soyle.stories.usecase.prose.ProseDoesNotExist
import com.soyle.stories.usecase.prose.ProseRepository
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
        val newMention = object : ProseContent.Mention<Any> {
            override val entityId: MentionedEntityId<Any> = entityId as MentionedEntityId<Any>
            override val startIndex: Int = index
            override val endIndex: Int = index + length
            override val text: SingleLine = countLines(prose.text.substring(startIndex, endIndex)) as SingleLine
        }
        val content = (prose.mentions + newMention).map { mention ->
            ProseContent(
                prose.text.substring(lastMentionEnd, mention.startIndex),
                mention.entityId to (countLines(prose.text.substring(mention.startIndex, mention.endIndex)) as SingleLine)
            ).also {
                lastMentionEnd = mention.endIndex
            }
        } + ProseContent(prose.text.substring(lastMentionEnd), null)
        controller.updateProse(prose.id, content)
    }
    fun givenProseMentionsEntity(prose: Prose, entityId: MentionedEntityId<*>, name: String) {
        val scope = ProseEditorScope(projectScope, prose.id, { _, _ -> }, {}) { _, _ -> }.apply {
            get<ProseEditorState>().versionNumber.set(prose.revision)
        }
        val controller = scope.get<EditProseController>()
        val appendedText = prose.text + name

        val newMention = object : ProseContent.Mention<Any> {
            override val entityId: MentionedEntityId<Any> = entityId as MentionedEntityId<Any>
            override val startIndex: Int = prose.text.length
            override val endIndex: Int = prose.text.length + name.length
            override val text: SingleLine = countLines(name) as SingleLine
        }
        var lastMentionEnd = 0
        val content = (prose.mentions + newMention).map { mention ->
            ProseContent(
                appendedText.substring(lastMentionEnd, mention.startIndex),
                mention.entityId to (countLines(appendedText.substring(mention.startIndex, mention.endIndex)) as SingleLine)
            ).also {
                lastMentionEnd = mention.endIndex
            }
        } + ProseContent(appendedText.substring(lastMentionEnd), null)
        controller.updateProse(prose.id, content)
    }

    fun givenProseDoesNotMention(prose: Prose, mentionText: String)
    {
        if (prose.mentions.any { prose.text.substring(it.startIndex, it.endIndex) == mentionText}) {
            val scope = ProseEditorScope(projectScope, prose.id, { _, _ -> }, {}) { _, _ -> }.apply {
                get<ProseEditorState>().versionNumber.set(prose.revision)
            }
            val controller = scope.get<EditProseController>()
            controller.updateProse(prose.id, listOf())
        }
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { ProseDriver(this) } }
        }

        operator fun invoke(workbench: WorkBench): ProseDriver = workbench.scope.get()
    }

}

fun <ID : Any> Prose.getMentionByEntityIdOrError(id: ID): ProseContent.Mention<ID>
{
    val mention =  mentions
        .find { it.entityId.id == id }
        ?: throw AssertionError("No mention in prose with id $id")
    return mention as ProseContent.Mention<ID>
}

fun Prose.getMentionByText(text: String): ProseContent.Mention<*>?
{
    return mentions.find { this.text.substring(it.startIndex, it.endIndex) == text }
}