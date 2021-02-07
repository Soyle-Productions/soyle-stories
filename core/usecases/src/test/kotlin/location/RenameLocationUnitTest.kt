package com.soyle.stories.usecase.location

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.locationName
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.prose.*
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.usecase.location.renameLocation.RenameLocation
import com.soyle.stories.usecase.location.renameLocation.RenameLocationUseCase
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RenameLocationUnitTest {

    private val location = makeLocation()
    private val inputName = locationName()

    private var updatedLocation: Location? = null
    private val updatedProse: MutableList<Prose> = mutableListOf()
    private var result: RenameLocation.ResponseModel? = null

    private val locationRepository = LocationRepositoryDouble(onUpdateLocation = ::updatedLocation::set)
    private val proseRepository = ProseRepositoryDouble(onReplaceProse = updatedProse::add)

    @Test
    fun `when location does not exist, should throw error`() {
        val error = assertThrows<LocationDoesNotExist> {
            renameLocation()
        }
        error.locationId.mustEqual(location.id.uuid)
    }

    @Nested
    inner class `When Location Exists` {
        init {
            locationRepository.givenLocation(location)
        }

        @Nested
        inner class `When Input Name is the Same as Current Name` {

            @Test
            fun `should not update location`() {
                renameLocation(location.name)
                assertNull(updatedLocation)
            }

            @Test
            fun `should not output event`() {
                renameLocation(location.name)
                assertNull(result)
            }

        }

        @Nested
        inner class `When Input Name is Different` {

            @Test
            fun `should update location`() {
                renameLocation()
                updatedLocation!!.name.mustEqual(inputName)
            }

            @Test
            fun `should event`() {
                renameLocation()
                result!!.locationRenamed.let {
                    it.locationId.mustEqual(location.id)
                    it.newName.mustEqual(inputName.value)
                }
            }

            @Test
            fun `should not update prose`() {
                renameLocation()
                updatedProse.isEmpty().mustEqual(true)
            }

            @Nested
            inner class `When Mentioned in Prose` {

                private val prose = List(3) {
                    makeProse(
                        content = location.name.value,
                        mentions = listOf(
                            ProseMention(
                                location.id.mentioned(),
                                ProseMentionRange(0, location.name.length)
                            )
                        )
                    )
                }

                init {
                    prose.onEach(proseRepository::givenProse)
                }

                @Test
                fun `should update prose`() {
                    renameLocation()
                    updatedProse.mapTo(HashSet(3)) { it.id }
                        .mustEqual(prose.mapTo(HashSet(3)) { it.id })
                    updatedProse.forEach {
                        it.content.contains(location.name.value).mustEqual(false)
                        it.content.contains(inputName.value).mustEqual(true)
                    }
                }

                @Test
                fun `should output prose mention text replaced events`() {
                    renameLocation()
                    result!!.mentionTextReplaced.mapTo(HashSet(3)) { it.proseId }
                        .mustEqual(prose.mapTo(HashSet(3)) { it.id })
                }

            }

        }

    }

    private fun renameLocation(withName: SingleNonBlankLine = inputName) {
        val useCase: RenameLocation = RenameLocationUseCase(locationRepository, proseRepository)
        runBlocking {
            useCase.invoke(location.id, withName, object : RenameLocation.OutputPort {
                override suspend fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
                    result = response
                }
            })
        }
    }
}