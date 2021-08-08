package com.soyle.stories.desktop.view.scene.sceneSetting

import com.soyle.stories.common.components.layouts.LayoutStyles
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.scene.setting.SceneSettingToolRoot
import com.soyle.stories.scene.setting.list.SceneSettingItemList
import javafx.scene.control.Labeled
import javafx.scene.image.ImageView
import javafx.scene.text.TextFlow
import tornadofx.Stylesheet
import tornadofx.uiComponent

class `Scene Setting Tool Root Access`(val root: SceneSettingToolRoot) : NodeAccess<SceneSettingToolRoot>(root) {

    companion object {

        fun SceneSettingToolRoot.access(
            op: `Scene Setting Tool Root Access`.() -> Unit = {}
        ): `Scene Setting Tool Root Access` =
            `Scene Setting Tool Root Access`(this).apply(op)
    }

    fun isFocusedOn(scene: com.soyle.stories.domain.scene.Scene): Boolean
    {
        return root.selectedScene == scene.id
    }

    val selectedSceneLabel: Labeled by mandatoryChild(SceneSettingToolRoot.Styles.sceneName)

    val inviteImage: ImageView? by temporaryChild(Stylesheet.imageView)

    val inviteTitle: Labeled? by temporaryChild(TextStyles.toolLevelTitle)

    val inviteMessage: TextFlow? by temporaryChild(LayoutStyles.inviteMessage)

    val list: SceneSettingItemList? by temporaryChild(SceneSettingItemList.Styles.sceneSettingItemList)

}