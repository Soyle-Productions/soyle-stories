package com.soyle.stories.prose

import com.soyle.stories.common.EntityId
import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.singleLine
import com.soyle.stories.entities.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProseUnitTest {

    private val proseId = Prose.Id()

    @Nested
    inner class `Create new Prose` {

        @Test
        fun `should create new prose and paragraph`() {
            val (newProse, event) = Prose.create()
            event.paragraphCreated.proseId.mustEqual(newProse.id)
        }

    }

    @Nested
    inner class `Create Additional Paragraph` {

        val prose = makeProse(proseId, paragraphs = List(5) { makeProseParagraph(proseId = proseId) })

        @Test
        fun `should create new paragraph with supplied text`() {
            val (newProse, _) = prose.withNewParagraphInserted(singleLine("banana"), 0)
            newProse.paragraphs
                .onEach { it.proseId.mustEqual(proseId) }
                .size.mustEqual(6)
            newProse.paragraphs[0].content.mustEqual(singleLine("banana"))
        }

        @Test
        fun `should update version id`() {
            val (newProse, _) = prose.withNewParagraphInserted(singleLine("banana"), 0)
            newProse.revision.mustEqual(prose.revision + 1L)
        }

        private fun assertUpdatedToMatchOrder(
            proseUpdate: ProseUpdate<ParagraphOrderChanged>,
            expectedOrder: List<ProseParagraph.Id>
        ) {
            proseUpdate.prose.paragraphOrder.mustEqual(expectedOrder) { "Prose has wrong paragraph order" }
            proseUpdate.event.newOrder.mustEqual(expectedOrder) { "Paragraph Order Changed event has wrong order" }
        }

        @Nested
        inner class `Add to End` {

            @Test
            fun `should update paragraph order with paragraph id at end`() {
                val proseUpdate = prose.withNewParagraphInserted(singleLine("banana"), 5)
                val expectedOrder = prose.paragraphOrder + proseUpdate.event.paragraphCreated!!.paragraphId
                assertUpdatedToMatchOrder(proseUpdate, expectedOrder)
            }

        }

        @Nested
        inner class `Add to Beginning` {

            @Test
            fun `should update paragraph order with paragraph id at beginning and all other following`() {
                val proseUpdate = prose.withNewParagraphInserted(singleLine("banana"), 0)
                val paragraphCreated = proseUpdate.event.paragraphCreated!!
                val expectedOrder = listOf(paragraphCreated.paragraphId) + prose.paragraphOrder
                assertUpdatedToMatchOrder(proseUpdate, expectedOrder)
            }

        }

        @Nested
        inner class `Add to Middle` {

            @Test
            fun `only paragraphs after the insertion index should be moved`() {
                val proseUpdate = prose.withNewParagraphInserted(singleLine("banana"), 2)
                val expectedOrder =
                    prose.paragraphOrder.take(2) + proseUpdate.event.paragraphCreated!!.paragraphId + prose.paragraphOrder.takeLast(
                        3
                    )
                assertUpdatedToMatchOrder(proseUpdate, expectedOrder)
            }

        }

    }

    @Nested
    inner class `Insert Text` {

        val characterId = EntityId.of(Character::class).id(Character.Id())
        val paragraph = makeProseParagraph(proseId = proseId, content = singleLine("banana"))
        val prose = makeProse(proseId, paragraphs = List(1) { paragraph })

        @Test
        fun `should update version id`() {
            val proseUpdate = prose.withTextInserted(singleLine("some "), paragraph.id, 0)
            proseUpdate.prose.revision.mustEqual(prose.revision + 1L)
        }

        @Test
        fun `paragraph order should not be updated`() {
            val proseUpdate = prose.withTextInserted(singleLine("some "), paragraph.id, 0)
            proseUpdate.prose.paragraphOrder.mustEqual(prose.paragraphOrder)
        }

        @Test
        fun `cannot bisect a mention`() {
            val proseUpdate = prose.withEntityMentioned(paragraph.id, characterId, 2, 3)
            assertThrows<ProseMentionCannotBeBisected> {
                proseUpdate.prose.withTextInserted(singleLine("fail"), paragraph.id, 3)
                proseUpdate.prose.withTextInserted(singleLine("fail"), paragraph.id, 4)
            }
            proseUpdate.prose.withTextInserted(singleLine("succeed"), paragraph.id, 2)
            proseUpdate.prose.withTextInserted(singleLine("succeed"), paragraph.id, 5)

        }

        @Nested
        inner class `Append to End` {

            @Test
            fun `should update paragraph text with text at end`() {
                val proseUpdate = prose.withTextInserted(singleLine("s"), paragraph.id, 6)
                proseUpdate.prose.paragraphs[0].content.toString().mustEqual("bananas")
            }

        }

        @Nested
        inner class `Prepend to Beginning` {

            @Test
            fun `should update paragraph text with text at beginning`() {
                val proseUpdate = prose.withTextInserted(singleLine("a "), paragraph.id, 0)
                proseUpdate.prose.paragraphs[0].content.toString().mustEqual("a banana")
            }

        }

        @Nested
        inner class `Insert Between Text` {

            @Test
            fun `should update paragraph text with text in the middle`() {
                val proseUpdate = prose.withTextInserted(singleLine("ned M"), paragraph.id, 3)
                proseUpdate.prose.paragraphs[0].content.toString().mustEqual("banned Mana")
            }

        }

        @Nested
        inner class `Insert Before Mention` {

            private val updatedProse: Prose
            private val paragraphWithMention: ProseParagraph

            init {
                prose.withEntityMentioned(paragraph.id, characterId, 2, 3).run {
                    updatedProse = prose
                    paragraphWithMention = prose.paragraphs[0]
                }
            }

            @Test
            fun `should update mention position`() {
                val proseUpdate = updatedProse.withTextInserted(
                    singleLine("one "),
                    paragraphWithMention.id,
                    0
                )
                proseUpdate.prose.paragraphs[0].allMentions.single().mustEqual(
                    ProseMention(characterId, ProseMentionPosition(paragraph.id, 6, 3))
                )
            }

        }

    }

    @Nested
    inner class `Mention Entity` {

        val characterId = EntityId.of(Character::class).id(Character.Id())
        private val paragraph = makeProseParagraph(proseId = proseId, content = singleLine("banana"))
        private val prose = makeProse(proseId, paragraphs = List(1) { paragraph })

        @Test
        fun `mentions must be in range`() {
            assertThrows<IndexOutOfBoundsException> {
                prose.withEntityMentioned(paragraph.id, characterId, -1, 1)
                prose.withEntityMentioned(paragraph.id, characterId, -1, 4)
                prose.withEntityMentioned(paragraph.id, characterId, -1, 8)
                prose.withEntityMentioned(paragraph.id, characterId, 0, 7)
                prose.withEntityMentioned(paragraph.id, characterId, 6, 1)
            }
            prose.withEntityMentioned(paragraph.id, characterId, 0, 6)
        }

        @Test
        fun `mentions cannot overlap`() {
            val proseUpdate = prose.withEntityMentioned(paragraph.id, characterId, 2, 3)
            assertThrows<MentionOverlapsExistingMention> {
                proseUpdate.prose.withEntityMentioned(paragraph.id, characterId, 1, 2)
                proseUpdate.prose.withEntityMentioned(paragraph.id, characterId, 1, 5)
                proseUpdate.prose.withEntityMentioned(paragraph.id, characterId, 3, 1)
                proseUpdate.prose.withEntityMentioned(paragraph.id, characterId, 4, 2)
            }
            proseUpdate.prose.withEntityMentioned(paragraph.id, characterId, 0, 2)
            proseUpdate.prose.withEntityMentioned(paragraph.id, characterId, 5, 1)
        }

        @Test
        fun `should update prose version`() {
            val proseUpdate = prose.withEntityMentioned(paragraph.id, characterId, 2, 3)
            proseUpdate.prose.revision.mustEqual(prose.revision + 1L)
        }

        @Test
        fun `mention should be included at requested position`() {
            val proseUpdate = prose.withEntityMentioned(paragraph.id, characterId, 2, 3)
            proseUpdate.prose.paragraphs[0].allMentions.toList().mustEqual(
                listOf(
                    ProseMention(characterId, ProseMentionPosition(paragraph.id, 2, 3))
                )
            )
        }

    }
}