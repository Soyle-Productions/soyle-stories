package com.soyle.stories.desktop.view.location.details

import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.desktop.view.common.NodeTest
import com.soyle.stories.desktop.view.common.`Node Assertions`.Companion.assertThat
import com.soyle.stories.desktop.view.location.details.`Location Details Access`.Companion.access
import com.soyle.stories.desktop.view.testconfig.DESIGN
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.details.models.LocationDetailsModel
import com.soyle.stories.location.details.components.LocationDetailsRoot
import com.soyle.stories.location.details.models.AvailableSceneToHostModel
import com.soyle.stories.location.details.models.HostedSceneItemModel
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Labeled
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import org.testfx.matcher.control.LabeledMatchers.hasText
import tornadofx.objectProperty
import tornadofx.observableListOf

class `Location Details Presentation Test` : NodeTest<LocationDetailsRoot>() {

    private val state = objectProperty<LocationDetailsModel>(LocationDetailsModel.Loading)
    private val locale = LocaleMock()
    override val view = LocationDetailsRoot(state, UserActionsMock(), locale).apply {
        if (DESIGN) asSurface { absoluteElevation = Elevation.getValue(5) }
    }

    @Test
    fun `Should be loading`() {
        view.access {
            assertThat(loadingIndicator).isVisible()
        }
    }

    @Test
    fun `should not display fields`() {
        view.access {
            assertThat(descriptionField).isNotVisible()
            assertThat(hostedScenesList).isNotVisible()
        }
    }

    @Nested
    inner class `Given data has loaded` {

        private val description = SimpleStringProperty("")
        private val hostedScenes = ReadOnlyListWrapper<HostedSceneItemModel>(observableListOf())
        private val availableScenesToHost = ReadOnlyListWrapper<AvailableSceneToHostModel>(null)
        private val loadedState = LocationDetailsModel.Loaded(
            description,
            hostedScenes.readOnlyProperty,
            availableScenesToHost.readOnlyProperty
        )

        init {
            state.set(loadedState)
        }

        @Test
        fun `should not be loading`() {
            view.access {
                assertThat(loadingIndicator).isNotVisible()
            }
        }

        @Test
        fun `should display fields`() {
            view.access {
                assertThat(descriptionField).isVisible()
                assertThat(hostedScenesList).isVisible()
            }
        }

        @Test
        fun `should display invitation message from locale`() {
            locale.hostSceneInLocationInvitationMessage.set("You should host some scenes")
            view.access {
                verifyThat(from(hostedScenesList!!.invitation!!).lookup(".label").query<Labeled>(), hasText("You should host some scenes"))
            }
        }

        @Nested
        inner class `Description Field`
        {

            @Test
            fun `should display label from locale`() {
                locale.description.set("Totally wacky description label")
                view.access {
                    verifyThat(descriptionField!!.title, hasText("Totally wacky description label"))
                }
            }

            @Test
            fun `should display description value`() {
                description.set("Description 47")
                view.access {
                    assertEquals("Description 47", descriptionField?.input?.text)
                }
            }

        }

        @Nested
        inner class `Hosted Scene List`
        {

            @Test
            fun `should display header from locale`() {
                locale.scenesHostedInLocation.set("All the stuff that happens here")
                view.access {
                    verifyThat(hostedScenesList!!.title, hasText("All the stuff that happens here"))
                }
            }

            @Test
            fun `should display empty list`() {
                view.access {
                    assertThat(hostedScenesList?.invitation).isVisible()
                    assertThat(hostedScenesList?.list).isNotVisible()
                    assertThat(hostSceneButton?.button).isVisible()
                }
            }

            @Nested
            inner class `Given Scenes have been Hosted`
            {
                init {
                    hostedScenes.setAll(
                        HostedSceneItemModel(Scene.Id(), SimpleStringProperty("Scene 42")),
                        HostedSceneItemModel(Scene.Id(), SimpleStringProperty("The Banana Scene")),
                        HostedSceneItemModel(Scene.Id(), SimpleStringProperty("The one where that thing happens"))
                    )
                }

                private val hostedScenesById = loadedState.hostedScenes.associateBy { it.id.toString() }

                @Test
                fun `should display list of hosted scenes`() {
                    view.access {
                        assertThat(hostedScenesList?.invitation).isNotVisible()
                        assertThat(hostedScenesList?.list).isVisible()
                        assertThat(hostSceneButton?.button).isVisible()
                        val hostedSceneItems = hostedScenesList!!.list!!.hostedSceneItems
                        assertEquals(3, hostedSceneItems.size)
                        assertEquals(
                            hostedScenesById.keys,
                            hostedSceneItems.map { it.id }.toSet()
                        )
                        hostedSceneItems.forEach {
                            it as Labeled
                            assertEquals(hostedScenesById[it.id]!!.name.value, it.text)
                        }
                    }
                }

                @Test
                fun `should update hosted scene name when changed`() {
                    loadedState.hostedScenes.first().name.set("New Scene Name")
                    view.access {
                        hostedScenesList!!.list!!.hostedSceneItems
                            .find { it.id == loadedState.hostedScenes.first().id.toString() }!!
                            .let {
                                it as Labeled
                                assertEquals("New Scene Name", it.text)
                            }
                    }
                }

            }

        }

    }

}