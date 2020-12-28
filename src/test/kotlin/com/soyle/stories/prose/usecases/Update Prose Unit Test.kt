package com.soyle.stories.prose.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.*
import com.soyle.stories.doubles.ProseRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.prose.ContentReplaced
import com.soyle.stories.prose.ProseDoesNotExist
import com.soyle.stories.prose.ProseEvent
import com.soyle.stories.prose.makeProse
import com.soyle.stories.prose.usecases.updateProse.UpdateProse
import com.soyle.stories.prose.usecases.updateProse.UpdateProseUseCase
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
            content.mustEqual("Bob can be annoying.  But listen to Frank and he'll tell you that Alexis is worse.")
            mentions.mustEqual(
                listOf(
                    ProseMention(EntityId.of(bob), ProseMentionRange(0, 3)),
                    ProseMention(EntityId.of(frank), ProseMentionRange(36, 5)),
                    ProseMention(EntityId.of(alexis), ProseMentionRange(66, 6))
                )
            )
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
            event.newContent.mustEqual(updatedProse!!.content)
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
            newContent.mustEqual(updatedProse!!.content)
            newMentions.mustEqual(updatedProse!!.mentions)
        }

    }

    private fun mentionOf(character: Character): Pair<EntityId<*>, SingleLine> {
        return EntityId.of(character) to singleLine(character.name.toString())
    }

    private infix fun String.followedBy(mention: Pair<EntityId<*>, SingleLine>?): ProseContent {
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