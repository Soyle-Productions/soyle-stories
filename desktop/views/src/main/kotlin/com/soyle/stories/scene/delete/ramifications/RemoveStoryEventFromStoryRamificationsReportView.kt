package com.soyle.stories.scene.delete.ramifications

import com.soyle.stories.common.components.emptyListDisplay
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.ramifications.RamificationsViewModel
import com.soyle.stories.scene.delete.DeleteSceneRamificationsReport
import com.soyle.stories.scene.effects.CharacterWillGainInheritedMotivation
import com.soyle.stories.scene.effects.CharacterWillGainInheritedMotivationViewModel
import com.soyle.stories.scene.effects.InheritedCharacterMotivationWillBeCleared
import com.soyle.stories.scene.effects.InheritedMotivationWillBeClearedViewModel
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*

class DeleteSceneRamificationsReportView : Fragment() {

    val viewModel: DeleteSceneRamificationsReportViewModel by param(defaultValue = DeleteSceneRamificationsReportViewModel())

    override val root: Parent =  vbox {
        stackpane { vgrow = Priority.ALWAYS }.apply {
            dynamicContent(viewModel.items) { items ->
                if (items.isNullOrEmpty()) {
                    emptyListDisplay(
                        viewModel.items.emptyProperty().not(),
                        "No scenes will be affected by deleting this scene.".toProperty(),
                        "Delete Scene".toProperty(),
                        callToAction = viewModel::delete
                    )
                } else {
                    vbox {
                        addClass("effects")
                        with(scope) {
                            children.setAll(items.mapNotNull {
                                when (it) {
                                    is CharacterWillGainInheritedMotivationViewModel -> {
                                        CharacterWillGainInheritedMotivation(it)
                                    }
                                    is InheritedMotivationWillBeClearedViewModel -> {
                                        InheritedCharacterMotivationWillBeCleared(it)
                                    }
                                    else -> null
                                }
                            })
                        }
                    }
                }
            }
        }
        buttonbar { padding = Insets(10.0, 10.0, 10.0, 10.0) }.apply {
            button("Delete") {
                action(viewModel::delete)
            }
            button("Cancel") {
                action(viewModel::cancel)
            }
        }
    }

}

fun deleteSceneRamifications(
    scope: Scope = FX.defaultScope,
    ramificationsTool: RamificationsViewModel,
    scene: Scene.Id
): DeleteSceneRamificationsReport {

    val report = ramificationsTool.report(
        RemoveStoryEventFromProject::class,
        scene
    ) {
        val fragment = find<DeleteSceneRamificationsReportView>(scope)
        content = fragment.root
        graphic().bind(fragment.iconProperty)
        text().bind(fragment.titleProperty)
        isListed().bind(fragment.viewModel.isNeeded())
        setOnCloseRequest { fragment.viewModel.cancel() }
        fragment.viewModel.isNeeded().onChangeUntil({ it != true }) {
            if (it != true) fragment.viewModel.cancel()
        }
    }

    return report.content!!.uiComponent<DeleteSceneRamificationsReportView>()!!.viewModel
}