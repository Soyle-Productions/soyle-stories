package com.soyle.stories.scene.characters.inspect

import com.soyle.stories.common.Receiver
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.doNothing
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.character.events.CharacterGainedMotivationInScene
import com.soyle.stories.domain.scene.character.events.CharacterMotivationInSceneChanged
import com.soyle.stories.domain.scene.character.events.CharacterMotivationInSceneCleared
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemLocale
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemViewModel
import com.soyle.stories.scene.characters.remove.confirmRemoveCharacterFromScenePrompt
import com.soyle.stories.scene.charactersInScene.assignRole.AssignRoleToCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterConfirmationPrompt
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneController
import com.soyle.stories.scene.charactersInScene.setDesire.SetCharacterDesireInSceneController
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.CharacterGainedMotivationInSceneNotifier
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.CharacterMotivationInSceneClearedNotifier
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.usecase.scene.character.inspect.CharacterInSceneInspection
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import javafx.beans.binding.ObjectExpression
import javafx.beans.binding.StringExpression
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class CharacterInSceneInspectionViewModel(
    val itemViewModel: CharacterInSceneItemViewModel,
    inspection: ObjectProperty<CharacterInSceneInspection?>,
    val isLoading: ObservableValue<Boolean> = booleanProperty(false),
    val onNavigateToPreviousScene: (Scene.Id) -> Unit = {},
    scope: Scope? = null
) {

    val item: CharacterInSceneItem
        get() = itemViewModel.item

    private val _inspection: ObjectProperty<CharacterInSceneInspection?> = inspection
    fun inspection(): ObjectExpression<CharacterInSceneInspection?> = _inspection
    val inspection: CharacterInSceneInspection?
        get() = _inspection.get()?.copy(motivationSources = otherMotivationsMap.mapNotNull { (key, vm) ->
            val value = vm.itemState ?: return@mapNotNull null
            key to value
        }.toMap())

    val character: Character.Id
        get() = itemViewModel.character

    val scene: Scene.Id
        get() = itemViewModel.scene

    fun role(): StringExpression = itemViewModel.role()
    fun hasRole(roleInScene: RoleInScene) = itemViewModel.hasRole(roleInScene)

    private val _design = _inspection.stringBinding { it?.desire }
    fun desire(): StringExpression = _design
    val desire: String by _design

    private val _motivation = _inspection.stringBinding { it?.motivation }
    fun motivation(): StringExpression = _motivation
    val motivation: String by _motivation

    private val _otherMotivationItems = _inspection.objectBinding { it?.otherMotivations }
    private var otherMotivationsMap = mapOf<Scene.Id, InheritedMotivationViewModel>()
    private val otherMotivations: ObservableValue<List<InheritedMotivationViewModel>?> = _otherMotivationItems.objectBinding { collection ->
        println("other motivation items changed $collection")
        val motivationsByScene = otherMotivationsMap
        collection.orEmpty().map { inheritedMotivation ->
            motivationsByScene.getOrElse(inheritedMotivation.sceneId) {
                InheritedMotivationViewModel(
                    objectProperty(inheritedMotivation).apply { onChange { _inspection.value = this@CharacterInSceneInspectionViewModel.inspection } },
                    scope
                )
            }
        }.also {
            otherMotivationsMap = it.associateBy { it.sceneId }
        }
    }

    private val _inheritedMotivation: ObservableValue<InheritedMotivationViewModel?> = otherMotivations.objectBinding {
        println("other motivation view models changed $it")
        otherMotivationsMap[this.inspection?.inheritedMotivation?.sceneId].also {
            println("  and motivation for $scene changed to $it")
        }
    }

    fun inheritedMotivation(): ObservableValue<InheritedMotivationViewModel?> = _inheritedMotivation
    val inheritedMotivation: InheritedMotivationViewModel? by _inheritedMotivation

    private val logic: ViewLogic? = scope?.let { ViewLogic(it) }

    val onRemoveCharacter: () -> Unit = logic?.run { ::removeCharacter } ?: ::doNothing
    val onToggleRole: (RoleInScene) -> Unit = logic?.run { ::toggleRole } ?: ::doNothing
    val onDesireChanged: (String) -> Unit = logic?.run { ::changeDesire } ?: ::doNothing
    val onMotivationChanged: (String) -> Unit = logic?.run { ::changeMotivation } ?: ::doNothing
    val onResetToPreviousMotivation: () -> Unit = logic?.run { ::resetToPreviousMotivation } ?: ::doNothing

    private inner class ViewLogic(private val scope: Scope) {
        private val context: CoroutineContext = scope.get<ThreadTransformer>().guiContext
        private val locale: CharacterInSceneItemLocale = scope.get()

        private val motivationGainedReceiver = Receiver<CharacterGainedMotivationInScene> { event ->
            withContext(context) { _inspection.value = inspection?.withEventApplied(event) }
        } listensTo scope.get<CharacterGainedMotivationInSceneNotifier>()

        private val motivationClearedReceiver = Receiver<CharacterMotivationInSceneCleared> { event ->
            withContext(context) { _inspection.value = inspection?.withEventApplied(event) }
        } listensTo scope.get<CharacterMotivationInSceneClearedNotifier>()


        private val removeCharacter: (Scene.Id, Character.Id, RemoveCharacterConfirmationPrompt) -> Job =
            scope.get<RemoveCharacterFromSceneController>()::removeCharacterFromScene
        private val characterRoleController = scope.get<AssignRoleToCharacterInSceneController>()
        private val characterDesireController = scope.get<SetCharacterDesireInSceneController>()
        private val characterMotivationController = scope.get<SetMotivationForCharacterInSceneController>()

        fun removeCharacter() {
            removeCharacter(
                item.scene,
                item.characterId,
                confirmRemoveCharacterFromScenePrompt(scope, scope.get<WorkBench>().currentStage)
            )
        }

        fun toggleRole(roleInScene: RoleInScene) {
            if (item.roleInScene == roleInScene) {
                characterRoleController.clearRole(item.scene, item.characterId)
            } else {
                characterRoleController.assignRole(item.scene, item.characterId, roleInScene)
            }
        }

        fun changeDesire(desire: String) {
            if (inspection?.desire != desire) {
                characterDesireController.setDesire(item.scene, item.characterId, desire)
            }
        }
        fun changeMotivation(motivation: String) {
            if (inspection?.motivation != motivation) {
                characterMotivationController.setMotivationForCharacter(item.scene, item.characterId, motivation)
            }
        }
        fun resetToPreviousMotivation() {
            if (! inspection?.motivation.isNullOrEmpty()) {
                characterMotivationController.clearMotivationForCharacter(item.scene, item.characterId)
            }
        }
    }

}