package com.soyle.stories.scene.characters.inspect

import com.soyle.stories.common.Receiver
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.events.CharacterGainedMotivationInScene
import com.soyle.stories.domain.scene.events.SceneRenamed
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.CharacterGainedMotivationInSceneNotifier
import com.soyle.stories.scene.renameScene.SceneRenamedNotifier
import com.soyle.stories.scene.renameScene.SceneRenamedReceiver
import com.soyle.stories.usecase.scene.common.InheritedMotivation
import javafx.beans.binding.ObjectExpression
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.withContext
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class InheritedMotivationViewModel(
    itemState: ObjectProperty<InheritedMotivation>,
    scope: Scope? = null
) {

    private val _itemState = itemState
    var itemState: InheritedMotivation by _itemState
        private set

    val sceneId: Scene.Id
        get() = _itemState.get().sceneId

    private val _sceneName = _itemState.stringBinding { it?.sceneName }
    val sceneName: String by _sceneName
    fun sceneName(): ObservableValue<String> = _sceneName

    private val _motivation = _itemState.stringBinding { it?.motivation }
    val motivation: String by _motivation
    fun motivation(): ObservableValue<String> = _motivation

    private val logic = scope?.let { ViewLogic(scope) }


    private inner class ViewLogic(val scope: Scope) {

        private val mainContext = scope.get<ThreadTransformer>().guiContext

        private val sceneRenamedReceiver = SceneRenamedReceiver { event ->
            withContext(mainContext) { itemState = itemState.withEventApplied(event) }
        } listensTo scope.get<SceneRenamedNotifier>()

        private val motivationGainedReceiver = Receiver<CharacterGainedMotivationInScene> { event ->
            withContext(mainContext) { itemState = itemState.withEventApplied(event) }
        } listensTo scope.get<CharacterGainedMotivationInSceneNotifier>()

    }

}