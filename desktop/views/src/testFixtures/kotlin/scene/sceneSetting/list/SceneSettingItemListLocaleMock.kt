package com.soyle.stories.desktop.view.scene.sceneSetting.list

import com.soyle.stories.common.markdown.ObservableMarkdownString
import com.soyle.stories.desktop.view.scene.sceneSetting.item.SceneSettingItemLocaleMock
import com.soyle.stories.desktop.view.scene.sceneSetting.useLocationButton.UseLocationButtonLocaleMock
import com.soyle.stories.prose.proseEditor.ProseEditorView
import com.soyle.stories.scene.setting.list.SceneSettingItemListLocale
import com.soyle.stories.scene.setting.list.item.SceneSettingItemLocale
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButtonLocale
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Parent
import org.fxmisc.richtext.TextExt
import tornadofx.*

class SceneSettingItemListLocaleMock(
    override val useLocationsAsSceneSetting: StringProperty = stringProperty("Use Locations as Scene Setting"),
    override val noLocationUsedInSceneMessage: ObservableMarkdownString = ObservableMarkdownString(stringProperty(
        """
            When you @mention a location in the scene, you can choose to use the location as a setting in the scene.
            However, you can also choose to use a location as a setting in this scene by clicking the button below.
        """.trimIndent()
    )),
    override val sceneSettings: StringProperty = stringProperty("Scene Settings"),
    override val useLocation: UseLocationButtonLocale = UseLocationButtonLocaleMock(),
    override val item: SceneSettingItemLocale  = SceneSettingItemLocaleMock(),

    override val failedToLoadUsedLocations: StringProperty = stringProperty("Failed to Load Used Locations"),
    override val retry: StringProperty = stringProperty("Retry")
) : SceneSettingItemListLocale