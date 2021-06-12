package com.soyle.stories.desktop.view.location.details.hostSceneButton

import com.soyle.stories.desktop.view.common.`Interaction Test`
import com.soyle.stories.desktop.view.location.details.LocaleMock
import com.soyle.stories.desktop.view.location.details.UserActionsMock
import com.soyle.stories.desktop.view.location.details.`Host Scene Button Access`.Companion.access
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.details.components.HostSceneButton
import com.soyle.stories.location.details.models.AvailableSceneToHostModel
import javafx.beans.property.ReadOnlyListWrapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import tornadofx.observableListOf
import tornadofx.stringProperty

class `Host Scene Button Interaction Test` : `Interaction Test`<HostSceneButton>() {

    private var loadAvailableScenesRequest: Unit? = null
    private var createSceneRequest: Unit? = null
    private var hostSceneRequest: Map<String, Any?>? = null
    private val actions = UserActionsMock(
        onLoadAvailableScenes = { loadAvailableScenesRequest = Unit },
        onHostScene = ::hostSceneRequest::set,
        onCreateSceneToHost = { createSceneRequest = Unit }
    )
    private val availableScenesToHost = ReadOnlyListWrapper<AvailableSceneToHostModel>(null)
    private val locale = LocaleMock()
    override val view = HostSceneButton(availableScenesToHost.readOnlyProperty, actions, locale)

    @Nested
    inner class `When Button is Selected`
    {

        init {
            showView()
            interact { view.fire() }
        }

        @Test
        fun `should show menu`() {
            assertTrue(view.isShowing)
        }

        @Test
        fun `should load available scenes`() {
            assertNotNull(loadAvailableScenesRequest)
        }

    }

    @Nested
    inner class `When Available Item Selected`
    {

        private val availableScene = AvailableSceneToHostModel(Scene.Id(), stringProperty("Some scene name"))

        init {
            availableScenesToHost.set(observableListOf(availableScene))
            showView()
            interact {
                view.fire()
                view.access().availableSceneItems.single().fire()
            }
        }

        @Test
        fun `should host selected scene`() {
            assertEquals(
                mapOf("sceneId" to availableScene.sceneId),
                hostSceneRequest
            )
        }

    }

    @Nested
    inner class `When Create Scene Item Selected`
    {
        init {
            availableScenesToHost.set(observableListOf())
            showView()
            interact {
                view.fire()
                view.access().createSceneItem!!.fire()
            }
        }

        @Test
        fun `should request to create a scene`() {
            assertNotNull(createSceneRequest)
        }
    }

}