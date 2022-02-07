package com.soyle.stories.scene.outline.remove.ramifications

import com.soyle.stories.common.markdown.markdown
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.ramifications.RamificationsViewModel
import com.soyle.stories.storyevent.coverage.uncover.UncoverStoryEventRamificationsReport
import com.soyle.stories.usecase.character.remove.RemoveCharacterFromStory
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromScene
import javafx.scene.Parent
import javafx.scene.control.Label
import tornadofx.*

class UncoverStoryEventRamificationsReportView: Fragment() {

    private val locale: UncoverStoryEventRamificationsReportLocale = resolve()
    val viewModel: UncoverStoryEventRamificationsReportViewModel by param(defaultValue = UncoverStoryEventRamificationsReportViewModel())

    init {
        titleProperty.bind(locale.uncoverStoryEventRamifications())
    }

    override val root: Parent = vbox {
        bindChildren(viewModel.items()) { item ->
            markdown(locale.implicitCharacterRemovedFromSceneMessage(item))
        }
    }

}

fun uncoverStoryEventRamifications(
    scope: ProjectScope,
    storyEventId: StoryEvent.Id
): UncoverStoryEventRamificationsReport {
    val ramificationsTool = scope.get<RamificationsViewModel>()

    val report = ramificationsTool.report(
        UncoverStoryEventFromScene::class,
        storyEventId
    ) {
        val fragment = find<UncoverStoryEventRamificationsReportView>(scope)
        content = fragment.root
        graphic().bind(fragment.iconProperty)
        text().bind(fragment.titleProperty)
        isListed().bind(fragment.viewModel.isNeeded())
        setOnCloseRequest { fragment.viewModel.cancel() }
        fragment.viewModel.isNeeded().onChangeUntil({ it != true }) {
            if (it != true) fragment.viewModel.cancel()
        }
    }

    val view = report.content!!.uiComponent<UncoverStoryEventRamificationsReportView>()!!

    return view.viewModel
}