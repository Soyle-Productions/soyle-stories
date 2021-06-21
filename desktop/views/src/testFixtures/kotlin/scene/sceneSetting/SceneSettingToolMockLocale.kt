package com.soyle.stories.desktop.view.scene.sceneSetting

import com.soyle.stories.common.components.text.TextStyles
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
    override val useLocationsAsSceneSetting: StringProperty = stringProperty("Use Locations as Scene Setting"),
    override val noSceneSelectedInviteMessage: ObjectProperty<Parent.() -> Unit> = objectProperty {
        /*
        No scene has been targeted to use locations.  Click on a scene in the Scene List or click anywhere inside of an open Scene Editor to target a scene and see what locations are being used.
         */
        text("No scene has been selected to use locations.  Click on a scene in the ")
        hyperlink("Scene List")
        text(" or click anywhere inside of an open Scene Editor to ")
        add(TextExt("select").apply { addClass(TextStyles.warning) })
        text(" a scene and see what locations are being used.")
    },
    override val selectedScene: ObjectProperty<(String) -> String> = objectProperty { "Selected Scene: $it" },
    override val sceneSettingItemListLocale: SceneSettingItemListLocaleMock = SceneSettingItemListLocaleMock()
) : SceneSettingToolLocale