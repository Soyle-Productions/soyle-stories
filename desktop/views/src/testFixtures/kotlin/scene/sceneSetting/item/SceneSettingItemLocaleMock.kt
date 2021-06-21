package com.soyle.stories.desktop.view.scene.sceneSetting.item

import com.soyle.stories.scene.setting.list.item.SceneSettingItemLocale
import javafx.beans.property.StringProperty
import tornadofx.stringProperty

class SceneSettingItemLocaleMock(
    override val locationHasBeenRemovedFromStory: StringProperty = stringProperty("This location has been removed from the story.")
) : SceneSettingItemLocale