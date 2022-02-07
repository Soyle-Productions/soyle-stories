package com.soyle.stories.storyevent.remove.ramifications

import com.soyle.stories.common.markdown.markdown
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.ramifications.RamificationsViewModel
import com.soyle.stories.storyevent.remove.RemoveStoryEventFromProjectRamificationsReport
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject
import javafx.scene.Parent
import javafx.scene.control.Label
import tornadofx.*

class RemoveStoryEventFromStoryRamificationsReportView : Fragment() {

    private val locale: RemoveStoryEventFromStoryRamificationsReportLocale = resolve()
    val viewModel: RemoveStoryEventFromStoryRamificationsReportViewModel by param(defaultValue = RemoveStoryEventFromStoryRamificationsReportViewModel())

    override val root: Parent = vbox {
        dynamicContent(viewModel.items()) { items ->
            items.orEmpty().forEach {
                markdown(locale.implicitCharacterRemovedFromSceneMessage(
                    it
                ))
            }
        }
    }

}

fun removeStoryEventFromStoryRamifications(
    scope: Scope = FX.defaultScope,
    ramificationsTool: RamificationsViewModel,
    storyEvents: Set<StoryEvent.Id>
): RemoveStoryEventFromProjectRamificationsReport {

    val report = ramificationsTool.report(
        RemoveStoryEventFromProject::class,
        storyEvents
    ) {
        val fragment = find<RemoveStoryEventFromStoryRamificationsReportView>(scope)
        content = fragment.root
        graphic().bind(fragment.iconProperty)
        text().bind(fragment.titleProperty)
        isListed().bind(fragment.viewModel.isNeeded())
        setOnCloseRequest { fragment.viewModel.cancel() }
        fragment.viewModel.isNeeded().onChangeUntil({ it != true }) {
            if (it != true) fragment.viewModel.cancel()
        }
    }

    return report.content!!.uiComponent<RemoveStoryEventFromStoryRamificationsReportView>()!!.viewModel
}