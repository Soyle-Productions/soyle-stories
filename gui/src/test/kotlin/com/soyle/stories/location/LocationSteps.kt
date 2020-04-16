package com.soyle.stories.location

import com.soyle.stories.character.CharacterArcComponent
import com.soyle.stories.entities.Project
import com.soyle.stories.gui.SingleThreadTransformer
import com.soyle.stories.layout.LayoutComponent
import com.soyle.stories.layout.LayoutTestView
import com.soyle.stories.layout.LayoutViewModelWrapper
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogTestView
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogViewModelWrapper
import com.soyle.stories.location.createLocationDialog.CreateNewLocationDialogComponent
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogComponent
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogTestView
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialogViewModelWrapper
import com.soyle.stories.location.locationList.LocationListComponent
import com.soyle.stories.location.locationList.LocationListTestView
import com.soyle.stories.location.locationList.LocationListViewModelWrapper
import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.projectList.ProjectListComponent
import com.soyle.stories.soylestories.DataComponent
import com.soyle.stories.soylestories.ProjectListViewModelWrapper
import com.soyle.stories.soylestories.SoyleStoriesComponent
import com.soyle.stories.soylestories.SoyleStoriesTestView
import com.soyle.stories.workspace.entities.Workspace
import com.soyle.stories.workspace.valueobjects.ProjectFile
import io.cucumber.java8.En
import io.cucumber.java8.PendingException
import io.cucumber.java8.Scenario
import io.github.pramcharan.wd.binary.downloader.WebDriverBinaryDownloader
import io.github.pramcharan.wd.binary.downloader.enums.BrowserType
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import java.util.*


class LocationSteps : En {
	private var driver: WebDriver? = null

	@BeforeAll
	fun before() {
		WebDriverBinaryDownloader.create().downloadLatestBinaryAndConfigure(BrowserType.CHROME)
		driver = ChromeDriver()
	}

	@AfterEach
	fun after(scenario: Scenario) {
		if (scenario.isFailed()) {
			scenario.write("Scenario failed so capturing a screenshot")
			val screenshot = driver as TakesScreenshot?
			scenario.embed(screenshot!!.getScreenshotAs(OutputType.BYTES), "image/png", scenario.name)
		}
		if (driver != null) {
			driver!!.quit()
		}
	}


	private val openProjectId = UUID.randomUUID()

	private val dataComponent = DataComponent()
	private val soyleStoriesState = ProjectListViewModelWrapper({
		layoutView
	})
	private val soyleStoriesView = SoyleStoriesTestView(
	  ProjectListComponent(
		SoyleStoriesComponent(dataComponent),
		dataComponent,
		soyleStoriesState
	  ).projectListViewListener
	)
	private val startAppIfHasNotBeen by lazy {
		soyleStoriesView.start()
		Unit
	}
	private val locationComponent by lazy {
		LocationComponent(
		  openProjectId,
		  dataComponent
		)
	}
	private val layoutComponent by lazy {
		LayoutComponent(
		  openProjectId,
		  dataComponent,
		  CharacterArcComponent(openProjectId, dataComponent),
		  locationComponent,
		  ::layoutState
		)
	}
	private val layoutView: LayoutTestView by lazy {
		if (soyleStoriesState.openProjects.find { it.projectId == openProjectId } == null) error("No open project")
		LayoutTestView(
		  openProjectId,
		  layoutComponent.layoutViewListener,
		  ::layoutState
		)
	}
	private val layoutState: LayoutViewModelWrapper by lazy {
		if (soyleStoriesState.openProjects.find { it.projectId == openProjectId } == null) error("No open project")
		LayoutViewModelWrapper({
			if (it.name == "Locations") {
				locationListView
			}
		}) {
			when (it) {
				Dialog.CreateLocation -> createLocationDialogView
				is Dialog.DeleteLocation -> deleteLocationDialogView
				else -> {}
			}
		}
	}

	private val createLocationDialogState = CreateLocationDialogViewModelWrapper()
	private val createLocationDialogView by lazy {
		CreateLocationDialogTestView(
		  CreateNewLocationDialogComponent(
			locationComponent,
			::createLocationDialogState
		  ).createLocationDialogViewListener,
		  layoutComponent.layoutViewListener
		)
	}
	private val deleteLocationDialogState = DeleteLocationDialogViewModelWrapper(::layoutState)
	private val deleteLocationDialogView by lazy {
		DeleteLocationDialogTestView(
		  DeleteLocationDialogComponent(
			locationComponent,
			::deleteLocationDialogState
		  ).deleteLocationDialogViewListener,
		  layoutComponent.layoutViewListener,
		  ::deleteLocationDialogState
		)
	}

	private val locationListState: LocationListViewModelWrapper by lazy {
		LocationListViewModelWrapper()
	}
	private val locationListView: LocationListTestView by lazy {
		LocationListTestView(
		  LocationListComponent(
			SingleThreadTransformer,
			locationComponent,
			::locationListState
		  ).locationListViewListener,
		  layoutComponent.layoutViewListener,
		  ::locationListState
		)
	}

	private var keyReceiver: (key: String) -> Unit = {
		when (it) {
			"enter" -> createLocationDialogView.pressEnterKey()
			"esc" -> createLocationDialogView.pressEscKey()
		}
	}

	init {
		Given("A project has been opened") {
			runBlocking {
				val projectFile = ProjectFile(Project.Id(openProjectId), "Untitled", "C:\\Users")
				dataComponent.workspaceRepository.updateWorkspace(Workspace(dataComponent.workerId, listOf(projectFile)))
				dataComponent.fileRepository.createFile(projectFile)
			}
		}
		Given("The Location List Tool has been opened") {
			startAppIfHasNotBeen
			if (!layoutState.staticTools.find { it.name == "Locations" }!!.isOpen) {
				layoutView.selectMenuItem("Tools.Locations")
			}
			assertThat(layoutState.staticTools.find { it.name == "Locations" }!!.isOpen).isTrue()
		}
		Given("A location has been created") {
			runBlocking {
				locationComponent.createNewLocationController.createNewLocation("Location", "")
			}
		}
		Given("The create new location dialog has been opened") {
			startAppIfHasNotBeen
			layoutComponent.layoutViewListener.openDialog(Dialog.CreateLocation)
			assertThat(layoutState.isCreateLocationDialogVisible).isTrue()
		}
		Given("The user has entered an invalid location name") {
			startAppIfHasNotBeen
			createLocationDialogView.nameText = "  "
		}
		Given("The user has entered a valid location name") {
			startAppIfHasNotBeen
			createLocationDialogView.nameText = "Valid Location"
		}
		Given("{int} Locations have been created") { int1: Int? ->
			runBlocking {
				repeat(int1!!) {
					locationComponent.createNewLocationController.createNewLocation("Location $it", "")
				}
			}
		}
		Given("the location right-click menu is open") {
			startAppIfHasNotBeen
			locationListView.rightClickOnLocation()
		}
		Given("a location has been selected") {
			startAppIfHasNotBeen
			locationListView.selectLocation()
		}
		Given("the delete location dialog has been opened") {
			startAppIfHasNotBeen
			val location = runBlocking {
				dataComponent.locationRepository.getAllLocationsInProject(Project.Id(openProjectId)).first()
			}
			layoutComponent.layoutViewListener.openDialog(Dialog.DeleteLocation(location.id.uuid.toString(), location.name))
			assertThat(layoutState.isDeleteLocationDialogVisible).isTrue()
			keyReceiver = {
				when (it) {
					"enter" -> createLocationDialogView.pressEnterKey()
					"esc" -> createLocationDialogView.pressEscKey()
				}
			}
		}
		Given("the location rename input box is visible") {
			keyReceiver = {
				when (it) {
					"enter" -> {}//createLocationDialogView.pressEnterKey()
					"esc" -> {}//createLocationDialogView.pressEscKey()
				}
			}
		}
		Given("the user has entered a valid name") {

		}



		When("User selects the file->new->location menu option") {
			startAppIfHasNotBeen
			layoutView.selectMenuItem("File.New.Location")
		}
		When("User clicks the center create new location button") {
			startAppIfHasNotBeen
			locationListView.clickCenterCreateNewLocationButton()
		}
		When("User clicks the bottom create new location button") {
			startAppIfHasNotBeen
			locationListView.clickBottomCreateNewLocationButton()
		}
		When("The user presses the Enter key") {
			startAppIfHasNotBeen
			(this::keyReceiver).get().invoke("enter")
		}
		When("The user clicks the Create button") {
			startAppIfHasNotBeen
			createLocationDialogView.clickCreateButton()
		}
		When("The user presses the Esc key") {
			startAppIfHasNotBeen
			(this::keyReceiver).get().invoke("esc")
		}
		When("The user clicks the Cancel button") {
			startAppIfHasNotBeen
			createLocationDialogView.clickCancelButton()
		}
		When("The Location List Tool is opened") {
			startAppIfHasNotBeen
			val locationTool = layoutState.staticTools.find { it.name == "Locations" }
			if (locationTool == null) throw error("no static locations list tool: ${layoutState.staticTools}")
			if (!locationTool.isOpen) {
				layoutView.selectMenuItem("Tools.Locations")
			} else {
				layoutView.selectMenuItem("Tools.Locations")
				layoutView.selectMenuItem("Tools.Locations")
			}
		}
		When("A new Location is created") {
			startAppIfHasNotBeen
			runBlocking {
				locationComponent.createNewLocationController.createNewLocation("New Location", "")
			}
		}
		When("A Location is deleted") {
			startAppIfHasNotBeen
			runBlocking {
				val firstLocation = dataComponent.locationRepository.getAllLocationsInProject(Project.Id(openProjectId)).first()
				locationComponent.deleteLocationController.deleteLocation(firstLocation.id.uuid.toString())
			}
		}
		When("the user clicks the location list tool right-click menu delete button") {
			startAppIfHasNotBeen
			locationListView.clickRightClickMenuDeleteButton()
		}
		When("the user clicks the location list tool right-click menu Rename button") {
			startAppIfHasNotBeen
			locationListView.clickRightClickMenuRenameButton()
		}
		When("the user clicks the location list tool delete button") {
			startAppIfHasNotBeen
			locationListView.clickBottomDeleteButton()
		}
		When("the user clicks the confirm delete location dialog delete button") {
			startAppIfHasNotBeen
			deleteLocationDialogView.clickConfirmButton()
		}
		When("the user clicks the confirm delete location dialog cancel button") {
			startAppIfHasNotBeen
			deleteLocationDialogView.clickCancelButton()
		}



		Then("The Location List Tool should show a special empty message") {
			assertThat(locationListState.isInEmptyState).isTrue()
		}
		Then("The Location List Tool should show all {int} locations") { locationCount: Int ->
			assertThat(locationListState.locations.size).isEqualTo(locationCount)
		}
		Then("The Location List Tool should show the new Location") {
			val allLocations = runBlocking {
				dataComponent.locationRepository.getAllLocationsInProject(Project.Id(openProjectId))
			}
			assertThat(locationListState.locations.map { it.id }).containsAll(allLocations.map { it.id.uuid.toString() })
		}
		Then("The Location List Tool should not show the deleted Location") {
			val allLocations = runBlocking {
				dataComponent.locationRepository.getAllLocationsInProject(Project.Id(openProjectId))
			}
			assertThat(locationListState.locations.map { it.id }).containsOnly(*allLocations.map { it.id.uuid.toString() }.toTypedArray())
		}
		Then("The create new location dialog should be open") {
			assertThat(layoutState.isCreateLocationDialogVisible).isTrue()
		}
		Then("An error message should be visible in the create new location dialog") {
			assertThat(layoutState.isCreateLocationDialogVisible).isTrue()
			assertThat(createLocationDialogState.errorMessage)
			  .isNotNull()
			  .isNotBlank()
		}
		Then("The create new location dialog should be closed") {
			assertThat(layoutState.isCreateLocationDialogVisible).isFalse()
		}
		Then("the confirm delete location dialog should be opened") {
			assertThat(layoutState.isDeleteLocationDialogVisible).isTrue()
		}
		Then("the delete location dialog should be closed") {
			assertThat(layoutState.isDeleteLocationDialogVisible).isFalse()
		}
		Then("the location's name should be replaced by an input box") {}
		Then("the location rename input box should contain the location's name") {}
		Then("the location rename input box should be replaced by the location name") {}
		Then("the location name should be the original name") {}
		Then("the location name should be the new name") {}
	}

}