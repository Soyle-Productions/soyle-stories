package com.soyle.stories.layout

import com.soyle.stories.character.CharacterArcComponent
import com.soyle.stories.entities.Project
import com.soyle.stories.gui.SingleThreadTransformer
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.closeTool.CloseToolUseCase
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayoutUseCase
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.layout.usecases.openTool.OpenToolUseCase
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpenedUseCase
import com.soyle.stories.location.LocationComponent
import com.soyle.stories.project.eventbus.CloseToolNotifier
import com.soyle.stories.project.eventbus.GetSavedLayoutNotifier
import com.soyle.stories.project.layout.openTool.OpenToolNotifier
import com.soyle.stories.project.eventbus.ToggleToolOpenedNotifier
import com.soyle.stories.project.layout.LayoutController
import com.soyle.stories.project.layout.LayoutPresenter
import com.soyle.stories.project.layout.LayoutView
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.soylestories.DataComponent
import java.util.*

class LayoutComponent(
  projectId: UUID,
  dataComponent: DataComponent,
  characterArcComponent: CharacterArcComponent,
  locationComponent: LocationComponent,
  layoutView: () -> LayoutView
) {

	val getSavedLayout: GetSavedLayout by lazy {
		GetSavedLayoutUseCase(dataComponent.layoutRepository)
	}

	val toggleToolOpened: ToggleToolOpened by lazy {
		ToggleToolOpenedUseCase(dataComponent.layoutRepository)
	}

	val openTool: OpenTool by lazy {
		OpenToolUseCase(Project.Id(projectId), dataComponent.layoutRepository)
	}
	val closeTool: CloseTool by lazy {
		CloseToolUseCase(dataComponent.context, projectId)
	}

	private val getSavedLayoutNotifier by lazy {
		GetSavedLayoutNotifier()
	}
	private val toggleToolOpenedNotifier by lazy {
		ToggleToolOpenedNotifier()
	}
	private val openToolNotifier by lazy {
		OpenToolNotifier()
	}
	private val closeToolNotifier by lazy {
		CloseToolNotifier()
	}

	private val layoutPresenter by lazy {
		LayoutPresenter(
		  layoutView(),
		  getSavedLayoutNotifier,
		  toggleToolOpenedNotifier,
		  openToolNotifier,
		  closeToolNotifier,
		  characterArcComponent.characterArcEvents.removeCharacterFromStory,
		  characterArcComponent.characterArcEvents.deleteLocalCharacterArc,
		  characterArcComponent.characterArcEvents.removeCharacterFromLocalComparison,
		  locationComponent.locationEvents
		)
	}


	val getSavedLayoutOutputPort: GetSavedLayout.OutputPort
		get() = layoutPresenter

	val toggleToolOpenedOutputPort: ToggleToolOpened.OutputPort
		get() = toggleToolOpenedNotifier

	val openToolOutputPort: OpenTool.OutputPort
		get() = openToolNotifier

	val closeToolOutputPort: CloseTool.OutputPort
		get() = closeToolNotifier

	val layoutViewListener: LayoutViewListener by lazy {
		LayoutController(
		  SingleThreadTransformer,
		  getSavedLayout,
		  getSavedLayoutOutputPort,
		  toggleToolOpened,
		  toggleToolOpenedOutputPort,
		  closeTool,
		  closeToolOutputPort,
		  layoutPresenter
		)
	}

}