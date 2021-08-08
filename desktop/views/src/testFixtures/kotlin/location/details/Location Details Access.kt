package com.soyle.stories.desktop.view.location.details

import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.desktop.view.location.details.`Host Scene Button Access`.Companion.access
import com.soyle.stories.location.details.components.HostSceneButton
import com.soyle.stories.location.details.components.LocationDescription
import com.soyle.stories.location.details.components.LocationDetailsRoot
import com.soyle.stories.location.details.components.ScenesHostedInLocation
import com.soyle.stories.location.details.LocationDetailsStyles
import com.soyle.stories.location.details.LocationDetailsView
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Labeled
import javafx.scene.control.TextInputControl
import org.testfx.api.FxRobot

class `Location Details Access`(private val root: LocationDetailsRoot) : FxRobot() {

    companion object {
        fun LocationDetailsRoot.access(
            accessor: `Location Details Access`.() -> Unit = {}
        ): `Location Details Access` =
            `Location Details Access`(this).apply(accessor)

        fun LocationDetailsView.access() = (root as LocationDetailsRoot).access()
        fun <T> LocationDetailsView.drive(op: `Location Details Access`.() -> T): T
        {
            var result: T? = null
            val access = access()
            access.interact { result = access.op() }
            return result as T
        }

    }

    val loadingIndicator: Node?
        get() = from(root.content).lookup(".progress-indicator").queryAll<Node>().firstOrNull()

    val descriptionField: LocationDescription?
        get() = from(root.content).lookup(".${LocationDetailsStyles.description.name}").queryAll<LocationDescription>().firstOrNull()

    val LocationDescription.title: Labeled?
        get() = from(this).lookup(".label").queryAll<Labeled>().firstOrNull()

    val LocationDescription.input: TextInputControl?
        get() = from(this).lookup(".text-input").queryAll<TextInputControl>().firstOrNull()

    val hostedScenesList: ScenesHostedInLocation?
        get() = from(root.content).lookup(".${LocationDetailsStyles.hostedScenesSection.name}").queryAll<ScenesHostedInLocation>().firstOrNull()

    val ScenesHostedInLocation.title: Labeled?
        get() = from(this).lookup(".label").queryAll<Labeled>().firstOrNull()

    val ScenesHostedInLocation.list: Parent?
        get() = from(this).lookup(".${LocationDetailsStyles.itemList.name}").queryAll<Parent>().firstOrNull()

    val Parent.hostedSceneItems: List<Chip>
        get() = from(this).lookup(".${LocationDetailsStyles.hostedSceneItem.name}").queryAll<Chip>().toList()

    val ScenesHostedInLocation.invitation: Node?
        get() = from(this).lookup(".${LocationDetailsStyles.invitation.name}").queryAll<Node>().firstOrNull()

    val hostSceneButton: `Host Scene Button Access`?
        get() = from(root.content).lookup("#${LocationDetailsStyles.addScene.name}").queryAll<HostSceneButton>().firstOrNull()?.access()

}