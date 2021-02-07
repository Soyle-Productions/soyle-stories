package com.soyle.stories.usecase.prose

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.prose.*
import com.soyle.stories.usecase.prose.readProse.ReadProse
import com.soyle.stories.usecase.prose.readProse.ReadProseUseCase
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
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
                        ProseMention(characterId.mentioned(), ProseMentionRange(12, 6)),
                        ProseMention(locationId.mentioned(), ProseMentionRange(24, 10))
                    )
                )
            )
            readProse()
            val result = result!!
            result.mentions.mustEqual(
                listOf(
                    ProseMention(characterId.mentioned(), ProseMentionRange(12, 6)),
                    ProseMention(locationId.mentioned(), ProseMentionRange(24, 10))
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