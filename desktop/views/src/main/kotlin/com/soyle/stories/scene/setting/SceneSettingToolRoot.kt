package com.soyle.stories.scene.setting

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.components.layouts.LayoutStyles.Companion.inviteMessage
import com.soyle.stories.common.components.layouts.inviteMessage
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.common.guiUpdate
import com.soyle.stories.common.scopedListener
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneRenamed
import com.soyle.stories.scene.deleteScene.SceneDeletedReceiver
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesReceiver
import com.soyle.stories.scene.renameScene.SceneRenamedReceiver
import com.soyle.stories.scene.setting.list.SceneSettingInviteImage.Companion.sceneSettingInviteImage
import com.soyle.stories.scene.setting.list.SceneSettingItemList
import com.soyle.stories.scene.target.SceneTargeted
import com.soyle.stories.scene.target.SceneTargetedReceiver
import javafx.application.Platform
import javafx.beans.property.ObjectPropertyBase
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import tornadofx.*
import java.util.*

class SceneSettingToolRoot(
    initialScene: Pair<Scene.Id, String>?,
    private val locale: SceneSettingToolLocale,

    sceneRenamed: Notifier<SceneRenamedReceiver>,
    sceneRemoved: Notifier<SceneDeletedReceiver>,
    sceneTargeted: Notifier<SceneTargetedReceiver>,

    private val makeSceneSettingItemList: SceneSettingItemList.Factory
) : VBox() {

    interface Factory {
        operator fun invoke(initialScene: Pair<Scene.Id, String>?): SceneSettingToolRoot
    }

    private val model = objectProperty<SceneSettingToolModel>(SceneSettingToolModel.NoSceneSelected)

    val selectedScene: Scene.Id?
        get() = (model.get() as? SceneSettingToolModel.SceneSelected)?.sceneId

    private val noSceneSelected = booleanBinding(model) { model.value is SceneSettingToolModel.NoSceneSelected }

    init {
        addClass(Styles.sceneSettingToolRoot)
        toggleClass(Styles.noScene, noSceneSelected)
        asSurface {
            inheritedElevation = Elevation.getValue(4)
        }
    }

    private val header = hbox {
        addClass(Stylesheet.headerPanel)
        asSurface {
            inheritedElevation = Elevation.getValue(4)
            relativeElevation = Elevation.getValue(8)
        }

        label {
            addClass(Styles.sceneName)
            addClass(TextStyles.fieldLabel)
            toggleClass(TextStyles.warning, noSceneSelected)

            scopedListener(model) {
                when (it) {
                    is SceneSettingToolModel.NoSceneSelected -> textProperty().bind(locale.noSceneSelected)
                    is SceneSettingToolModel.SceneSelected -> textProperty().bind(stringBinding(locale.selectedScene, it.sceneName) {
                        locale.selectedScene.value.invoke(it.sceneName.value)
                    })
                }
            }
        }
    }

    init {
        vbox {
            addClass(Stylesheet.content)
            vgrow = Priority.ALWAYS

            dynamicContent(model) {
                when (it) {
                    is SceneSettingToolModel.SceneSelected -> {
                        add(makeSceneSettingItemList(it.sceneId).apply {
                            vgrow = Priority.ALWAYS
                        })
                    }
                    is SceneSettingToolModel.NoSceneSelected -> {
                        sceneSettingInviteImage()
                        toolTitle { textProperty().bind(locale.useLocationsAsSceneSetting) }
                        inviteMessage {
                            addClass(TextStyles.fieldLabel)
                            dynamicContent(locale.noSceneSelectedInviteMessage) { if (it != null) apply(it) }
                        }
                    }
                }
            }
        }
    }

    private val domainEventListener = object :
        SceneRenamedReceiver,
        SceneDeletedReceiver
    {
        override suspend fun receiveSceneRenamed(event: SceneRenamed) {
            withContext(Dispatchers.JavaFx) {
                val currentModel = model.value
                if (currentModel is SceneSettingToolModel.SceneSelected) {
                    if (currentModel.sceneId != event.sceneId) return@withContext
                    currentModel.sceneName.set(event.sceneName)
                }
            }
        }

        override suspend fun receiveSceneDeleted(event: Scene.Id) {
            withContext(Dispatchers.JavaFx) {
                val currentModel = model.value
                if (currentModel is SceneSettingToolModel.SceneSelected) {
                    if (currentModel.sceneId != event) return@withContext
                    model.set(SceneSettingToolModel.NoSceneSelected)
                }
            }
        }
    }

    init {
        sceneRenamed.addListener(domainEventListener)
        sceneRemoved.addListener(domainEventListener)
    }

    private val guiEventListener = object :
        SceneTargetedReceiver
    {
        override suspend fun receiveSceneTargeted(event: SceneTargeted) {
            guiUpdate {
                model.set(SceneSettingToolModel.SceneSelected(event.sceneId, stringProperty(event.sceneName)))
            }
        }
    }

    init {
        sceneTargeted.addListener(guiEventListener)
        if (initialScene != null) {
            model.set(SceneSettingToolModel.SceneSelected(initialScene.first, stringProperty(initialScene.second)))
        }
    }

    override fun getUserAgentStylesheet(): String = Styles().externalForm

    class Styles : Stylesheet() {

        companion object {

            val sceneSettingToolRoot by cssclass()

            val noScene by csspseudoclass()

            val sceneName by cssclass()

            init {
                fun import() {
                    importStylesheet<Styles>()
                    SceneSettingItemList.Styles // initialize
                }
                if (Platform.isFxApplicationThread()) import()
                else runLater { import() }
            }

        }

        init {
            sceneSettingToolRoot {
                headerPanel {
                    alignment = Pos.CENTER_LEFT
                    padding = box(10.px, 16.px)
                }

                content {
                    alignment = Pos.TOP_LEFT
                    spacing = 0.px
                    padding = box(0.px)
                }
                and(noScene) {
                    content {
                        alignment = Pos.CENTER
                        spacing = 16.px
                        padding = box(16.px)

                        inviteMessage {
                            hyperlink {
                                borderWidth = multi(box(0.px))
                            }
                        }
                    }
                }
            }
        }

    }

}