package com.soyle.stories.scene.sceneSetting

import com.soyle.stories.common.components.dataDisplay.chip.Chip.Companion.chip
import com.soyle.stories.location.items.LocationItemViewModel
import javafx.scene.Parent
import tornadofx.addClass


fun Parent.SceneSettingChip(
    locationItem: LocationItemViewModel,
    onRemoveLocation: (LocationItemViewModel) -> Unit
) = chip(locationItem.name, onDelete = { onRemoveLocation(locationItem) }) {
    node.id = locationItem.id.toString()
}.node