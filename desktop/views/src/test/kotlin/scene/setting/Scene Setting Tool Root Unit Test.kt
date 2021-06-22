package com.soyle.stories.desktop.view.scene.setting

import com.soyle.stories.common.components.text.TextStyles.Companion.warning
import com.soyle.stories.desktop.view.common.NodeTest
import com.soyle.stories.desktop.view.scene.sceneSetting.SceneSettingToolMockLocale
import com.soyle.stories.desktop.view.scene.sceneSetting.`Scene Setting Tool Root Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneSetting.list.SceneSettingItemListFactory
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneRenamed
import com.soyle.stories.scene.target.SceneTargeted
import com.soyle.stories.scene.deleteScene.SceneDeletedNotifier
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesNotifier
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.renameScene.SceneRenamedNotifier
import com.soyle.stories.scene.setting.SceneSettingToolRoot
import com.soyle.stories.scene.target.SceneTargetedNotifier
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem
import javafx.scene.control.Label
import javafx.scene.text.Text
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import tornadofx.*

class `Scene Setting Tool Root Unit Test` : NodeTest<SceneSettingToolRoot>() {

    private val locale = SceneSettingToolMockLocale()

    private val sceneRenamedNotifier = SceneRenamedNotifier()
    private val sceneRemovedNotifier = SceneDeletedNotifier()
    private val sceneTargetedNotifier = SceneTargetedNotifier()

    private val makeSceneSettingItemList = SceneSettingItemListFactory()

    override val view: SceneSettingToolRoot = SceneSettingToolRoot(
        locale,
        sceneRenamedNotifier,
        sceneRemovedNotifier,
        sceneTargetedNotifier,
        makeSceneSettingItemList
    )

    @Test
    fun `should display no scene selected`() {
        `should show invitation`()

        locale.noSceneSelected.set("WARNING!  DANGER!")
        assertEquals("WARNING!  DANGER!", view.access().selectedSceneLabel.text)

        locale.useLocationsAsSceneSetting.set("Use 'em")
        assertEquals("Use 'em", view.access().inviteTitle!!.text)

        locale.noSceneSelectedInviteMessage.set {
            label("blah")
            text("foo")
            label("bar")
        }
        with(view.access().inviteMessage!!) {
            assertEquals("blah", (children.component1() as Label).text)
            assertEquals("foo", (children.component2() as Text).text)
            assertEquals("bar", (children.component3() as Label).text)
        }
    }

    private fun `should show invitation`() {
        assertTrue(view.hasClass(SceneSettingToolRoot.Styles.noScene))
        assertTrue(view.access().selectedSceneLabel.hasClass(warning))
        assertTrue(view.access().inviteImage!!.isVisible)
        assertTrue(view.access().inviteTitle!!.isVisible)
        assertTrue(view.access().inviteMessage!!.isVisible)
        assert(view.access().list?.isVisible != true)
    }

    @Nested
    inner class `Given Scene Selected` {

        private val sceneId = Scene.Id()

        init {
            makeSceneSettingItemList.onInvoke = {
                assertEquals(sceneId, it)
            }
            runBlocking {
                sceneTargetedNotifier.receiveSceneTargeted(
                    SceneTargeted(
                        sceneId,
                        Prose.Id(),
                        "This Scene"
                    )
                )
            }
            interact{}
        }

        @Test
        fun `should display scene selected`() {
            assertFalse(view.hasClass(SceneSettingToolRoot.Styles.noScene))

            with(view.access().selectedSceneLabel) {
                assertFalse(hasClass(warning))

                locale.selectedScene.set { "We're looking at: $it" }
                assertEquals("We're looking at: This Scene", text)
            }

            assert(view.access().inviteImage?.isVisible != true)
            assert(view.access().inviteTitle?.isVisible != true)
            assert(view.access().inviteMessage?.isVisible != true)

            assertTrue(view.access().list!!.isVisible)
        }

        @Nested
        inner class `When Scene is Renamed` {

            init {
                locale.selectedScene.set { "Scene: $it" }
            }

            @Test
            fun `different scene should have no affect`() {
                sceneRenamed(Scene.Id(), "New Scene Name")
                assertEquals("Scene: This Scene", view.access().selectedSceneLabel.text)
            }

            @Test
            fun `same scene should modify selected scene label`() {
                sceneRenamed(sceneId, "New Scene Name")
                assertEquals("Scene: New Scene Name", view.access().selectedSceneLabel.text)
            }

            private fun sceneRenamed(sceneId: Scene.Id, newName: String) {
                runBlocking {
                    sceneRenamedNotifier.receiveSceneRenamed(SceneRenamed(sceneId, newName))
                }
            }

        }

        @Nested
        inner class `When Scene is Removed from Story` {

            @Test
            fun `different scene should have no affect`() {
                sceneRemoved(Scene.Id())
                assertFalse(view.hasClass(SceneSettingToolRoot.Styles.noScene))
            }

            @Test
            fun `same scene should modify selected scene label`() {
                sceneRemoved(sceneId)
                assertTrue(view.hasClass(SceneSettingToolRoot.Styles.noScene))
            }

            private fun sceneRemoved(sceneId: Scene.Id) {
                runBlocking {
                    sceneRemovedNotifier.receiveSceneDeleted(sceneId)
                }
            }

        }

    }

}