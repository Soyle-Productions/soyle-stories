package com.soyle.stories.desktop.config.locale

import com.soyle.stories.character.delete.ConfirmDeleteCharacterPromptLocale
import com.soyle.stories.character.delete.ramifications.RemoveCharacterRamificationsReportLocale
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.common.markdown.ObservableMarkdownString
import com.soyle.stories.desktop.locale.SoyleMessageBundle
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogLocale
import com.soyle.stories.location.details.LocationDetailsLocale
import com.soyle.stories.prose.proseEditor.ProseEditorView
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptLocale
import com.soyle.stories.scene.SceneLocale
import com.soyle.stories.scene.characters.SceneCharactersLocale
import com.soyle.stories.scene.characters.tool.SceneCharactersToolLocale
import com.soyle.stories.scene.characters.list.CharactersInSceneListLocale
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemLocale
import com.soyle.stories.scene.characters.remove.ConfirmRemoveCharacterFromScenePromptLocale
import com.soyle.stories.scene.outline.remove.ConfirmRemoveStoryEventFromScenePromptLocale
import com.soyle.stories.scene.outline.remove.ramifications.UncoverStoryEventRamificationsReportLocale
import com.soyle.stories.scene.setting.SceneSettingToolLocale
import com.soyle.stories.scene.setting.list.SceneSettingItemListLocale
import com.soyle.stories.scene.setting.list.item.SceneSettingItemLocale
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButtonLocale
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventPromptLocale
import com.soyle.stories.storyevent.character.remove.ramifications.RemoveCharacterFromStoryEventRamificationsReportLocale
import com.soyle.stories.storyevent.remove.RemoveStoryEventConfirmationPromptLocale
import com.soyle.stories.storyevent.remove.ramifications.RemoveStoryEventFromStoryRamificationsReportLocale
import com.soyle.stories.theme.characterValueComparison.components.addValueButton.AddValueButtonLocale
import com.soyle.stories.theme.valueWeb.create.CreateValueWebFormLocale
import com.soyle.stories.theme.valueWeb.opposition.create.CreateOppositionValueFormLocale
import com.soyle.stories.usecase.scene.character.effects.CharacterInSceneEffect
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import javafx.beans.binding.StringBinding
import javafx.beans.binding.StringExpression
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.text.Text
import tornadofx.*
import java.text.MessageFormat
import java.util.*

class LocaleHolder(bundle: SoyleMessageBundle) :
    CreateLocationDialogLocale,
    AddValueButtonLocale,
    CreateOppositionValueFormLocale, CreateValueWebFormLocale,
    ConfirmRemoveCharacterFromScenePromptLocale,
    ConfirmDeleteCharacterPromptLocale, RemoveCharacterRamificationsReportLocale,
    RemoveCharacterFromStoryEventPromptLocale,
    RemoveCharacterFromStoryEventRamificationsReportLocale,
    ConfirmRemoveStoryEventFromScenePromptLocale,
    RemoveStoryEventConfirmationPromptLocale {

    private val currentLocale = objectProperty(bundle)

    @Suppress("unused")
    fun holdLocale(bundle: SoyleMessageBundle) {
        currentLocale.value = bundle
    }

    private inline fun get(crossinline extract: SoyleMessageBundle.() -> String): StringBinding = currentLocale.stringBinding { it?.extract().orEmpty() }
    fun ObservableValue<String>.asMarkdown() = ObservableMarkdownString(this)


    override val description: ObservableValue<String> = get { description }
    override val loading: ObservableValue<String> = get { loading }

    override val cancel: ObservableValue<String> = get { cancel }
    override val create: ObservableValue<String> = get { create }
    override val name: ObservableValue<String> = get { name }
    override val newLocation: ObservableValue<String> = get { newLocation }
    override val pleaseProvideALocationName: ObservableValue<String> =
        get { pleaseProvideALocationName }
    override val addValue: ObservableValue<String> = get { addValue }
    override val createNewValueWeb: ObservableValue<String> = get { createNewValueWeb }
    override val createOppositionValue: ObservableValue<String> =
        get { createOppositionValue }
    override val themeHasNoValueWebs: ObservableValue<String> = get { themeHasNoValueWebs }
    override val nameCannotBeBlank: ObservableValue<String> = get { nameCannotBeBlank }

    private fun createDynamicMessage(parent: Parent, segment: SoyleMessageBundle.MessageSegment) {
        when (segment) {
            is SoyleMessageBundle.MessageSegment.Text -> parent.text(segment.message)
            is SoyleMessageBundle.MessageSegment.Link -> parent.hyperlink(segment.message)
            is SoyleMessageBundle.MessageSegment.Warning -> parent.label(segment.message) { addClass(TextStyles.warning) }
            is SoyleMessageBundle.MessageSegment.Mention -> parent.label(segment.message) { addClass(ProseEditorView.Styles.mention) }
        }
    }

    override fun confirmRemoveCharacterFromSceneTitle(): ObservableValue<String> = get { confirmRemoveCharacterFromSceneTitle }
    override fun remove(): ObservableValue<String> = get { remove }

    override fun confirmRemoveCharacterFromSceneMessage(
        characterName: ObservableValue<String>,
        sceneName: ObservableValue<String>
    ): ObservableValue<String> = stringBinding(currentLocale, characterName, sceneName) {
        currentLocale.value.confirmRemoveCharacterFromSceneMessage.format(currentLocale.get().locale, characterName.value, sceneName.value)
    }

    override fun confirmDeleteCharacterMessage(characterName: ObservableValue<String>): ObservableValue<String> {
        return stringBinding(currentLocale, characterName) {
            val current = currentLocale.get()
            current.confirmRemoveCharacterFromStoryMesssage.format(current.locale, characterName.value)
        }
    }

    override fun confirmDeleteCharacterTitle(): ObservableValue<String> = get { confirmRemoveCharacterFromStory }

    override fun noCharacterName(): ObservableValue<String> = get { noCharacterName }

    override fun characterInSceneEffectMessage(effect: CharacterInSceneEffect): ObservableMarkdownString {
        val characterName = effect.characterName.orEmpty().toProperty()
        val sceneName = effect.sceneName.toProperty()
        return currentLocale.stringBinding(characterName, sceneName) {
            it!!.effect_scene_characters_sceneWillBeInconsistentDueToIncludedCharacterRemoved.format(it.locale, effect.character, characterName.value, effect.scene, sceneName.value)
        }.asMarkdown()
    }

    override fun confirmRemoveCharacterFromStoryEvent(): ObservableValue<String> = get { confirmRemoveCharacterFromStoryEvent }
    override fun confirmRemoveStoryEventFromScene(): ObservableValue<String> = get { confirmRemoveStoryEventFromScene }
    override fun areYouSureYouWantToRemoveTheCharacterFromTheStoryEvent(
        characterName: ObservableValue<String>,
        storyEventName: ObservableValue<String>
    ): ObservableValue<String> {
        return stringBinding(currentLocale, characterName, storyEventName) {
            val current = currentLocale.get()
            current.areYouSureYouWantToRemoveTheCharacterFromTheStoryEvent.format(current.locale, characterName.value, storyEventName.value)
        }
    }

    override fun areYouSureYouWantToRemoveTheStoryEventFromTheScene(
        storyEventName: ObservableValue<String>,
        sceneName: ObservableValue<String>
    ): ObservableValue<String> {
        return stringBinding(currentLocale, sceneName, storyEventName) {
            val current = currentLocale.get()
            current.areYouSureYouWantToRemoveTheStoryEventFromTheScene.format(current.locale, storyEventName.value, sceneName.value)
        }
    }

    override fun removeCharacterFromStoryRamifications(): ObservableValue<String> = get { removeCharacterFromStoryRamifications }
    override fun confirmRemoveStoryEventFromProject(): ObservableValue<String> = get { confirmRemoveStoryEventFromProject }

    override fun areYouSureYouWantToRemoveTheseStoryEventsFromTheProject(storyEventNames: List<String>): ObservableValue<String> {
        return get {
            areYouSureYouWantToRemoveStoryEventFromProject.format(locale, storyEventNames.size)
        }
    }
}