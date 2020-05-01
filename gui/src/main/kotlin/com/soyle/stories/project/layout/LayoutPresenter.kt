package com.soyle.stories.project.layout

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory
import com.soyle.stories.characterarc.LocalCharacterArcException
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc
import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.eventbus.listensTo
import com.soyle.stories.layout.LayoutException
import com.soyle.stories.layout.usecases.ActiveWindow
import com.soyle.stories.layout.usecases.StaticTool
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.project.layout.openTool.OpenToolPresenter
import com.soyle.stories.theme.LocalThemeException
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparison
import kotlin.reflect.KClass

class LayoutPresenter(
  private val view: LayoutView,
  getSavedLayoutNotifier: Notifier<GetSavedLayout.OutputPort>,
  toggleToolOpenedNotifier: Notifier<ToggleToolOpened.OutputPort>,
  openToolNotifier: Notifier<OpenTool.OutputPort>,
  closeToolNotifier: Notifier<CloseTool.OutputPort>,
  removeCharacterNotifier: Notifier<RemoveCharacterFromLocalStory.OutputPort>,
  deleteLocalCharacterArcNotifier: Notifier<DeleteLocalCharacterArc.OutputPort>,
  removeCharacterFromComparisonNotifier: Notifier<RemoveCharacterFromLocalComparison.OutputPort>,
  locationEvents: LocationEvents
) : GetSavedLayout.OutputPort, ToggleToolOpened.OutputPort, CloseTool.OutputPort, RemoveCharacterFromLocalStory.OutputPort, DeleteLocalCharacterArc.OutputPort, RemoveCharacterFromLocalComparison.OutputPort, CreateNewLocation.OutputPort, DeleteLocation.OutputPort {

	private val subPresenters = listOf(
	  OpenToolPresenter(view) listensTo openToolNotifier
	)

	init {
		getSavedLayoutNotifier.addListener(this)
		toggleToolOpenedNotifier.addListener(this)
		closeToolNotifier.addListener(this)
		removeCharacterNotifier.addListener(this)
		deleteLocalCharacterArcNotifier.addListener(this)
		removeCharacterFromComparisonNotifier.addListener(this)
		locationEvents.createNewLocation.addListener(this)
		locationEvents.deleteLocation.addListener(this)
	}

	private fun pushLayout(activeWindows: List<ActiveWindow>, staticTools: List<StaticTool>) {
		view.update {
			copy(
			  staticTools = staticTools.map { StaticToolViewModel(it.toolId.toString(), it.isOpen, it.toolTypeId.toPresentableToolName()!!) },
			  primaryWindow = activeWindows.find { it.isPrimary }!!.let(::toWindowViewModel),
			  secondaryWindows = activeWindows.filterNot { it.isPrimary }.map(::toWindowViewModel),
			  isValid = true
			)
		}
	}

	override fun receiveGetSavedLayoutResponse(response: GetSavedLayout.ResponseModel) {
		pushLayout(response.windows, response.staticTools)
	}

	override fun receiveToggleToolOpenedResponse(response: ToggleToolOpened.ResponseModel) {
		pushLayout(response.windows, response.staticTools)
	}

	override fun receiveCloseToolResponse(response: CloseTool.ResponseModel) {
		val closedWindowId = response.closedWindowId?.toString()
		view.update {
			if (closedWindowId != null) {
				copy(secondaryWindows = secondaryWindows.filterNot { it.id == closedWindowId })
			} else {
				copy(isValid = false)
			}
		}
	}

	override fun receiveRemoveCharacterFromLocalStoryResponse(response: RemoveCharacterFromLocalStory.ResponseModel) {
		if (response.removedTools.isEmpty()) return
		view.update {
			copy(isValid = false)
		}
	}

	override fun receiveDeleteLocalCharacterArcResponse(response: DeleteLocalCharacterArc.ResponseModel) {
		if (response.removedTools.isEmpty()) return
		view.update {
			copy(isValid = false)
		}
	}

	override fun receiveRemoveCharacterFromLocalComparisonResponse(response: RemoveCharacterFromLocalComparison.ResponseModel) {
		if (response.removedTools.isEmpty()) return
		view.update {
			copy(isValid = false)
		}
	}

	fun displayDialog(dialog: Dialog) {
		view.update {
			copy(
			  openDialogs = openDialogs + (dialog::class to dialog)
			)
		}
	}

	fun removeDialog(dialog: KClass<out Dialog>) {
		view.update {
			copy(
			  openDialogs = openDialogs - dialog
			)
		}
	}

	override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
		view.update {
			copy(
			  openDialogs = openDialogs - Dialog.CreateLocation::class
			)
		}
	}

    override fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
        view.update {
            copy(
              openDialogs = openDialogs - Dialog.DeleteLocation::class
            )
        }
    }

    override fun receiveDeleteLocationFailure(failure: LocationException) {

    }
	override fun receiveCreateNewLocationFailure(failure: LocationException) {

	}

	override fun receiveRemoveCharacterFromLocalComparisonFailure(failure: LocalThemeException) {

	}

	override fun receiveRemoveCharacterFromLocalStoryFailure(failure: CharacterException) {

	}

	override fun receiveCloseToolFailure(failure: LayoutException) {}

	override fun receiveDeleteLocalCharacterArcFailure(failure: LocalCharacterArcException) {

	}
}