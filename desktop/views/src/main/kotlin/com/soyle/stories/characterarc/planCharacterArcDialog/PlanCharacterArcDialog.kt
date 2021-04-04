package com.soyle.stories.characterarc.planCharacterArcDialog

import com.soyle.stories.characterarc.planNewCharacterArc.CreatedCharacterArcNotifier
import com.soyle.stories.characterarc.planNewCharacterArc.CreatedCharacterArcReceiver
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.usecase.character.arc.planNewCharacterArc.CreatedCharacterArc
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*

class PlanCharacterArcDialog : Fragment("New Character Arc") {

    override val scope: ProjectScope = super.scope as ProjectScope
    val planCharacterArcDialogViewListener = resolve<PlanCharacterArcDialogViewListener>()
    var characterId: String = ""
    var onCharacterArcCreated: ((CreatedCharacterArc) -> Unit)? = null

    private val createdCharacterArcNotifier = resolve<CreatedCharacterArcNotifier>()
    private val createdCharacterArcReceiver: CreatedCharacterArcReceiver = object : CreatedCharacterArcReceiver {
        override suspend fun receiveCreatedCharacterArc(createdCharacterArc: CreatedCharacterArc) {
            scope.applicationScope.get<ThreadTransformer>().gui {
                onCharacterArcCreated?.invoke(createdCharacterArc)
            }
            createdCharacterArcNotifier.removeListener(this)
        }
    }

    override val root = form {
        textfield {
            requestFocus()
            onAction = EventHandler {
                it.consume()
                if (text.isEmpty()) {
                    return@EventHandler
                }
                createdCharacterArcNotifier.addListener(createdCharacterArcReceiver)
                planCharacterArcDialogViewListener.planCharacterArc(characterId, text)
                close()
            }
        }
    }

}

fun planCharacterArcDialog(
    scope: ProjectScope,
    characterId: String, owner: Stage?, onCharacterArcCreated: (CreatedCharacterArc) -> Unit = {}
): PlanCharacterArcDialog = find<PlanCharacterArcDialog>(scope).apply {
    this.characterId = characterId
    this.onCharacterArcCreated = onCharacterArcCreated
    openModal(StageStyle.UTILITY, Modality.APPLICATION_MODAL, escapeClosesWindow = true, owner = owner)?.apply {
        centerOnScreen()
    }
}
