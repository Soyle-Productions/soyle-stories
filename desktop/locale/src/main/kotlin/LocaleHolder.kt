package com.soyle.stories.desktop.locale

import com.soyle.stories.Locale
import com.soyle.stories.character.CharacterLocale
import com.soyle.stories.character.delete.ConfirmDeleteCharacterPromptLocale
import com.soyle.stories.character.delete.RemoveCharacterLocale
import com.soyle.stories.character.delete.ramifications.RemoveCharacterRamificationsReportLocale
import com.soyle.stories.common.markdown.ObservableMarkdownString
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.LocationLocale
import com.soyle.stories.location.createLocationDialog.CreateLocationDialogLocale
import com.soyle.stories.location.details.LocationDetailsLocale
import com.soyle.stories.ramifications.RamificationsLocale
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptLocale
import com.soyle.stories.scene.SceneLocale
import com.soyle.stories.scene.characters.SceneCharactersLocale
import com.soyle.stories.scene.characters.list.CharactersInSceneListLocale
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemLocale
import com.soyle.stories.scene.characters.remove.ConfirmRemoveCharacterFromScenePromptLocale
import com.soyle.stories.scene.characters.tool.SceneCharactersToolLocale
import com.soyle.stories.scene.delete.DeleteSceneConfirmationPromptLocale
import com.soyle.stories.scene.delete.RemoveSceneLocale
import com.soyle.stories.scene.effects.InheritedMotivationChangedLocale
import com.soyle.stories.scene.outline.SceneOutlineLocale
import com.soyle.stories.scene.outline.remove.ConfirmRemoveStoryEventFromScenePromptLocale
import com.soyle.stories.scene.outline.remove.RemoveStoryEventFromSceneLocale
import com.soyle.stories.scene.outline.remove.ramifications.UncoverStoryEventRamificationsReportLocale
import com.soyle.stories.scene.setting.SceneSettingToolLocale
import com.soyle.stories.scene.setting.list.SceneSettingItemListLocale
import com.soyle.stories.scene.setting.list.item.SceneSettingItemLocale
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButtonLocale
import com.soyle.stories.storyevent.StoryEventLocale
import com.soyle.stories.storyevent.character.StoryEventCharactersLocale
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventLocale
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventPromptLocale
import com.soyle.stories.storyevent.character.remove.ramifications.RemoveCharacterFromStoryEventRamificationsReportLocale
import com.soyle.stories.storyevent.remove.RemoveStoryEventConfirmationPromptLocale
import com.soyle.stories.storyevent.remove.RemoveStoryEventLocale
import com.soyle.stories.storyevent.remove.ramifications.RemoveStoryEventFromStoryRamificationsReportLocale
import com.soyle.stories.usecase.scene.character.effects.CharacterInSceneEffect
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.scene.character.effects.IncludedCharacterNotInProject
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.StringExpression
import javafx.beans.value.ObservableValue
import tornadofx.get
import tornadofx.getValue
import tornadofx.objectProperty
import tornadofx.stringProperty
import java.text.MessageFormat
import java.util.*

class LocaleHolder : Locale {

    private val selectedLocaleTypeProperty = objectProperty<java.util.Locale>()
    private val selectedLocaleType by selectedLocaleTypeProperty

    private fun currentBundle(module: String) = createObjectBinding({
        ResourceBundle.getBundle(module, selectedLocaleType ?: java.util.Locale.ENGLISH)!!
    }, selectedLocaleTypeProperty)

    private operator fun ObjectBinding<ResourceBundle>.get(key: String) =
        createStringBinding({ value[key] }, this)

    private fun ObjectBinding<ResourceBundle>.format(key: String, vararg dependencies: ObservableValue<*>) =
        createStringBinding({
            val currentValue = value
            val observableKeyedValue = currentValue[key]
            val formatted = MessageFormat.format(
                observableKeyedValue,
                *(dependencies.map { it.value }.toTypedArray())
            )
            formatted
        }, this, *dependencies)

    private fun ObservableValue<String>.asMarkdown(): ObservableMarkdownString =
        ObservableMarkdownString(this)

    private val characterBundle = currentBundle("character/character")
    private val locationBundle = currentBundle("location/location")
    private val sceneBundle = currentBundle("scene/scene")
    private val storyEventBundle = currentBundle("story event/story event")
    private val ramificationsBundle = currentBundle("ramifications/ramifications")

    override val characters: CharacterLocale = object : CharacterLocale {
        override val remove: RemoveCharacterLocale = object : RemoveCharacterLocale {
            override val confirmation: ConfirmDeleteCharacterPromptLocale =
                object : ConfirmDeleteCharacterPromptLocale {
                    override fun confirmDeleteCharacterTitle(): ObservableValue<String> =
                        characterBundle["remove.prompt.title"]

                    override fun confirmDeleteCharacterMessage(characterName: ObservableValue<String>): ObservableValue<String> =
                        characterBundle.format("remove.prompt.message", characterName)

                    override fun remove(): ObservableValue<String> = characterBundle["remove.confirm"]

                }
            override val ramifications: RemoveCharacterRamificationsReportLocale =
                object : RemoveCharacterRamificationsReportLocale {
                    override fun removeCharacterFromStoryRamifications(): ObservableValue<String> =
                        characterBundle["remove.ramifications.title"]

                    override fun characterInSceneEffectMessage(effect: CharacterInSceneEffect): ObservableMarkdownString {
                        return effectMessage(effect) ?: error("unexpected effect type ${effect::class.simpleName}")
                    }

                    override fun noCharacterName(): ObservableValue<String> = characterBundle["unknownCharacter"]

                }
        }
    }

    override val scenes: SceneLocale = object : SceneLocale {
        val currentBundle: ObjectBinding<ResourceBundle> = sceneBundle

        override val remove: RemoveSceneLocale = object : RemoveSceneLocale {
            override val prompt: DeleteSceneConfirmationPromptLocale = object : DeleteSceneConfirmationPromptLocale {
                override val remove: ObservableValue<String> = sceneBundle["remove.prompt.confirm"]
                override val title: ObservableValue<String> = sceneBundle["remove.prompt.title"]

                override fun message(sceneName: ObservableValue<String>): ObservableValue<String> =
                    sceneBundle.format("remove.prompt.confirm", sceneName)
            }
        }

        override val tool: SceneLocale.CommonToolLocale = object : SceneLocale.CommonToolLocale {
            override fun selectedScene(sceneName: ObservableValue<String>): ObservableValue<String> {
                return currentBundle.format("tool.selectedScene", sceneName)
            }

            override val noSceneSelected: ObservableValue<String> = currentBundle["tool.noSceneSelected"]
        }
        private val commonTool = tool

        override val effects: InheritedMotivationChangedLocale = object : InheritedMotivationChangedLocale {
            override fun willNoLongerHaveMotivationInScene(
                motivation: ObservableValue<String>,
                sceneName: ObservableValue<String>,
                sceneId: Scene.Id
            ): ObservableMarkdownString =
                sceneBundle.format(
                    "effects.willNoLongerHaveMotivationInScene",
                    motivation,
                    sceneName,
                    stringProperty("Scene#${sceneId.uuid}")
                )
                    .asMarkdown()

            override fun willInheritMotivation(
                motivation: ObservableValue<String>,
                sourceSceneName: ObservableValue<String>,
                sourceSceneId: Scene.Id,
                sceneName: ObservableValue<String>,
                sceneId: Scene.Id
            ): ObservableMarkdownString =
                sceneBundle.format(
                    "effects.willInheritMotivation",
                    motivation,
                    sceneName,
                    stringProperty("Scene#${sceneId.uuid}"),
                    sourceSceneName,
                    stringProperty("Scene#${sourceSceneId.uuid}")
                )
                    .asMarkdown()
        }

        override val characters: SceneCharactersLocale = object : SceneCharactersLocale {
            override val list: CharactersInSceneListLocale = object : CharactersInSceneListLocale {
                override val addCharacter: StringExpression = currentBundle["characters.tool.addCharacter"]
                override val options: StringExpression = currentBundle["characters.tool.options"]
                override val noCharactersInScene_inviteMessage: ObservableMarkdownString =
                    currentBundle["characters.tool.noCharactersInScene.inviteMessage"].asMarkdown()
                override val toolTitle: ObservableValue<String> = currentBundle["characters.tool.title"]

                override val item: CharacterInSceneItemLocale = object : CharacterInSceneItemLocale {
                    override val role: CharacterInSceneItemLocale.Roles = object : CharacterInSceneItemLocale.Roles {
                        override val incitingCharacter: ObservableValue<String> =
                            currentBundle["characters.list.item.role.incitingCharacter"]
                        override val opponentCharacter: ObservableValue<String> =
                            currentBundle["characters.list.item.role.opponentCharacter"]
                    }
                    override val warning: CharacterInSceneItemLocale.Warnings =
                        object : CharacterInSceneItemLocale.Warnings {
                            override val characterNotInvolvedInAnyStoryEvents: ObservableValue<String> =
                                currentBundle["characters.list.item.warning.characterNotInvolvedInAnyStoryEvents"]
                            override val characterRemovedFromStory: ObservableValue<String> =
                                currentBundle["characters.list.item.warning.characterRemovedFromStory"]
                        }
                    override val menu: CharacterInSceneItemLocale.ContextMenu =
                        object : CharacterInSceneItemLocale.ContextMenu {
                            override val edit: StringExpression = currentBundle["characters.list.item.menu.edit"]
                            override val removeCharacterFromScene: StringExpression =
                                currentBundle["characters.list.item.menu.removeCharacterFromScene"]
                            override val toggleIncitingCharacter: StringExpression =
                                currentBundle["characters.list.item.menu.toggleIncitingCharacter"]
                            override val toggleOpponentCharacter: StringExpression =
                                currentBundle["characters.list.item.menu.toggleOpponentCharacter"]
                        }
                }
            }
            private val characterList
                get() = list
            override val tool: SceneCharactersToolLocale =
                object : SceneCharactersToolLocale, SceneLocale.CommonToolLocale by commonTool {
                    override val sceneCharactersTitle: StringExpression = currentBundle["characters.tool.title"]
                    override val noSceneSelectedInviteMessage: ObservableMarkdownString =
                        currentBundle["characters.tool.noCharactersInScene.inviteMessage"].asMarkdown()
                    override val list: CharactersInSceneListLocale
                        get() = characterList
                }
            override val remove: ConfirmRemoveCharacterFromScenePromptLocale =
                object : ConfirmRemoveCharacterFromScenePromptLocale {
                    override fun remove(): ObservableValue<String> = currentBundle["characters.remove"]

                    override fun confirmRemoveCharacterFromSceneTitle(): ObservableValue<String> =
                        currentBundle["characters.remove.confirmation.title"]

                    override fun confirmRemoveCharacterFromSceneMessage(
                        characterName: ObservableValue<String>,
                        sceneName: ObservableValue<String>
                    ): ObservableValue<String> =
                        currentBundle.format("characters.remove.confirmation.message", characterName, sceneName)
                }
        }
        override val locations: SceneSettingToolLocale = object : SceneSettingToolLocale {
            override val list: SceneSettingItemListLocale = object : SceneSettingItemListLocale {
                override val retry: ObservableValue<String> = currentBundle["locations.list.retryLoad"]
                override val failedToLoadUsedLocations: ObservableValue<String> =
                    currentBundle["locations.list.failedToLoad"]
                override val useLocationsAsSceneSetting: ObservableValue<String> =
                    currentBundle["locations.tool.useLocations"]
                override val noLocationUsedInSceneMessage: ObservableMarkdownString =
                    currentBundle["locations.tool.noLocationsUsed.inviteMessage"].asMarkdown()
                override val sceneSettings: ObservableValue<String> = currentBundle["locations.tool.title"]

                override val useLocation: UseLocationButtonLocale = object : UseLocationButtonLocale {
                    override val loading: ObservableValue<String> = currentBundle["locations.tool.loading"]
                    override val useLocation: ObservableValue<String> = currentBundle["locations.tool.useLocation"]
                    override val createLocation: ObservableValue<String> =
                        currentBundle["locations.tool.createNewLocation"]
                    override val allExistingLocationsInProjectHaveBeenUsed: ObservableValue<String> =
                        currentBundle["locations.tool.loading"]
                }
                override val item: SceneSettingItemLocale = object : SceneSettingItemLocale {
                    override val allExistingLocationsInProjectHaveBeenUsed: ObservableValue<String> =
                        currentBundle["locations.tool.useLocation.allLocationsUsed"]
                    override val loading: ObservableValue<String> =
                        currentBundle["locations.tool.loading"]
                    override val locationHasBeenRemovedFromStory: ObservableValue<String> =
                        currentBundle["locations.list.item.warning.locationRemovedFromStory"]
                    override val removeFromScene: ObservableValue<String> =
                        currentBundle["locations.list.item.menu.removeFromScene"]
                    override val replaceWith: ObservableValue<String> =
                        currentBundle["locations.list.item.menu.replaceWith"]
                }

            }
            override val sceneSettingItemListLocale: SceneSettingItemListLocale
                get() = list

            override val noSceneSelected: ObservableValue<String> =
                currentBundle["tool.noSceneSelected"]
            override val noSceneSelected_inviteMessage: ObservableMarkdownString =
                sceneBundle["locations.tool.noSceneSelected.inviteMessage"].asMarkdown()
            override val sceneSettingToolTitle: ObservableValue<String> =
                currentBundle["locations.tool.title"]

            override fun selectedScene(sceneName: ObservableValue<String>): ObservableValue<String> =
                currentBundle.format("tool.selectedScene", sceneName)
        }
        override val storyEvents: SceneOutlineLocale = object : SceneOutlineLocale {
            override val remove: RemoveStoryEventFromSceneLocale = object : RemoveStoryEventFromSceneLocale {
                override val prompt: ConfirmRemoveStoryEventFromScenePromptLocale =
                    object : ConfirmRemoveStoryEventFromScenePromptLocale {
                        override fun confirmRemoveStoryEventFromScene(): ObservableValue<String> =
                            sceneBundle["storyEvents.remove.prompt.title"]

                        override fun remove(): ObservableValue<String> =
                            sceneBundle["storyEvents.remove.prompt.confirm"]

                        override fun areYouSureYouWantToRemoveTheStoryEventFromTheScene(
                            storyEventName: ObservableValue<String>,
                            sceneName: ObservableValue<String>
                        ): ObservableValue<String> = sceneBundle.format(
                            "storyEvents.remove.prompt.message",
                            storyEventName,
                            sceneName
                        )
                    }
                override val ramifications: UncoverStoryEventRamificationsReportLocale =
                    object : UncoverStoryEventRamificationsReportLocale {
                        override fun noCharacterName(): ObservableValue<String> = characterBundle["unknownCharacter"]

                        override fun uncoverStoryEventRamifications(): ObservableValue<String> =
                            sceneBundle["storyEvents.remove.ramifications.title"]

                        override fun implicitCharacterRemovedFromSceneMessage(
                            effect: ImplicitCharacterRemovedFromScene
                        ): ObservableMarkdownString =
                            effectMessage(effect)!!
                    }
            }
        }
    }

    override val locations: LocationLocale = object : LocationLocale {
        val currentBundle = locationBundle

        override val create: CreateLocationDialogLocale = object : CreateLocationDialogLocale {
            override val newLocation: ObservableValue<String> = currentBundle["create.prompt.title"]
            override val description: ObservableValue<String> = currentBundle["description"]
            override val name: ObservableValue<String> = currentBundle["name"]
            override val pleaseProvideALocationName: ObservableValue<String> =
                currentBundle["create.prompt.name.warning.pleaseProvide"]
            override val cancel: ObservableValue<String> = currentBundle["create.prompt.cancel"]
            override val create: ObservableValue<String> = currentBundle["create.prompt.create"]
        }

        override val details: LocationDetailsLocale = object : LocationDetailsLocale {
            override val allExistingScenesInProjectHaveBeenHosted: ObservableValue<String> =
                currentBundle["scenes.host.allScenesAlreadyUsed"]
            override val createScene: ObservableValue<String> =
                sceneBundle["create"]
            override val description: ObservableValue<String> = currentBundle["description"]
            override val hostScene: ObservableValue<String> =
                currentBundle["scenes.host"]
            override val hostSceneInLocationInvitationMessage: ObservableValue<String> =
                currentBundle["scenes.noHostedScenes.invitationMessage"]
            override val loading: ObservableValue<String> =
                currentBundle["scenes.loading"]
            override val scenesHostedInLocation: ObservableValue<String> =
                currentBundle["detail.tool.title"]

            override fun locationDetailsToolName(locationName: ObservableValue<String>): ObservableValue<String> {
                return currentBundle.format("details.tool.title", locationName)
            }
        }
    }


    override val storyEvents: StoryEventLocale = object : StoryEventLocale {
        override val characters: StoryEventCharactersLocale = object : StoryEventCharactersLocale {
            override val remove: RemoveCharacterFromStoryEventLocale = object : RemoveCharacterFromStoryEventLocale {
                override val confirmation: RemoveCharacterFromStoryEventPromptLocale =
                    object : RemoveCharacterFromStoryEventPromptLocale {
                        override fun confirmRemoveCharacterFromStoryEvent(): ObservableValue<String> =
                            storyEventBundle["characters.remove.prompt.title"]

                        override fun remove(): ObservableValue<String> =
                            storyEventBundle["characters.remove.confirm"]

                        override fun areYouSureYouWantToRemoveTheCharacterFromTheStoryEvent(
                            characterName: ObservableValue<String>,
                            storyEventName: ObservableValue<String>
                        ): ObservableValue<String> =
                            storyEventBundle.format(
                                "characters.remove.prompt.message",
                                characterName,
                                storyEventName
                            )
                    }
                override val ramifications: RemoveCharacterFromStoryEventRamificationsReportLocale =
                    object : RemoveCharacterFromStoryEventRamificationsReportLocale {
                        override fun noCharacterName(): ObservableValue<String> = characterBundle["unknownCharacter"]

                        override fun characterInSceneEffectMessage(effect: CharacterInSceneEffect): ObservableMarkdownString {
                            return effectMessage(effect) ?: error("unexpected effect type ${effect::class.simpleName}")
                        }
                    }
            }
        }
        override val remove: RemoveStoryEventLocale = object : RemoveStoryEventLocale {
            override val prompt: RemoveStoryEventConfirmationPromptLocale =
                object : RemoveStoryEventConfirmationPromptLocale {
                    override fun confirmRemoveStoryEventFromProject(): ObservableValue<String> =
                        storyEventBundle["remove.prompt.title"]

                    override fun remove(): ObservableValue<String> = storyEventBundle["remove.prompt.confirm"]

                    override fun areYouSureYouWantToRemoveTheseStoryEventsFromTheProject(storyEventNames: List<String>): ObservableValue<String> =
                        storyEventBundle.format("remove.prompt.message", stringProperty(storyEventNames.toString()))
                }
            override val ramifications: RemoveStoryEventFromStoryRamificationsReportLocale =
                object : RemoveStoryEventFromStoryRamificationsReportLocale {
                    override fun noCharacterName(): ObservableValue<String> = characterBundle["unknownCharacter"]

                    override fun implicitCharacterRemovedFromSceneMessage(
                        effect: ImplicitCharacterRemovedFromScene
                    ): ObservableMarkdownString =
                        effectMessage(effect)!!
                }
        }
    }

    private fun effectMessage(effect: Any): ObservableMarkdownString? {
        return when (effect) {
            is ImplicitCharacterRemovedFromScene -> ramificationsBundle.format(
                "scene.character.effect.implicitCharacterRemovedFromScene",
                stringProperty(effect.characterName),
                stringProperty(effect.sceneName),
                stringProperty(effect.scene.uuid.toString())
            ).asMarkdown()
            is IncludedCharacterNotInProject -> ramificationsBundle.format(
                "scene.character.effect.includedCharacterNotInProject",
                stringProperty(effect.characterName),
                stringProperty(effect.sceneName),
                stringProperty(effect.scene.uuid.toString())
            ).asMarkdown()
            else -> null
        }
    }

    override val ramifications: RamificationsLocale = object : RamificationsLocale {

        override val confirmation: ConfirmationPromptLocale = object : ConfirmationPromptLocale {
            override val ramifications: ObservableValue<String> = ramificationsBundle["ramifications"]

            override val doNotShowDialogAgain: ObservableValue<String> =
                ramificationsBundle["confirmation.doNotShowAgain"]

            override val confirm: ObservableValue<String> = ramificationsBundle["confirmation.confirm"]

            override val cancel: ObservableValue<String> = ramificationsBundle["confirmation.cancel"]
        }
    }

}