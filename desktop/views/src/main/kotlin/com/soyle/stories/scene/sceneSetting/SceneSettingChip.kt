package com.soyle.stories.scene.sceneSetting

import com.soyle.stories.common.components.chip
import com.soyle.stories.location.items.LocationItemViewModel
import javafx.scene.Parent
import tornadofx.addClass
import tornadofx.toProperty


fun Parent.SceneSettingChip(
    locationItem: LocationItemViewModel,
    onRemoveLocation: (LocationItemViewModel) -> Unit
) = chip(locationItem.name.toProperty(), onDelete = { onRemoveLocation(locationItem) }) {
    node.id = locationItem.id.toString()
    node.addClass(SceneSettingView.Styles.sceneSettingChip)
}.node