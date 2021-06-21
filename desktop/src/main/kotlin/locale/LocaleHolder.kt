package com.soyle.stories.desktop.config.locale

import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.desktop.locale.SoyleMessageBundle
import com.soyle.stories.location.details.LocationDetailsLocale
import com.soyle.stories.prose.proseEditor.ProseEditorView
import com.soyle.stories.scene.setting.SceneSettingToolLocale
import com.soyle.stories.scene.setting.list.SceneSettingItemListLocale
import com.soyle.stories.scene.setting.list.item.SceneSettingItemLocale
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButtonLocale
import javafx.beans.value.ObservableValue
import javafx.scene.Parent
import tornadofx.*

class LocaleHolder(bundle: SoyleMessageBundle) : LocationDetailsLocale, SceneSettingToolLocale, SceneSettingItemListLocale, SceneSettingItemLocale, UseLocationButtonLocale {

    private val currentLocale = objectProperty<SoyleMessageBundle>(bundle)

    fun holdLocale(bundle: SoyleMessageBundle) {
        currentLocale.value = bundle
    }

    override val description: ObservableValue<String> = currentLocale.stringBinding { it!!.description }
    override val scenesHostedInLocation: ObservableValue<String> =
        currentLocale.stringBinding { it!!.scenesHostedInLocation }
    override val hostScene: ObservableValue<String> = currentLocale.stringBinding { it!!.hostScene }
    override val loading: ObservableValue<String> = currentLocale.stringBinding { it!!.loading }
    override val createScene: ObservableValue<String> = currentLocale.stringBinding { it!!.createScene }
    override val hostSceneInLocationInvitationMessage: ObservableValue<String> =
        currentLocale.stringBinding { it!!.hostSceneInLocationInvitationMessage }
    override val allExistingScenesInProjectHaveBeenHosted: ObservableValue<String> =
        currentLocale.stringBinding { it!!.allExistingScenesInProjectHaveBeenHosted }

    override fun locationDetailsToolName(locationName: String): ObservableValue<String> =
        currentLocale.stringBinding { it!!.locationDetailsToolName.format(it.locale, locationName) }

    override val noSceneSelectedInviteMessage: ObservableValue<Parent.() -> Unit> = currentLocale.objectBinding { bundle ->
        { parent: Parent ->
            bundle!!.noSceneSelectedInviteMessage.forEach {
                createDynamicMessage(parent, it)
            }
        }
    } as ObservableValue<Parent.() -> Unit>
    override val noSceneSelected: ObservableValue<String> = currentLocale.stringBinding { it!!.noSceneSelected }
    override val sceneSettingItemListLocale: SceneSettingItemListLocale = this
    override val sceneSettingToolTitle: ObservableValue<String> = currentLocale.stringBinding { it!!.sceneSettingToolTitle }
    override val selectedScene: ObservableValue<(String) -> String> =
        objectBinding(currentLocale) {
            { sceneName: String ->
                get().selectedScene.format(currentLocale.get().locale, sceneName)
            }
        } as ObservableValue<(String) -> String>
    override val useLocationsAsSceneSetting: ObservableValue<String> = currentLocale.stringBinding { it!!.useLocationsAsSceneSetting }
    override val failedToLoadUsedLocations: ObservableValue<String> = currentLocale.stringBinding { it!!.failedToLoadUsedLocations }
    override val retry: ObservableValue<String> = currentLocale.stringBinding { it!!.retry }
    override val sceneSettings: ObservableValue<String> = currentLocale.stringBinding { it!!.sceneSettings }
    override val useLocation: ObservableValue<String> = currentLocale.stringBinding { it!!.useLocation }
    override val allExistingLocationsInProjectHaveBeenUsed: ObservableValue<String> = currentLocale.stringBinding { it!!.allExistingLocationsInProjectHaveBeenUsed }
    override val createLocation: ObservableValue<String> = currentLocale.stringBinding { it!!.createLocation }
    override val locationHasBeenRemovedFromStory: ObservableValue<String> = currentLocale.stringBinding { it!!.locationHasBeenRemovedFromStory }
    override val noLocationUsedInSceneMessage: ObservableValue<Parent.() -> Unit> = currentLocale.objectBinding { bundle ->
        { parent: Parent ->
            bundle!!.noLocationUsedInSceneMessage.forEach {
                createDynamicMessage(parent, it)
            }
        }
    } as ObservableValue<Parent.() -> Unit>

    private fun createDynamicMessage(parent: Parent, segment: SoyleMessageBundle.MessageSegment) {
        when (segment) {
            is SoyleMessageBundle.MessageSegment.Text -> parent.text(segment.message)
            is SoyleMessageBundle.MessageSegment.Link -> parent.hyperlink(segment.message)
            is SoyleMessageBundle.MessageSegment.Warning -> parent.label(segment.message) { addClass(TextStyles.warning) }
            is SoyleMessageBundle.MessageSegment.Mention -> parent.label(segment.message) { addClass(ProseEditorView.Styles.mention) }
        }
    }
}