package com.soyle.stories.desktop.view.scene.sceneSetting

import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.markdown.ObservableMarkdownString
import com.soyle.stories.desktop.view.scene.sceneSetting.list.SceneSettingItemListLocaleMock
import com.soyle.stories.scene.setting.SceneSettingToolLocale
import com.soyle.stories.scene.setting.list.SceneSettingItemListLocale
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Parent
import org.fxmisc.richtext.TextExt
import tornadofx.*

class SceneSettingToolMockLocale(
    override val sceneSettingToolTitle: StringProperty = stringProperty("Scene Setting"),
    override val noSceneSelected: StringProperty = stringProperty("No Scene Selected"),
    override val noSceneSelected_inviteMessage: ObservableMarkdownString = ObservableMarkdownString(stringProperty(
        """
            No scene has been selected to use locations.  Click on a scene in the (Scene List)[Tool(SceneList)] 
            or click anywhere inside of an open Scene Editor to select a scene and see what locations are being used.
        """.trimIndent()
    )),
    private val sceneNameF: (ObservableValue<String>) -> ObservableValue<String> = { stringBinding(it) { "Scene: ${it.value}" } },
    override val list: SceneSettingItemListLocale = SceneSettingItemListLocaleMock(),
    override val sceneSettingItemListLocale: SceneSettingItemListLocaleMock = SceneSettingItemListLocaleMock()
) : SceneSettingToolLocale {
    override fun selectedScene(sceneName: ObservableValue<String>): ObservableValue<String> = sceneNameF.invoke(sceneName)
}