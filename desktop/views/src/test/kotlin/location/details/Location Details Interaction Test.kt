package com.soyle.stories.desktop.view.location.details

import com.soyle.stories.desktop.view.common.components.dataDisplay.`Chip Access`.Companion.access
import com.soyle.stories.desktop.view.location.details.`Location Details Access`.Companion.access
import com.soyle.stories.desktop.view.runHeadless
import com.soyle.stories.desktop.view.testconfig.DESIGN
import com.soyle.stories.location.details.models.LocationDetailsModel
import com.soyle.stories.location.details.components.LocationDetailsRoot
import com.soyle.stories.location.details.models.AvailableSceneToHostModel
import com.soyle.stories.location.details.models.HostedSceneItemModel
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Scene
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import tornadofx.objectProperty
import tornadofx.observableListOf
import tornadofx.stringProperty

class `Location Details Interaction Test` : FxRobot() {

    init {
        if (!DESIGN) runHeadless()
    }

    private val stage = FxToolkit.registerPrimaryStage()

    @AfterEach
    fun `cleanup stage`() {
        interact { stage.close() }
    }

    private val hostedScenes = ReadOnlyListWrapper<HostedSceneItemModel>(observableListOf())
    private val state = LocationDetailsModel.Loaded(
        SimpleStringProperty(""),
        hostedScenes.readOnlyProperty,
        ReadOnlyListWrapper<AvailableSceneToHostModel>(observableListOf()).readOnlyProperty
    )

    private var reDescribeRequest: Map<String, Any?>? = null
    private var removeHostedSceneRequest: Map<String, Any?>? = null
    private val actions = UserActionsMock(
        onReDescribeLocation = { reDescribeRequest = mapOf("description" to it) },
        onRemoveScene = ::removeHostedSceneRequest::set
    )

    private val view = LocationDetailsRoot(objectProperty<LocationDetailsModel>(state), actions, LocaleMock())
    private fun showView() {
        interact {
            stage.scene = Scene(view)
            stage.show()
        }
    }

    @Nested
    inner class `Description Field` {

        @Test
        fun `clicking away should do nothing`() {
            view.access {
                showView()
                interact {
                    descriptionField!!.input!!.requestFocus()
                    view.requestFocus()
                }
            }
            assertNull(reDescribeRequest)
        }

        @Nested
        inner class `Given new description value input` {

            @Test
            fun `clicking away should send update`() {
                view.access {
                    val descriptionInput = descriptionField!!.input!!
                    descriptionInput.text = "New Description"
                    showView()
                    interact {
                        descriptionInput.requestFocus()
                        view.requestFocus()
                    }
                }
                assertEquals(mapOf("description" to "New Description"), reDescribeRequest)
            }

        }

    }

    @Nested
    inner class `Hosted Scenes`
    {

        init {
            hostedScenes.setAll(
                HostedSceneItemModel(com.soyle.stories.domain.scene.Scene.Id(), stringProperty("")),
                HostedSceneItemModel(com.soyle.stories.domain.scene.Scene.Id(), stringProperty("")),
                HostedSceneItemModel(com.soyle.stories.domain.scene.Scene.Id(), stringProperty(""))
            )
        }

        @Nested
        inner class `When Hosted Scene is Deleted`
        {

            private val sceneToRemove = hostedScenes.random()

            @Test
            fun `should request to remove the scene`() {
                view.access {
                    showView()
                    val deleteButton = hostedScenesList!!.hostedSceneItems.find { it.id == sceneToRemove.id.toString() }!!
                        .access().deleteButton!!
                    interact {
                        clickOn(deleteButton)
                    }
                }
                assertEquals(
                    mapOf("sceneId" to sceneToRemove.id),
                    removeHostedSceneRequest
                )

            }

        }

    }

}