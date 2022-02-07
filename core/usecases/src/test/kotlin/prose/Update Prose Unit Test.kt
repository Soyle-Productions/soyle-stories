package com.soyle.stories.usecase.prose

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.prose.*
import com.soyle.stories.domain.prose.events.ContentReplaced
import com.soyle.stories.domain.prose.events.ProseEvent
import com.soyle.stories.domain.singleLine
import com.soyle.stories.domain.validation.SingleLine
import com.soyle.stories.usecase.prose.updateProse.UpdateProse
import com.soyle.stories.usecase.prose.updateProse.UpdateProseUseCase
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Update Prose Unit Test` {

    private val prose = makeProse()
    private var updatedProse: Prose? = null
    private var addedProseEvents = mutableMapOf<Prose.Id, List<ProseEvent>>()

    private var result: UpdateProse.ResponseModel? = null

    private val proseRepository = ProseRepositoryDouble(
        onReplaceProse = ::updatedProse::set,
        onAddEvents = addedProseEvents::put
    )

    @Test
    fun `prose must exist`() {
        val error = assertThrows<ProseDoesNotExist> {
            updateProse()
        }
        error.proseId.mustEqual(prose.id)
    }

    @Test
    fun `should update prose in repository`() {
        proseRepository.givenProse(prose)
        val bob = makeCharacter(name = nonBlankStr("Bob"))
        val frank = makeCharacter(name = nonBlankStr("Frank"))
        val alexis = makeCharacter(name = nonBlankStr("Alexis"))

        updateProse(
            "" followedBy mentionOf(bob),
            " can be annoying.  But listen to " followedBy mentionOf(frank),
            " and he'll tell you that " followedBy mentionOf(alexis),
            " is worse." followedBy null
        )
        with(updatedProse!!) {
            id.mustEqual(prose.id)
            revision.mustEqual(prose.revision + 1)
            text.mustEqual("Bob can be annoying.  But listen to Frank and he'll tell you that Alexis is worse.")
            mentions.map { it.entityId }.mustEqual(listOf(bob.id.mentioned(), frank.id.mentioned(), alexis.id.mentioned()))
            mentions.map { it.startIndex }.mustEqual(listOf(0, 36, 66))
            mentions.map { it.endIndex }.mustEqual(listOf(3, 41, 72))
        }
    }

    @Test
    fun `should add prose event to repository`() {
        proseRepository.givenProse(prose)

        updateProse()
        with(addedProseEvents.getValue(prose.id)) {
            val event = single() as ContentReplaced
            event.proseId.mustEqual(prose.id)
            event.revision.mustEqual(prose.revision + 1)
            event.newContent.mustEqual(updatedProse!!.text)
            event.newMentions.mustEqual(updatedProse!!.mentions)
        }
    }

    @Test
    fun `should output event`() {
        proseRepository.givenProse(prose)
        val bob = makeCharacter(name = nonBlankStr("Bob"))
        val frank = makeCharacter(name = nonBlankStr("Frank"))
        val alexis = makeCharacter(name = nonBlankStr("Alexis"))

        updateProse(
            "" followedBy mentionOf(bob),
            " can be annoying.  But listen to " followedBy mentionOf(frank),
            " and he'll tell you that " followedBy mentionOf(alexis),
            " is worse." followedBy null
        )
        with(result!!.contentReplaced) {
            proseId.mustEqual(prose.id)
            revision.mustEqual(prose.revision + 1)
            newContent.mustEqual(updatedProse!!.text)
            newMentions.mustEqual(updatedProse!!.mentions)
        }

    }

    private fun mentionOf(character: Character): Pair<MentionedEntityId<*>, SingleLine> {
        return character.id.mentioned() to singleLine(character.displayName.toString())
    }

    private infix fun String.followedBy(mention: Pair<MentionedEntityId<*>, SingleLine>?): ProseContent {
        return ProseContent(this, mention)
    }

    private fun updateProse(vararg content: ProseContent) {
        val useCase: UpdateProse = UpdateProseUseCase(proseRepository)
        val output = object : UpdateProse.OutputPort {
            override suspend fun invoke(response: UpdateProse.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(prose.id, content.toList(), output)
        }
    }

}