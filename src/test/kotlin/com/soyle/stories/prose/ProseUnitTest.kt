package com.soyle.stories.prose

import com.soyle.stories.common.EntityId
import com.soyle.stories.common.mustEqual
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.entities.ProseMentionRange
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProseUnitTest {

    private val proseId = Prose.Id()

    @Nested
    inner class `Create new Prose` {

        @Test
        fun `content and mentions should be empty`() {
            val (newProse, _) = Prose.create()
            assertTrue(newProse.content.isEmpty())
            assertTrue(newProse.mentions.isEmpty())
            newProse.revision.mustEqual(0L)
        }

    }

    @Nested
    inner class `Insert Text` {

        val characterId = EntityId.of(Character::class).id(Character.Id())
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

        val characterId = EntityId.of(Character::class).id(Character.Id())
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
}