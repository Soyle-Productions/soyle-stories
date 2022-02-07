package com.soyle.stories.storyevent.character.remove.ramifications

import com.soyle.stories.character.removeCharacterFromStory.RamificationsReport
import com.soyle.stories.common.markdown.markdown
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.character.Character
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.ramifications.RamificationsViewModel
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventRamificationsReport
import com.soyle.stories.usecase.character.remove.RemoveCharacterFromStory
import javafx.scene.Parent
import javafx.scene.control.Label
import tornadofx.*

class RemoveCharacterFromStoryEventRamificationsReportView : Fragment() {

    private val locale: RemoveCharacterFromStoryEventRamificationsReportLocale = resolve()
    val viewModel: RemoveCharacterFromStoryEventRamificationsReportViewModel by param(defaultValue = RemoveCharacterFromStoryEventRamificationsReportViewModel())

    override val root: Parent = vbox {
        dynamicContent(viewModel.items()) { items ->
            items.orEmpty().forEach {
                markdown(locale.characterInSceneEffectMessage(
                    it
                ))
            }
        }
    }

}

fun removeCharacterFromStoryEventRamifications(
    characterId: Character.Id,
    scope: ProjectScope
): RemoveCharacterFromStoryEventRamificationsReport {
    val ramificationsTool = scope.get<RamificationsViewModel>()

    val report = ramificationsTool.report(
        RemoveCharacterFromStory::class,
        characterId
    ) {
        val fragment = find<RemoveCharacterFromStoryEventRamificationsReportView>(scope)
        content = fragment.root
        graphic().bind(fragment.iconProperty)
        text().bind(fragment.titleProperty)
        isListed().bind(fragment.viewModel.isNeeded())
        setOnCloseRequest { fragment.viewModel.cancel() }
        fragment.viewModel.isNeeded().onChangeUntil({ it != true }) {
            if (it != true) fragment.viewModel.cancel()
        }
    }

    val view = report.content!!.uiComponent<RemoveCharacterFromStoryEventRamificationsReportView>()!!

    return view.viewModel
}