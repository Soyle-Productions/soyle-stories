package com.soyle.stories.di.project

import com.soyle.stories.common.ThreadTransformerImpl
import com.soyle.stories.di.characterarc.CharacterArcComponent
import com.soyle.stories.di.location.LocationComponent
import com.soyle.stories.di.modules.DataComponent
import com.soyle.stories.entities.Project
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.closeTool.CloseToolUseCase
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayoutUseCase
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.layout.usecases.openTool.OpenToolUseCase
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpenedUseCase
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBenchModel
import com.soyle.stories.project.eventbus.CloseToolNotifier
import com.soyle.stories.project.eventbus.GetSavedLayoutNotifier
import com.soyle.stories.project.eventbus.OpenToolNotifier
import com.soyle.stories.project.eventbus.ToggleToolOpenedNotifier
import com.soyle.stories.project.layout.LayoutController
import com.soyle.stories.project.layout.LayoutPresenter
import com.soyle.stories.project.layout.LayoutViewListener
import tornadofx.Component
import tornadofx.FX
import tornadofx.ScopedInstance

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 2:34 PM
 */
class LayoutComponent : Component(), ScopedInstance {

    override val scope: ProjectScope = super.scope as ProjectScope

    private val dataComponent: DataComponent by inject(overrideScope = FX.defaultScope)
    private val characterArcComponent: CharacterArcComponent by inject()
    private val locationComponent: LocationComponent by inject()

    val getSavedLayout: GetSavedLayout by lazy {
        GetSavedLayoutUseCase(dataComponent.layoutRepository)
    }

    val toggleToolOpened: ToggleToolOpened by lazy {
        ToggleToolOpenedUseCase(dataComponent.layoutRepository)
    }

    val openTool: OpenTool by lazy {
        OpenToolUseCase(Project.Id(scope.projectId), dataComponent.layoutRepository)
    }
    val closeTool: CloseTool by lazy {
        CloseToolUseCase(dataComponent.context, scope.projectId)
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
            find<WorkBenchModel>(),
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
        get() = getSavedLayoutNotifier

    val toggleToolOpenedOutputPort: ToggleToolOpened.OutputPort
        get() = toggleToolOpenedNotifier

    val openToolOutputPort: OpenTool.OutputPort
        get() = openToolNotifier

    val closeToolOutputPort: CloseTool.OutputPort
        get() = closeToolNotifier

    val layoutViewListener: LayoutViewListener by lazy {
        LayoutController(
            ThreadTransformerImpl,
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