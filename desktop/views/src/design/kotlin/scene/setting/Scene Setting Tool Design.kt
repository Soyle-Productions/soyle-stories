package com.soyle.stories.desktop.view.scene.setting

import com.soyle.stories.desktop.view.scene.sceneSetting.SceneSettingToolMockLocale
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.ListLocationsInSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.ListLocationsToUseInSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.item.SceneSettingItemFactory
import com.soyle.stories.desktop.view.scene.sceneSetting.list.SceneSettingItemListFactory
import com.soyle.stories.desktop.view.scene.sceneSetting.list.SceneSettingItemListMockActions
import com.soyle.stories.desktop.view.scene.sceneSetting.useLocationButton.UseLocationButtonFactory
import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.desktop.view.testframework.State
import com.soyle.stories.desktop.view.testframework.SubComponent
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.config.fixed.SceneSetting
import com.soyle.stories.location.deleteLocation.DeletedLocationNotifier
import com.soyle.stories.scene.deleteScene.SceneDeletedNotifier
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.renameScene.SceneRenamedNotifier
import com.soyle.stories.scene.setting.SceneSettingToolModel
import com.soyle.stories.scene.setting.SceneSettingToolRoot
import com.soyle.stories.scene.setting.list.SceneSettingItemList
import com.soyle.stories.scene.setting.list.item.SceneSettingItemModel
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButton
import com.soyle.stories.scene.target.SceneTargeted
import com.soyle.stories.scene.target.SceneTargetedNotifier
import com.soyle.stories.usecase.location.listAllLocations.LocationItem
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInScene
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInScene
import javafx.scene.Node
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import tornadofx.FX
import tornadofx.booleanProperty
import tornadofx.objectProperty
import tornadofx.stringProperty
import kotlin.properties.Delegates

class `Scene Setting Tool Design` : DesignTest() {

    private val sceneTargetedNotifier = SceneTargetedNotifier()

    @State
    fun `no scene targeted`() = verifyDesign {
        width = 400.0
        height = 600.0
    }

    @State
    fun `scene targeted`() {
        verifyDesign {
            width = 400.0
            height = 600.0
            runBlocking {
                sceneTargetedNotifier.receiveSceneTargeted(SceneTargeted(Scene.Id(), Prose.Id(), "Scene Name"))
            }
        }
    }

    override val node: Node
        get() = SceneSettingToolRoot(
            null,
            SceneSettingToolMockLocale(),
            SceneRenamedNotifier(),
            SceneDeletedNotifier(),
            sceneTargetedNotifier,
            SceneSettingItemListFactory()
        )

    @SubComponent
    inner class `Scene Setting List` : Design() {

        private var listLocationsInScene = ListLocationsInSceneControllerDouble()

        @State
        fun `loading`() = verifyDesign()

        @State
        fun `error`() {
            listLocationsInScene = ListLocationsInSceneControllerDouble(
                onListLocationsInScene = { _, _ ->
                    listLocationsInScene.job.completeExceptionally(Error(""))
                }
            )
            verifyDesign()
        }

        @State
        fun `no scene settings`() {
            listLocationsInScene = ListLocationsInSceneControllerDouble(
                onListLocationsInScene = { _, output ->
                    runBlocking {
                        output.receiveLocationsUsedInScene(ListLocationsUsedInScene.ResponseModel(emptyList()))
                    }
                }
            )
            verifyDesign {
                width = 400.0
                height = 600.0
            }
        }

        @State
        fun `has scene settings`() {
            listLocationsInScene = ListLocationsInSceneControllerDouble(
                onListLocationsInScene = { _, output ->
                    runBlocking {
                        output.receiveLocationsUsedInScene(ListLocationsUsedInScene.ResponseModel(List(6) {
                            LocationItem(Location.Id(), "Location $it")
                        }))
                    }
                }
            )
            verifyDesign()
        }

        override val node: Node
            get() = SceneSettingItemListFactory(
                listLocationsInSceneController = listLocationsInScene
            ).invoke(Scene.Id())

        @SubComponent
        inner class `Scene Setting Item` : Design() {

            private var model =
                SceneSettingItemModel(Scene.Id(), Location.Id(), "Scene Setting Item", booleanProperty(false))

            @State
            fun `default`() = verifyDesign()

            @State
            fun `has problem`() {
                model = SceneSettingItemModel(Scene.Id(), Location.Id(), "Scene Setting Item", booleanProperty(true))
                verifyDesign()
            }

            override val node: Node
                get() = SceneSettingItemFactory().invoke(model)

        }

        @SubComponent
        inner class `Use Location Button` : Design() {

            private var listLocationsToUseInScene = ListLocationsToUseInSceneControllerDouble()

            @State
            fun `loading`() = verifyDesign()

            @State
            fun `no available locations`() {
                listLocationsToUseInScene = ListLocationsToUseInSceneControllerDouble(
                    onListLocationsToUse = { _, output ->
                        runBlocking {
                            output.receiveAvailableLocationsToUseInScene(
                                ListAvailableLocationsToUseInScene.ResponseModel(emptyList())
                            )
                        }
                    }
                )
                verifyDesign()
            }

            @State
            fun `has available locations`() {
                listLocationsToUseInScene = ListLocationsToUseInSceneControllerDouble(
                    onListLocationsToUse = { _, output ->
                        runBlocking {
                            output.receiveAvailableLocationsToUseInScene(
                                ListAvailableLocationsToUseInScene.ResponseModel(List (7) {
                                    LocationItem(Location.Id(), "Location $it")
                                })
                            )
                        }
                    }
                )
                verifyDesign()
            }

            override val node: Node
                get() = UseLocationButtonFactory(
                    listLocationsToUseInSceneController = listLocationsToUseInScene
                ).invoke(Scene.Id())

        }

    }

}