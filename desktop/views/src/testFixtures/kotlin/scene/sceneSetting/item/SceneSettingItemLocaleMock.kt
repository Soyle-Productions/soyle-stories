package com.soyle.stories.desktop.view.scene.sceneSetting.item

import com.soyle.stories.scene.setting.list.item.SceneSettingItemLocale
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import tornadofx.stringProperty

class SceneSettingItemLocaleMock(
    override val locationHasBeenRemovedFromStory: StringProperty = stringProperty("This location has been removed from the story."),
    override val removeFromScene: StringProperty = stringProperty("Remove from Scene"),
    override val replaceWith: StringProperty = stringProperty("Replace With..."),
    override val loading: StringProperty = stringProperty("Loading..."),
    override val allExistingLocationsInProjectHaveBeenUsed: StringProperty = stringProperty("All existing locations in project have been used"),
) : SceneSettingItemLocale