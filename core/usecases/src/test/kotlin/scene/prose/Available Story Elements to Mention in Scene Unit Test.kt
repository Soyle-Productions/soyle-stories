package com.soyle.stories.usecase.scene.prose

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.prose.mentions.AvailableStoryElementsToMentionInScene
import com.soyle.stories.usecase.shared.availability.AnyAvailableStoryElementItem
import com.soyle.stories.usecase.shared.availability.AvailableStoryElementItem
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Available Story Elements to Mention in Scene Unit Test` {

    val response = AvailableStoryElementsToMentionInScene(
        Scene.Id(),
        emptySet(),
        emptySet()
    )

    @Nested
    inner class `Can Query Results` {

        @Test
        fun `should only output items containing query`() {
            val allItems = listOf(
                AvailableStoryElementItem(Character.Id().mentioned(), "York", null),
                AvailableStoryElementItem(Character.Id().mentioned(), "Billy", null),
                AvailableStoryElementItem(Character.Id().mentioned(), "Frank", null),
                AvailableStoryElementItem(Character.Id().mentioned(), "Bobby", null),
            )
            val response = allItems.fold(response) { response, item -> response.withElement(item) }

            response.getMatches(nonBlankStr("Y")).mustEqual(
                listOf(
                    allItems[0],
                    allItems[1],
                    allItems[3]
                )
            )
        }

        @Test
        fun `matches should not be case sensitive`() {
            val item = AvailableStoryElementItem(Character.Id().mentioned(), "Billy", null)
            val response = response.withElement(item)

            response.getMatches(nonBlankStr("b")).mustEqual(
                listOf(item)
            )
        }

        @Test
        fun `should prioritize items already mentioned`() {
            val items = listOf(
                AvailableStoryElementItem(Location.Id().mentioned(), "Golden Gate Bridge", null),
                AvailableStoryElementItem(Character.Id().mentioned(), "Bobby", null),
                AvailableStoryElementItem(Location.Id().mentioned(), "Bangladesh", null),
                AvailableStoryElementItem(Character.Id().mentioned(), "Robert", null),
            )
            val response = items.fold(response) { response, item -> response.withElement(item) }
                .withMentionedId(items[0].entityId)
                .withMentionedId(items[3].entityId)

            response.getMatches(nonBlankStr("b")).take(2).toSet().mustEqual(
                setOf(items[0], items[3])
            )
        }

    }

    @Nested
    inner class `Can Find Replacement` {

        @Test
        fun `should not return anything when no elements added`() {
            response.getReplacements(Character.Id().mentioned()).isEmpty().mustEqual(true)
        }

        @Test
        fun `should only return elements of same type`() {
            response.withElement(AnyAvailableStoryElementItem(Character.Id().mentioned(), "Bob", null))
                .withElement(AnyAvailableStoryElementItem(Location.Id().mentioned(), "Bangladesh", null))
                .run {
                    getReplacements(Character.Id().mentioned()).single().name.mustEqual("Bob")
                    getReplacements(Location.Id().mentioned()).single().name.mustEqual("Bangladesh")
                }
        }

        @Test
        fun `should not include element in results`() {
            val locationId = Location.Id().mentioned()
            response.withElement(AnyAvailableStoryElementItem(Location.Id().mentioned(), "Spain", null))
                .withElement(AnyAvailableStoryElementItem(locationId, "Bangladesh", null))
                .getReplacements(locationId).single().name.mustEqual("Spain")
        }

        @Test
        fun `should list included elements first`() {
            val includedLocation = Location.Id().mentioned()
            response.withElement(AnyAvailableStoryElementItem(Location.Id().mentioned(), "Spain", null))
                .withElement(AnyAvailableStoryElementItem(Character.Id().mentioned(), "Bob", null))
                .withElement(AnyAvailableStoryElementItem(includedLocation, "Michigan", null))
                .withMentionedId(includedLocation)
                .getReplacements(Location.Id().mentioned()).run {
                    first().name.mustEqual("Michigan")
                    component2().name.mustEqual("Spain")
                }
        }

        @Test
        fun `should list alternative character names before anything else`() {
            val characterId = Character.Id().mentioned()
            val includedCharacter = Character.Id().mentioned()
            response.withElement(AnyAvailableStoryElementItem(characterId, "Bob", null))
                .withElement(AnyAvailableStoryElementItem(characterId, "Robert", null))
                .withElement(AnyAvailableStoryElementItem(Character.Id().mentioned(), "Alice", null))
                .withElement(AnyAvailableStoryElementItem(characterId, "Bobby", null))
                .withElement(AnyAvailableStoryElementItem(includedCharacter, "Frank", null))
                .withMentionedId(includedCharacter)
                .getReplacements(characterId, "Robert").map { it.name }.mustEqual(listOf(
                    "Bob",
                    "Bobby",
                    "Frank",
                    "Alice"
                ))
        }

        @Test
        fun `should not filter out different characters with the same name`() {
            val characterId = Character.Id().mentioned()
            val otherCharacter = Character.Id().mentioned()
            response.withElement(AnyAvailableStoryElementItem(characterId, "Bob", null))
                .withElement(AnyAvailableStoryElementItem(otherCharacter, "Bob", null))
                .getReplacements(characterId, "Bob")
                .single().run {
                    entityId.mustEqual(otherCharacter)
                    name.mustEqual("Bob")
                }
        }

    }


}