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
            assertTrue(newProse.text.isEmpty())
            assertTrue(newProse.content.isEmpty())
            assertTrue(newProse.mentions.isEmpty())
            newProse.revision.mustEqual(0L)
        }

    }

    @Nested
    inner class `Insert Text` {

        val characterId = Character.Id().mentioned()
        val prose = makeProse(proseId, content = listOf(ProseContent("banana", null)))

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
                proseUpdate.prose.text.mustEqual("bananas")
            }

            @Test
            fun `should append to end text content`() {
                val proseUpdate = prose.withTextInserted("s", 6)
                proseUpdate.prose.content.single().run {
                    text.mustEqual("bananas")
                    startIndex.mustEqual(0)
                    endIndex.mustEqual(7)
                }
            }

        }

        @Nested
        inner class `Prepend to Beginning` {

            @Test
            fun `should update paragraph text with text at beginning`() {
                val proseUpdate = prose.withTextInserted("a ", 0)
                proseUpdate.prose.text.mustEqual("a banana")
            }

            @Test
            fun `should prepend to beginning of text content`() {
                val proseUpdate = prose.withTextInserted("a ", 0)
                proseUpdate.prose.content.single().run {
                    text.mustEqual("a banana")
                    startIndex.mustEqual(0)
                    endIndex.mustEqual(8)
                }
            }

        }

        @Nested
        inner class `Insert Between Text` {

            @Test
            fun `should update paragraph text with text in the middle`() {
                val proseUpdate = prose.withTextInserted("ned M", 3)
                proseUpdate.prose.text.mustEqual("banned Mana")
            }

            @Test
            fun `should insert into middle of text content`() {
                val proseUpdate = prose.withTextInserted("ned M", 3)
                proseUpdate.prose.content.single().run {
                    text.mustEqual("banned Mana")
                    startIndex.mustEqual(0)
                    endIndex.mustEqual(11)
                }
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
                proseUpdate.prose.mentions.single().run{
                    entityId.mustEqual(characterId)
                    startIndex.mustEqual(6)
                    endIndex.mustEqual(9)
                }
            }

            @Test
            fun `should insert text content before mention`() {
                val proseUpdate = proseWithMention.withTextInserted(
                    "one ",
                    0
                )
                proseUpdate.prose.content.map { it.text.toString() }.mustEqual(listOf(
                    "one ba",
                    "nan",
                    "a"
                ))
                proseUpdate.prose.mentions.single().text.mustEqual(singleLine("nan"))
            }

        }

    }

    @Nested
    inner class `Mention Entity` {

        val characterId = Character.Id().mentioned()
        private val prose = makeProse(proseId, content = listOf(ProseContent("banana", null)))

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
            proseUpdate.prose.mentions.single().run {
                entityId.mustEqual(characterId)
                startIndex.mustEqual(2)
                endIndex.mustEqual(5)
            }
        }

        @Test
        fun `content should be split to separate text from mention`() {
            val proseUpdate = prose.withEntityMentioned(characterId, 2, 3)
            proseUpdate.prose.content.map { it.text.toString() }.mustEqual(listOf(
                "ba",
                "nan",
                "a"
            ))
            proseUpdate.prose.mentions.single().text.mustEqual(singleLine("nan"))
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
                content = listOf(
                    ProseContent("I mention ", entityId to singleLine("Bob"))
                )
            )
            val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
            update.event!!.let {
                it.proseId.mustEqual(prose.id)
                it.newContent.mustEqual(update.prose.text)
                it.entityId.mustEqual(entityId)
                it.deletedText.mustEqual("Bob")
                it.insertedText.mustEqual("Frank")
            }
        }

        @Nested
        inner class `When Sections in Content Match Mention Text but don't Mention Entity` {

            val prose = makeProse(
                content = listOf(
                    ProseContent("I mention ", entityId to singleLine("Bob")),
                    ProseContent(", the character and I just talk about Bob, the idea.  I mention a different ", Character.Id().mentioned() to singleLine("Bob"))
                )
            )

            @Test
            fun `prose content should not modify those areas`() {
                val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
                assertEquals(
                    "I mention Frank, the character and I just talk about Bob, the idea.  I mention a different Bob",
                    update.prose.text
                )
            }

        }

        @Nested
        inner class `When Multiple Mentions Match` {

            val prose = makeProse(
                content = listOf(
                    ProseContent("I mention ", entityId to singleLine("Bob")),
                    ProseContent(", the character and then I mention ", entityId to singleLine("Bob")),
                    ProseContent(" again.  Let's say ", entityId to singleLine("Bob")),
                    ProseContent(" a third time.", null)
                )
            )

            @Test
            fun `should modify content areas with matched mentions`() {
                val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
                update.prose.text.mustEqual(
                    "I mention Frank, the character and then I mention Frank again.  Let's say Frank a third time."
                )
            }

            @Test
            fun `should update mention positions and lengths`() {
                val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
                update.prose.mentions.forEach { it.entityId.mustEqual(entityId) }
                update.prose.mentions.forEach { it.text.toString().mustEqual("Frank") }
                update.prose.mentions.map { it.startIndex }.mustEqual(listOf(10, 50, 74))
                update.prose.mentions.map { it.endIndex }.mustEqual(listOf(15, 55, 79))
            }

            @Test
            fun `produced event should communicate mention positioning changes`() {
                val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
                update.event!!.newMentions.forEach { it.entityId.mustEqual(entityId) }
                update.event!!.newMentions.forEach { it.text.toString().mustEqual("Frank") }
                update.event!!.newMentions.map { it.startIndex }.mustEqual(listOf(10, 50, 74))
                update.event!!.newMentions.map { it.endIndex }.mustEqual(listOf(15, 55, 79))
            }

        }

        @Test
        fun `other mentions should be shifted, but not modified`() {
            val aliceId = Character.Id().mentioned()
            val prose = makeProse(
                content = listOf(
                    ProseContent("I mention ", entityId to singleLine("Bob")),
                    ProseContent(", the character and then I mention ", aliceId to singleLine("Alice")),
                    ProseContent(".  Let's say ", entityId to singleLine("Bob")),
                    ProseContent(" and ", aliceId to singleLine("Alice")),
                    ProseContent(" again.", null)
                )
            )
            val update = prose.withMentionTextReplaced(entityId, "Bob" to "Frank")
            update.prose.mentions.map { it.entityId }.mustEqual(listOf(entityId, aliceId, entityId, aliceId))
            update.prose.mentions.map { it.text.toString() }.mustEqual(listOf("Frank", "Alice", "Frank", "Alice"))
            update.prose.mentions.map { it.startIndex }.mustEqual(listOf(10, 50, 68, 78))
            update.prose.mentions.map { it.endIndex }.mustEqual(listOf(15, 55, 73, 83))
        }

        @Nested
        inner class `When multiple mentions with different text match entity` {

            val prose = makeProse(
                content = listOf(
                    ProseContent("I mention ", entityId to singleLine("Bob")),
                    ProseContent(", the character and then I mention him as ", entityId to singleLine("Robert")),
                    ProseContent(".  Another name for him is ", entityId to singleLine("Bobby")),
                    ProseContent(".", null)
                )
            )

            @Test
            fun `only mentions that match entity and text should be replaced`() {
                val update = prose.withMentionTextReplaced(entityId, "Robert" to "Frank")
                update.prose.text.mustEqual(
                    "I mention Bob, the character and then I mention him as Frank.  Another name for him is Bobby."
                )
            }

        }

    }

    @Nested
    inner class `With Text Removed` {

        @Test
        fun `range out of bounds should throw error`() {
            val prose = makeProse(content = listOf(ProseContent("abcde", null)))
            assertThrows<IndexOutOfBoundsException> { prose.withTextRemoved(-4..0) }
            assertThrows<IndexOutOfBoundsException> { prose.withTextRemoved(-1..2) }
            assertThrows<IndexOutOfBoundsException> { prose.withTextRemoved(2..6) }
            assertThrows<IndexOutOfBoundsException> { prose.withTextRemoved(-1..6) }
            assertThrows<IndexOutOfBoundsException> { prose.withTextRemoved(6..9) }
        }

        @Test
        fun `cannot bisect a mention`() {
            val prose = makeProse(
                content = listOf(
                    ProseContent("Talking about ", entityId() to singleLine("Bob")),
                    ProseContent(" can be fun.", null)
                )
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
                val prose = makeProse(content = listOf(ProseContent("I'm content that will have a portion deleted.", null)))
                val update = prose.withTextRemoved(1..15 /* == 1 until 16 */)
                update.prose.text.mustEqual(
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
                    content = listOf(
                        ProseContent("Mention ", bobId to singleLine("Bob")),
                        ProseContent(", then remove text, then mention ", frankId to singleLine("Frank"))
                    )
                )
                val update = prose.withTextRemoved(11..28)
                update.prose.mentions.map { it.entityId }.mustEqual(listOf(bobId, frankId))
                update.prose.mentions.map { it.startIndex }.mustEqual(listOf(8, 26))
                update.prose.mentions.map { it.endIndex }.mustEqual(listOf(11, 31))
                update.event.newMentions.mustEqual(update.prose.mentions)
            }

            @Test
            fun `should remove mentions inside range`() {
                val bobId = entityId()
                val frankId = entityId()
                val prose = makeProse(
                    content = listOf(
                        ProseContent("Mention ", bobId to singleLine("Bob")),
                        ProseContent(", then mention ", frankId to singleLine("Frank")),
                        ProseContent(" and remove text, then mention ", frankId to singleLine("Frank"))
                    )
                )
                val update = prose.withTextRemoved(11 until 62)
                update.prose.mentions.map { it.entityId }.mustEqual(listOf(bobId, frankId))
                update.prose.mentions.map { it.startIndex }.mustEqual(listOf(8, 11))
                update.prose.mentions.map { it.endIndex }.mustEqual(listOf(11, 16))
                update.event.newMentions.mustEqual(update.prose.mentions)
            }

        }

    }

    @Nested
    inner class `Without Mention` {

        @Test
        fun `when mention does not exist, should throw error`() {
            val entityId = entityId()
            val prose = makeProse(content = listOf(ProseContent("I mention Bob, the character", null)))
            assertThrows<MentionDoesNotExistInProse> { prose.withoutMention(entityId, 10) }
        }

        @Test
        fun `should remove mention`() {
            val entityId = entityId()
            val prose = makeProse(content = listOf(
                ProseContent("I mention ", entityId to singleLine("Bob")), ProseContent(", the character", null)
            ))
            val update = prose.withoutMention(entityId, 10)
            assertNull(update.prose.mentions.find { it.entityId == entityId && it.startIndex == 10 })
            update.event.entityId.mustEqual(entityId)
            update.event.position.index.mustEqual(10)
            update.event.position.length.mustEqual(3)
        }

        @Test
        fun `should not remove text`() {
            val entityId = entityId()
            val prose = makeProse(content = listOf(
                ProseContent("I mention ", entityId to singleLine("Bob")), ProseContent(", the character", null)
            ))
            val update = prose.withoutMention(entityId, 10)
            update.prose.text.mustEqual("I mention Bob, the character")
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
            newProse.text.mustEqual("Bob can be annoying.  But listen to Frank and he'll tell you that Alexis is worse.")
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
            newProse.mentions.map { it.entityId }.mustEqual(listOf(bob.id.mentioned(), frank.id.mentioned(), alexis.id.mentioned()))
            newProse.mentions.map { it.text.toString() }.mustEqual(listOf("Bob", "Frank", "Alexis"))
            newProse.mentions.map { it.startIndex }.mustEqual(listOf(0, 36, 66))
            newProse.mentions.map { it.endIndex }.mustEqual(listOf(3, 41, 72))
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
            event.newContent.mustEqual(newProse.text)
            event.newMentions.mustEqual(newProse.mentions)
        }

    }
}