package com.soyle.stories.desktop.view.scene.sceneSetting.list

import com.soyle.stories.common.components.text.FieldLabel
import com.soyle.stories.common.components.text.SectionTitle
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.components.text.ToolTitle
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.domain.location.Location
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView
import com.soyle.stories.scene.setting.list.SceneSettingItemList
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButton
import javafx.scene.control.Button
import javafx.scene.control.ProgressIndicator
import javafx.scene.image.ImageView
import javafx.scene.text.TextFlow
import tornadofx.Stylesheet

class `Scene Setting Item List Access`(val list: SceneSettingItemList) : NodeAccess<SceneSettingItemList>(list) {

    companion object {

        fun SceneSettingItemList.access(op: `Scene Setting Item List Access`.() -> Unit = {}): `Scene Setting Item List Access` {
            return `Scene Setting Item List Access`(this).apply(op)
        }
        fun SceneSettingItemList.drive(op: `Scene Setting Item List Access`.() -> Unit)
        {
            access {
                interact { op() }
            }
        }
    }

    val loadingIndicator: ProgressIndicator? by temporaryChild(Stylesheet.progressIndicator)

    val errorMessage: FieldLabel? by temporaryChild(Stylesheet.error)

    val retryButton: Button? by temporaryChild(SceneSettingItemList.Styles.retry)

    val inviteImage: ImageView? by temporaryChild(Stylesheet.imageView)

    val inviteTitle: ToolTitle? by temporaryChild(TextStyles.toolLevelTitle)

    val toolTitle: SectionTitle? by temporaryChild(TextStyles.sectionTitle)

    val inviteMessage: TextFlow? by temporaryChild(TextStyles.fieldLabel)

    val useLocationButton: UseLocationButton? by temporaryChild(UseLocationButton.Styles.useLocation)

    val sceneSettingItems: List<SceneSettingItemView> by children(SceneSettingItemView.Styles.sceneSettingItem)

    fun getSceneSettingItem(locationId: Location.Id): SceneSettingItemView? =
        sceneSettingItems.find { it.id == locationId.toString() }

    fun getSceneSettingItemByName(settingName: String): SceneSettingItemView? =
        sceneSettingItems.find { it.text == settingName }
}