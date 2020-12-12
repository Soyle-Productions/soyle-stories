package com.soyle.stories.prose.usecases

import com.soyle.stories.common.EntityId
import com.soyle.stories.common.mustEqual
import com.soyle.stories.doubles.ProseRepositoryDouble
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.entities.ProseMentionRange
import com.soyle.stories.prose.ProseDoesNotExist
import com.soyle.stories.prose.makeProse
import com.soyle.stories.prose.usecases.readProse.ReadProse
import com.soyle.stories.prose.usecases.readProse.ReadProseUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Read Prose Unit Test` {

    private val prose = makeProse()
    private val proseRepository = ProseRepositoryDouble()
    private var result: ReadProse.ResponseModel? = null

    @Test
    fun `prose doesn't exist should throw error`() {
        val error = assertThrows<ProseDoesNotExist> {
            readProse()
        }
        error.proseId.mustEqual(prose.id)
    }

    @Nested
    inner class `Prose Exists` {

        init {
            proseRepository.givenProse(prose)
        }

        @Test
        fun `should output current revision`() {
            readProse()
            val result = result!!
            result.proseId.mustEqual(prose.id)
            result.revision.mustEqual(prose.revision)
        }

        @Test
        fun `should output prose content`() {
            proseRepository.givenProse(makeProse(id = prose.id, content = "I'm a funky monkey\nfrom funky town."))
            readProse()
            val result = result!!
            result.body.mustEqual(
                "I'm a funky monkey\n" +
                        "from funky town."
            )
        }

        @Test
        fun `should output prose mentions`() {
            val characterId = Character.Id()
            val locationId = Location.Id()
            proseRepository.givenProse(
                makeProse(
                    id = prose.id,
                    content = "I'm a funky monkey\nfrom funky town.",
                    mentions = listOf(
                        ProseMention(EntityId.of(Character::class).id(characterId), ProseMentionRange(12, 6)),
                        ProseMention(EntityId.of(Location::class).id(locationId), ProseMentionRange(24, 10))
                    )
                )
            )
            readProse()
            val result = result!!
            result.mentions.mustEqual(
                listOf(
                    ProseMention(EntityId.of(Character::class).id(characterId), ProseMentionRange(12, 6)),
                    ProseMention(EntityId.of(Location::class).id(locationId), ProseMentionRange(24, 10))
                )
            )
        }

    }

    private fun readProse() {
        val useCase: ReadProse = ReadProseUseCase(proseRepository)
        runBlocking {
            useCase.invoke(prose.id, object : ReadProse.OutputPort {
                override suspend fun receiveProse(response: ReadProse.ResponseModel) {
                    result = response
                }
            })
        }
    }

}