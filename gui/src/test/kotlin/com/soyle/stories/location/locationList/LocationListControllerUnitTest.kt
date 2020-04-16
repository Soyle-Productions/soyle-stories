package com.soyle.stories.location.locationList

import com.soyle.stories.gui.SingleThreadTransformer
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.controllers.RenameLocationController
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.location.usecases.renameLocation.RenameLocation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class LocationListControllerUnitTest {

	private var useCasesCalled: List<Any> = emptyList()
	private val listAllLocations: ListAllLocations = object : ListAllLocations {
		override suspend fun invoke(output: ListAllLocations.OutputPort) {
			useCasesCalled = useCasesCalled + this
		}
	}

	@Test
	fun `calls listAllLocations use case`() {
		val viewListener: LocationListViewListener = LocationListController(
		  SingleThreadTransformer,
		  listAllLocations,
		  object : ListAllLocations.OutputPort {
			  override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {}
		  },
		  RenameLocationController(object : RenameLocation {
			  override suspend fun invoke(id: UUID, name: String, output: RenameLocation.OutputPort) {}
		  }, object : RenameLocation.OutputPort {
			  override fun receiveRenameLocationFailure(failure: LocationException) { }
			  override fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) { }
		  })
		)
		viewListener.getValidState()
		assertThat(useCasesCalled).contains(listAllLocations)
	}

}