package com.soyle.stories.character.delete.ramifications

import com.soyle.stories.character.removeCharacterFromStory.RamificationsReport
import com.soyle.stories.common.markdown.markdown
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.character.Character
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.ramifications.RamificationsViewModel
import com.soyle.stories.usecase.character.remove.RemoveCharacterFromStory
import javafx.scene.Parent
import javafx.scene.control.Label
import tornadofx.*

class RemoveCharacterRamificationsReportView : Fragment() {

    private val locale: RemoveCharacterRamificationsReportLocale = resolve()
    val viewModel: RemoveCharacterRamificationsReportViewModel by param(defaultValue = RemoveCharacterRamificationsReportViewModel())

    init {
        titleProperty.bind(locale.removeCharacterFromStoryRamifications())
    }

    override val root: Parent = vbox {
        dynamicContent(viewModel.items()) { items ->
            items.orEmpty().forEach {
                markdown(locale.characterInSceneEffectMessage(it))
            }
            if (items.isNullOrEmpty()) {
                label("Safe remove character!")
            }
        }
    }

}

fun removeCharacterRamifications(
    characterId: Character.Id,
    scope: ProjectScope
): RamificationsReport {
    val ramificationsTool = scope.get<RamificationsViewModel>()

    val report = ramificationsTool.report(
        RemoveCharacterFromStory::class,
        characterId
    ) {
        val fragment = find<RemoveCharacterRamificationsReportView>(scope)
        content = fragment.root
        graphic().bind(fragment.iconProperty)
        text().bind(fragment.titleProperty)
        isListed().bind(fragment.viewModel.isNeeded())
        setOnCloseRequest { fragment.viewModel.cancel() }
        fragment.viewModel.isNeeded().onChangeUntil({ it != true }) {
            if (it != true) fragment.viewModel.cancel()
        }
    }

    val view = report.content!!.uiComponent<RemoveCharacterRamificationsReportView>()!!

    return view.viewModel
}