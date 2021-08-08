package com.soyle.stories.scene.setting.list.item

import javafx.beans.value.ObservableValue

interface SceneSettingItemLocale {

    val locationHasBeenRemovedFromStory: ObservableValue<String>
    val removeFromScene: ObservableValue<String>
    val replaceWith: ObservableValue<String>
    val loading: ObservableValue<String>
    val allExistingLocationsInProjectHaveBeenUsed: ObservableValue<String>

}