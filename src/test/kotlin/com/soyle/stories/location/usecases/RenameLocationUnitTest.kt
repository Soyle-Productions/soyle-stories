package com.soyle.stories.location.usecases

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.common.mustEqual
import com.soyle.stories.entities.Location
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.locationName
import com.soyle.stories.location.makeLocation
import com.soyle.stories.location.usecases.renameLocation.RenameLocation
import com.soyle.stories.location.usecases.renameLocation.RenameLocationUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RenameLocationUnitTest {

	private val location = makeLocation()
	private val inputName = locationName()

	private var updatedLocation: Location? = null
	private var result: RenameLocation.ResponseModel? = null

	private val locationRepository = LocationRepositoryDouble(onUpdateLocation = ::updatedLocation::set)

	@Test
	fun `when location does not exist, should throw error`() {
		val error = assertThrows<LocationDoesNotExist> {
			renameLocation()
		}
		error.locationId.mustEqual(location.id.uuid)
	}

	@Nested
	inner class `When Location Exists`
	{
		init {
		    locationRepository.givenLocation(location)
		}

		@Nested
		inner class `When Input Name is the Same as Current Name`
		{

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
		inner class `When Input Name is Different`
		{

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

		}

	}

	private fun renameLocation(withName: SingleNonBlankLine = inputName) {
		val useCase: RenameLocation = RenameLocationUseCase(locationRepository)
		runBlocking {
			useCase.invoke(location.id, withName, object : RenameLocation.OutputPort {
				override suspend fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
					result = response
				}
			})
		}
	}
}