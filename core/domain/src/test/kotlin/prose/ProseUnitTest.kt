package com.soyle.stories.domain.prose

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.singleLine
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProseUnitTest {

    private val proseId = Prose.Id()

    fun entityId() = Character.Id().mentioned()

    @Nested
    inner class `Create new Prose` {

        @Test
        fun `content and mentions should be empty`() {
            val (newProse, _) = Prose.create(Project.Id())
            assertTrue(newProse.content.isEmpty())
            assertTrue(newProse.mentions.isEmpty())
            newProse.revision.mustEqual(0L)
        }

    }

    @Nested
    inner class `Insert Text` {

        val characterId = Character.Id().mentioned()
        val prose = makeProse(proseId, content = "banana")

        @Test
        fun `should update version id`() {
            val proseUpdate = prose.withTextInserted("some ", 0)
            proseUpdate.prose.revision.mustEqual(prose.revision + 1L)
        }

        @Test
        fun `cannot bisect a mention`() {
            val proseUpdate = prose.withEntityMentioned(characterId, 2, 3)
            assertThrows<ProseMentionCannotBeBisected> {
                proseUpdate.prose.withTextInserted("fail", 3)
                proseUpdate.prose.withTextInserted("fail", 4)
            }
            proseUpdate.prose.withTextInserted("succeed", 2)
            proseUpdate.prose.withTextInserted("succeed", 5)

        }

        @Nested
        inner class `Append to End` {

            @Test
            fun `should update paragraph text with text at end`() {
                val proseUpdate = prose.withTextInserted("s", 6)
                proseUpdate.prose.content.mustEqual("bananas")
            }

        }

        @Nested
        inner class `Prepend to Beginning` {

            @Test
            fun `should update paragraph text with text at beginning`() {
                val proseUpdate = prose.withTextInserted("a ", 0)
                proseUpdate.prose.content.mustEqual("a banana")
            }

        }

        @Nested
        inner class `Insert Between Text` {

            @Test
            fun `should update paragraph text with text in the middle`() {
                val proseUpdate = prose.withTextInserted("ned M", 3)
                proseUpdate.prose.content.mustEqual("banned Mana")
            }

        }

        @Nested
        inner class `Insert Before Mention` {

            private val proseWithMention: Prose = prose.withEntityMentioned(characterId, 2, 3).prose

            @Test
            fun `should update mention position`() {
                val proseUpdate = proseWithMention.withTextInserted(
                    "one ",
                    0
                )
                proseUpdate.prose.mentions.single().mustEqual(
                    ProseMention(characterId, ProseMentionRange(6, 3))
                )
            }

        }

    }

    @Nested
    inner class `Mention Entity` {

        val characterId = Character.Id().mentioned()
        private val prose = makeProse(proseId, content = "banana")

        @Test
        fun `mentions must be in range`() {
            assertThrows<IndexOutOfBoundsException> {
                prose.withEntityMentioned(characterId, -1, 1)
                prose.withEntityMentioned(characterId, -1, 4)
                prose.withEntityMentioned(characterId, -1, 8)
                prose.withEntityMentioned(characterId, 0, 7)
                prose.withEntityMentioned(characterId, 6, 1)
            }
            prose.withEntityMentioned(characterId, 0, 6)
        }

        @Test
        fun `mentions cannot overlap`() {
            val proseUpdate = prose.withEntityMentioned(characterId, 2, 3)
            assertThrows<MentionOverlapsExistingMention> {
                proseUpdate.prose.withEntityMentioned(characterId, 1, 2)
                proseUpdate.prose.withEntityMentioned(characterId, 1, 5)
                proseUpdate.prose.withEntityMentioned(characterId, 3, 1)
                proseUpdate.prose.withEntityMentioned(characterId, 4, 2)
            }
            proseUpdate.prose.withEntityMentioned(characterId, 0, 2)
            proseUpdate.prose.withEntityMentioned(characterId, 5, 1)
        }

        @Test
        fun `should update prose version`() {
            val proseUpdate = prose.withEntityMentioned(characterId, 2, 3)
            proseUpdate.prose.revision.mustEqual(prose.revision + 1L)
        }

        @Test
        fun `mention should be included at requested position`() {
            val proseUpdate = prose.withEntityMentioned(characterId, 2, 3)
            proseUpdate.prose.mentions.mustEqual(
                listOf(
                    ProseMention(characterId, ProseMentionRange(2, 3))
                )
            )
        }

    }

    @Nested
    inner class `With Mention Text Replaced` {

        private val entityId = Character.Id().mentioned()

        @Nested
        inner class `When No Mentions Match Entity Id` {

            private val prose = makeProse()

            @Test
            fun `prose should not be modified`() {
                val update = prose.withMentionTextReplaced(entityId, "" to "I should not appear")
                assertEquals(prose, update.prose)
            }

            @Test
            fun `event should not be produced`() {
                val update = prose.withMentionTextReplaced(entityId, "" to "I should not appear")
                assertNull(update.event)
            }

        }

        @Test
        fun `produced event should communicate text changes`() {
            val prose = makeProse(
                content = "I mention Bob",
                mentions = listOf(
                    ProseMention(entityId, ProseMentionRange(10, 3))
                )
            )
            val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
            update.event!!.let {
                it.proseId.mustEqual(prose.id)
                it.newContent.mustEqual(update.prose.content)
                it.entityId.mustEqual(entityId)
                it.deletedText.mustEqual("Bob")
                it.insertedText.mustEqual("Frank")
            }
        }

        @Nested
        inner class `When Sections in Content Match Mention Text but don't Mention Entity` {

            val prose = makeProse(
                content = "I mention Bob, the character and I just talk about Bob, the idea.  I mention a different Bob",
                mentions = listOf(
                    ProseMention(entityId, ProseMentionRange(10, 3)),
                    ProseMention(Character.Id().mentioned(), ProseMentionRange(89, 3))
                )
            )

            @Test
            fun `prose content should not modify those areas`() {
                val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
                assertEquals(
                    "I mention Frank, the character and I just talk about Bob, the idea.  I mention a different Bob",
                    update.prose.content
                )
            }

        }

        @Nested
        inner class `When Multiple Mentions Match` {

            val prose = makeProse(
                content = "I mention Bob, the character and then I mention Bob again.  Let's say Bob a third time.",
                mentions = listOf(
                    ProseMention(entityId, ProseMentionRange(10, 3)),
                    ProseMention(entityId, ProseMentionRange(48, 3)),
                    ProseMention(entityId, ProseMentionRange(70, 3))
                )
            )

            @Test
            fun `should modify content areas with matched mentions`() {
                val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
                update.prose.content.mustEqual(
                    "I mention Frank, the character and then I mention Frank again.  Let's say Frank a third time."
                )
            }

            @Test
            fun `should update mention positions and lengths`() {
                val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
                assertEquals(
                    listOf(
                        ProseMention(entityId, ProseMentionRange(10, 5)),
                        ProseMention(entityId, ProseMentionRange(50, 5)),
                        ProseMention(entityId, ProseMentionRange(74, 5))
                    ),
                    update.prose.mentions
                )
            }

            @Test
            fun `produced event should communicate mention positioning changes`() {
                val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
                update.event!!.newMentions.mustEqual(
                    listOf(
                        ProseMention(entityId, ProseMentionRange(10, 5)),
                        ProseMention(entityId, ProseMentionRange(50, 5)),
                        ProseMention(entityId, ProseMentionRange(74, 5))
                    )
                )
            }

        }

        @Test
        fun `other mentions should be shifted, but not modified`() {
            val aliceId = Character.Id().mentioned()
            val prose = makeProse(
                content = "I mention Bob, the character and then I mention Alice.  Let's say Bob and Alice again.",
                mentions = listOf(
                    ProseMention(entityId, ProseMentionRange(10, 3)),
                    ProseMention(aliceId, ProseMentionRange(48, 5)),
                    ProseMention(entityId, ProseMentionRange(66, 3)),
                    ProseMention(aliceId, ProseMentionRange(74, 5))
                )
            )
            val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
            assertEquals(
                listOf(
                    ProseMention(entityId, ProseMentionRange(10, 5)),
                    ProseMention(aliceId, ProseMentionRange(50, 5)),
                    ProseMention(entityId, ProseMentionRange(68, 5)),
                    ProseMention(aliceId, ProseMentionRange(78, 5))
                ),
                update.prose.mentions
            )
        }

        @Nested
        inner class `When multiple mentions with different text match entity` {

            val prose = makeProse(
                content = "I mention Bob, the character and then I mention him as Robert.  Another name for him is Bobby.",
                mentions = listOf(
                    ProseMention(entityId, ProseMentionRange(10, 3)),
                    ProseMention(entityId, ProseMentionRange(55, 6)),
                    ProseMention(entityId, ProseMentionRange(88, 5))
                )
            )

            @Test
            fun `only mentions that match entity and text should be replaced`() {
                val update = prose.withMentionTextReplaced(entityId, "Robert" to "Frank")
                update.prose.content.mustEqual(
                    "I mention Bob, the character and then I mention him as Frank.  Another name for him is Bobby."
                )
            }

        }

    }

    @Nested
    inner class `With Text Removed` {

        @Test
        fun `range out of bounds should throw error`() {
            val prose = makeProse(content = "abcde")
            assertThrows<IndexOutOfBoundsException> { prose.withTextRemoved(-4..0) }
            assertThrows<IndexOutOfBoundsException> { prose.withTextRemoved(-1..2) }
            assertThrows<IndexOutOfBoundsException> { prose.withTextRemoved(2..6) }
            assertThrows<IndexOutOfBoundsException> { prose.withTextRemoved(-1..6) }
            assertThrows<IndexOutOfBoundsException> { prose.withTextRemoved(6..9) }
        }

        @Test
        fun `cannot bisect a mention`() {
            val prose = makeProse(
                content = "Talking about Bob can be fun.",
                mentions = listOf(ProseMention(entityId(), ProseMentionRange(14, 3)))
            )
            assertThrows<ProseMentionCannotBeBisected> { prose.withTextRemoved(12..14) }
            assertThrows<ProseMentionCannotBeBisected> { prose.withTextRemoved(14..15) }
            assertThrows<ProseMentionCannotBeBisected> { prose.withTextRemoved(15..19) }

            // should be fine
            prose.withTextRemoved(12 until 14 /* == 12 .. 13 */)
            prose.withTextRemoved(17..19)
            prose.withTextRemoved(14 until 17 /* == 14 .. 16 */)
        }

        @Nested
        inner class `When Range is Valid` {

            @Test
            fun `should remove text in range`() {
                val prose = makeProse(content = "I'm content that will have a portion deleted.")
                val update = prose.withTextRemoved(1..15 /* == 1 until 16 */)
                update.prose.content.mustEqual(
                    "I will have a portion deleted."
                )
                update.event.index.mustEqual(1)
                update.event.deletedText.mustEqual("'m content that")
            }

            @Test
            fun `should adjust mentions after removed section`() {
                val bobId = entityId()
                val frankId = entityId()
                val prose = makeProse(
                    content = "Mention Bob, then remove text, then mention Frank",
                    mentions = listOf(
                        ProseMention(bobId, ProseMentionRange(8, 3)),
                        ProseMention(frankId, ProseMentionRange(44, 5))
                    )
                )
                val update = prose.withTextRemoved(11..28)
                update.prose.mentions.mustEqual(
                    listOf(
                        ProseMention(bobId, ProseMentionRange(8, 3)),
                        ProseMention(frankId, ProseMentionRange(26, 5))
                    )
                )
                update.event.newMentions.mustEqual(update.prose.mentions)
            }

            @Test
            fun `should remove mentions inside range`() {
                val bobId = entityId()
                val frankId = entityId()
                val prose = makeProse(
                    content = "Mention Bob, then mention Frank and remove text, then mention Frank",
                    mentions = listOf(
                        ProseMention(bobId, ProseMentionRange(8, 3)),
                        ProseMention(frankId, ProseMentionRange(26, 5)),
                        ProseMention(frankId, ProseMentionRange(62, 5))
                    )
                )
                val update = prose.withTextRemoved(11 until 62)
                update.prose.mentions.mustEqual(
                    listOf(
                        ProseMention(bobId, ProseMentionRange(8, 3)),
                        ProseMention(frankId, ProseMentionRange(11, 5))
                    )
                )
                update.event.newMentions.mustEqual(update.prose.mentions)
            }

        }

    }

    @Nested
    inner class `Without Mention` {

        @Test
        fun `when mention does not exist, should throw error`() {
            val mention = ProseMention(entityId(), ProseMentionRange(10, 3))
            val prose = makeProse(content = "I mention Bob, the character", mentions = emptyList())
            assertThrows<MentionDoesNotExistInProse> { prose.withoutMention(mention) }
        }

        @Test
        fun `should remove mention`() {
            val mention = ProseMention(entityId(), ProseMentionRange(10, 3))
            val prose = makeProse(content = "I mention Bob, the character", mentions = listOf(mention))
            val update = prose.withoutMention(mention)
            update.prose.mentions.contains(mention).mustEqual(false)
            update.event.entityId.mustEqual(mention.entityId)
            update.event.position.mustEqual(mention.position)
        }

        @Test
        fun `should not remove text`() {
            val mention = ProseMention(entityId(), ProseMentionRange(10, 3))
            val prose = makeProse(content = "I mention Bob, the character", mentions = listOf(mention))
            val update = prose.withoutMention(mention)
            update.prose.content.mustEqual("I mention Bob, the character")
        }

    }

    @Nested
    inner class `Replace Content` {

        private val prose = makeProse()
        private val bob = makeCharacter()
        private val frank = makeCharacter()
        private val alexis = makeCharacter()

        @Test
        fun `content should be joined together`() {
            val (newProse) = prose.withContentReplaced(
                listOf(
                    ProseContent("", bob.id.mentioned() to singleLine("Bob")),
                    ProseContent(" can be annoying.  But listen to ", frank.id.mentioned() to singleLine("Frank")),
                    ProseContent(" and he'll tell you that ", alexis.id.mentioned() to singleLine("Alexis")),
                    ProseContent(" is worse.", null)
                )
            )
            newProse.content.mustEqual("Bob can be annoying.  But listen to Frank and he'll tell you that Alexis is worse.")
        }

        @Test
        fun `mentions should be in relative positions`() {
            val (newProse) = prose.withContentReplaced(
                listOf(
                    ProseContent("", bob.id.mentioned() to singleLine("Bob")),
                    ProseContent(" can be annoying.  But listen to ", frank.id.mentioned() to singleLine("Frank")),
                    ProseContent(" and he'll tell you that ", alexis.id.mentioned() to singleLine("Alexis")),
                    ProseContent(" is worse.", null)
                )
            )
            newProse.mentions.mustEqual(
                listOf(
                    ProseMention(bob.id.mentioned(), ProseMentionRange(0, 3)),
                    ProseMention(frank.id.mentioned(), ProseMentionRange(36, 5)),
                    ProseMention(alexis.id.mentioned(), ProseMentionRange(66, 6))
                )
            )
        }

        @Test
        fun `should emit event`() {
            val (newProse, event) = prose.withContentReplaced(
                listOf(
                    ProseContent("", bob.id.mentioned() to singleLine("Bob")),
                    ProseContent(" can be annoying.  But listen to ", frank.id.mentioned() to singleLine("Frank")),
                    ProseContent(" and he'll tell you that ", alexis.id.mentioned() to singleLine("Alexis")),
                    ProseContent(" is worse.", null)
                )
            )
            event.revision.mustEqual(prose.revision + 1)
            event.proseId.mustEqual(prose.id)
            event.newContent.mustEqual(newProse.content)
            event.newMentions.mustEqual(newProse.mentions)
        }

    }
}