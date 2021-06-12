package com.soyle.stories.location.details.models

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList

sealed class LocationDetailsModel {

    object Loading : LocationDetailsModel()

    class Loaded(
        val description: ReadOnlyStringProperty,
        val hostedScenes: ReadOnlyListProperty<HostedSceneItemModel>,
        val availableScenesToHost: ReadOnlyListProperty<AvailableSceneToHostModel>
    ): LocationDetailsModel()

}