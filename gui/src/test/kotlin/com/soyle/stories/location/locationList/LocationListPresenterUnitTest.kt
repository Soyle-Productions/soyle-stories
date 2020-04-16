package com.soyle.stories.location.locationList

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.location.events.CreateNewLocationNotifier
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.location.usecases.listAllLocations.LocationItem
import com.soyle.stories.location.usecases.renameLocation.RenameLocation
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.function.Predicate

class LocationListPresenterUnitTest {

	private var viewModel: LocationListViewModel? = null

	@BeforeEach
	fun clear() {
		viewModel = null
	}

	@Test
	fun `empty response`() {
		givenInInvalidState()
		whenLocationListReceived(responseLocations = emptyList())
		assertThat(viewModel!!.hasLocations).isFalse()
	}

	@Test
	fun `listAllLocations overrides existing data`() {
		givenLocationItemsAlreadyListed()
		whenLocationListReceived(responseLocations = emptyList())
		assertThat(viewModel!!.hasLocations).isFalse()
	}

	@Test
	fun `some locations`() {
		givenInInvalidState()
		val locationItems = List(5) {
			LocationItem(UUID.randomUUID(), "Location Name: ${UUID.randomUUID()}")
		}
		whenLocationListReceived(responseLocations = locationItems)
		assertLocationsProperlyMappedFrom(locationItems)
	}

	@Test
	fun `react to create new location`() {
		givenLocationItemsAlreadyListed(count = 10)
		whenNewLocationReceived()
		assertThat(viewModel!!.locations.size).isEqualTo(11)
	}

	@Test
	fun `react to delete location`() {
		givenLocationItemsAlreadyListed(count = 10)
		whenLocationIsDeleted()
		assertThat(viewModel!!.locations.size).isEqualTo(9)
	}


	private fun givenInInvalidState() {
		viewModel = null
	}
	private fun givenLocationItemsAlreadyListed(count: Int = 10) {
		viewModel = LocationListViewModel(List(count) { LocationItemViewModel(UUID.randomUUID().toString(), "") })
	}

	private fun whenLocationListReceived(responseLocations: List<LocationItem>) {
		val response = ListAllLocations.ResponseModel(responseLocations)
		val presenter = getPresenter()
		presenter.receiveListAllLocationsResponse(response)
	}
	private fun whenNewLocationReceived() {
		val response = CreateNewLocation.ResponseModel(UUID.randomUUID(), "")
		val notifier = object : Notifier<CreateNewLocation.OutputPort>() {
			fun receiveCreateNewLocationResponse() {
				notifyAll { it.receiveCreateNewLocationResponse(response) }
			}
		}
		val presenter = getPresenter(notifier)
		notifier.receiveCreateNewLocationResponse()
	}
	private fun whenLocationIsDeleted() {
		val response = DeleteLocation.ResponseModel(UUID.fromString(viewModel!!.locations.first().id), emptySet())
		val notifier = object : Notifier<DeleteLocation.OutputPort>() {
			fun receiveDeleteLocationResponse() {
				notifyAll { it.receiveDeleteLocationResponse(response) }
			}
		}
		val presenter = getPresenter(deleteLocation = notifier)
		notifier.receiveDeleteLocationResponse()
	}
	private fun createLocationNotifier() = object : Notifier<CreateNewLocation.OutputPort>() {}
	private fun deleteLocationNotifier() = object : Notifier<DeleteLocation.OutputPort>() {}
	private fun renameLocationNotifier() = object : Notifier<RenameLocation.OutputPort>() {}
	private fun getPresenter(
	  createNewLocation: Notifier<CreateNewLocation.OutputPort> = createLocationNotifier(),
	  deleteLocation: Notifier<DeleteLocation.OutputPort> = deleteLocationNotifier(),
	  renameLocation: Notifier<RenameLocation.OutputPort> = renameLocationNotifier()
	) = LocationListPresenter(object : LocationListView {
		override fun update(update: LocationListViewModel?.() -> LocationListViewModel) {
			viewModel = viewModel.update()
		}
		override fun updateOrInvalidated(update: LocationListViewModel.() -> LocationListViewModel) {
			viewModel = viewModel?.update()
		}
	}, object : LocationEvents {
		override val createNewLocation: Notifier<CreateNewLocation.OutputPort> = createNewLocation
		override val deleteLocation: Notifier<DeleteLocation.OutputPort> = deleteLocation
		override val renameLocation: Notifier<RenameLocation.OutputPort> = renameLocation
	})

	private fun assertLocationsProperlyMappedFrom(source: List<LocationItem>) {
		assertThat(viewModel!!.locations.map(LocationItemViewModel::id).toSet())
		  .isEqualTo(source.map { it.id.toString() }.toSet())

		assertThat(viewModel!!.locations.map(LocationItemViewModel::name).toSet())
		  .isEqualTo(source.map(LocationItem::locationName).toSet())
	}
}