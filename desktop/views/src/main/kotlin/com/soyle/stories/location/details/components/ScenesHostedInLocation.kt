package com.soyle.stories.location.details.components

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Companion.chip
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.surfaces.elevated
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.location.details.LocationDetailsActions
import com.soyle.stories.location.details.LocationDetailsLocale
import com.soyle.stories.location.details.models.HostedSceneItemModel
import com.soyle.stories.location.details.models.LocationDetailsModel
import com.soyle.stories.location.details.LocationDetailsStyles
import com.soyle.stories.location.details.components.HostSceneButton.Companion.hostSceneButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.ReadOnlyListProperty
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import tornadofx.*

class ScenesHostedInLocation(
    private val state: LocationDetailsModel.Loaded,
    private val actions: LocationDetailsActions,
    private val locale: LocationDetailsLocale
) : VBox() {

    companion object {
        @ViewBuilder
        fun Parent.scenesHostedInLocation(
            state: LocationDetailsModel.Loaded,
            actions: LocationDetailsActions,
            locale: LocationDetailsLocale
        ) = opcr(this, ScenesHostedInLocation(state, actions, locale))
    }

    private val doesNotHaveScenesProperty = state.hostedScenes.emptyProperty()
    private val hasSceneProperty = doesNotHaveScenesProperty.not()

    private val header = hbox {
        addClass(Stylesheet.header)
        dynamicContent(hasSceneProperty) {
            sectionTitle(locale.scenesHostedInLocation).apply {
                hgrow = Priority.ALWAYS
            }
            if (hasSceneProperty.value) hostSceneButton(state.availableScenesToHost, actions, locale)
        }
    }

    init {
        addClass(LocationDetailsStyles.hostedScenesSection)
        toggleClass(LocationDetailsStyles.hasScenes, hasSceneProperty)
    }

    init {
        dynamicContent(hasSceneProperty) {
            add(header)
            val body = if (hasSceneProperty.value) hostedSceneItemList(state.hostedScenes)
                       else hostSceneInvitation()
            body.apply {
                vgrow = Priority.ALWAYS
            }
        }
    }

    @ViewBuilder
    private fun Parent.hostSceneInvitation(): Node {
        return vbox {
            addClass(LocationDetailsStyles.invitation)

            fieldLabel(locale.hostSceneInLocationInvitationMessage).apply {
                minHeight = Region.USE_PREF_SIZE
            }
            hostSceneButton(state.availableScenesToHost, actions, locale)
        }
    }

    @ViewBuilder
    private fun Parent.hostedSceneItemList(hostedScenes: ReadOnlyListProperty<HostedSceneItemModel>): Node {
        return flowpane {
            addClass(LocationDetailsStyles.itemList)
            bindChildren(hostedScenes.value) {
                hostedSceneItem(it)
            }
        }
    }

    @ViewBuilder
    private fun Parent.hostedSceneItem(model: HostedSceneItemModel): Node
    {
        return chip(model.name).apply {
            addClass(LocationDetailsStyles.hostedSceneItem)
            id = model.id.toString()
            asSurface { inheritedElevationProperty().bind(this@ScenesHostedInLocation.elevated().absoluteElevationProperty()) }
            deleteGraphic = MaterialIconView(MaterialIcon.DELETE)
            onDelete { actions.removeScene(model.id) }
        }
    }

}