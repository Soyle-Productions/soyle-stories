package com.soyle.stories.desktop.config.scene

import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.characters.tool.SceneCharactersToolScope
import com.soyle.stories.scene.charactersInScene.assignRole.CharacterRoleInSceneChangedNotifier
import com.soyle.stories.scene.charactersInScene.assignRole.CharacterRoleInSceneChangedReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneNotifier
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneNotifier
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneReceiver
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.CharacterIncludedInSceneNotifier
import com.soyle.stories.scene.charactersInScene.involve.CharacterInvolvedInSceneNotifier
import com.soyle.stories.scene.charactersInScene.involve.CharacterInvolvedInSceneReceiver
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneNotifier
import com.soyle.stories.scene.charactersInScene.setDesire.CharacterDesireInSceneChangedNotifier
import com.soyle.stories.scene.charactersInScene.setDesire.CharacterDesireInSceneChangedReceiver
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.CharacterGainedMotivationInSceneNotifier
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.CharacterMotivationInSceneClearedNotifier
import com.soyle.stories.scene.charactersInScene.source.added.SourceAddedToCharacterInSceneNotifier
import com.soyle.stories.scene.charactersInScene.source.added.SourceAddedToCharacterInSceneReceiver
import com.soyle.stories.scene.create.SceneCreatedNotifier
import com.soyle.stories.scene.create.SceneCreatedReceiver
import com.soyle.stories.scene.delete.SceneDeletedNotifier
import com.soyle.stories.scene.delete.SceneDeletedReceiver
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesNotifier
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesReceiver
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedNotifier
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedReceiver
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneNotifier
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneReceiver
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneNotifier
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneReceiver
import com.soyle.stories.scene.renameScene.SceneRenamedNotifier
import com.soyle.stories.scene.renameScene.SceneRenamedReceiver
import com.soyle.stories.scene.sceneFrame.SceneFrameValueChangedNotifier
import com.soyle.stories.scene.sceneFrame.SceneFrameValueChangedReceiver
import com.soyle.stories.scene.trackSymbolInScene.*
import com.soyle.stories.storyevent.character.remove.CharacterRemovedFromStoryEventNotifier
import com.soyle.stories.storyevent.coverage.uncover.StoryEventUncoveredBySceneNotifier
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensNotifier

object Notifiers {

    init {
        scoped<ProjectScope> {
            provide(SceneCreatedReceiver::class) {
                SceneCreatedNotifier()
            }
            provide {
                CharacterIncludedInSceneNotifier()
            }
            provide(CharacterInvolvedInSceneReceiver::class) {
                CharacterInvolvedInSceneNotifier()
            }
            provide(CharacterArcSectionsCoveredBySceneReceiver::class) {
                CharacterArcSectionsCoveredBySceneNotifier()
            }
            provide(CharacterArcSectionUncoveredInSceneReceiver::class) {
                CharacterArcSectionUncoveredInSceneNotifier()
            }
            provide(TrackedSymbolsRemovedReceiver::class) {
                TrackedSymbolsRemovedNotifier()
            }
            provide(SymbolsTrackedInSceneReceiver::class) {
                SymbolsTrackedInSceneNotifier()
            }
            provide(TrackedSymbolsRenamedReceiver::class) {
                TrackedSymbolsRenamedNotifier()
            }
            provide(SymbolPinnedToSceneReceiver::class) {
                SymbolPinnedToSceneNotifier()
            }
            provide(SymbolUnpinnedFromSceneReceiver::class) {
                SymbolUnpinnedFromSceneNotifier()
            }
            provide(SceneFrameValueChangedReceiver::class) {
                SceneFrameValueChangedNotifier()
            }
            provide(LocationRemovedFromSceneReceiver::class) {
                LocationRemovedFromSceneNotifier()
            }
            provide(LocationUsedInSceneReceiver::class) {
                LocationUsedInSceneNotifier()
            }
            provide(SceneSettingLocationRenamedReceiver::class) {
                SceneSettingLocationRenamedNotifier()
            }
            provide(CharacterRoleInSceneChangedReceiver::class) {
                CharacterRoleInSceneChangedNotifier()
            }
            provide(CharacterDesireInSceneChangedReceiver::class) {
                CharacterDesireInSceneChangedNotifier()
            }
            provide(SceneDeletedReceiver::class) {
                SceneDeletedNotifier()
            }
            provide(SceneRenamedReceiver::class) {
                SceneRenamedNotifier()
            }
            provide(SceneInconsistenciesReceiver::class) {
                SceneInconsistenciesNotifier()
            }
            provide(SourceAddedToCharacterInSceneReceiver::class) {
                SourceAddedToCharacterInSceneNotifier()
            }
            provide { CharacterGainedMotivationInSceneNotifier() }
            provide { CharacterMotivationInSceneClearedNotifier() }
            provide { RemovedCharacterFromSceneNotifier() }
            scoped<SceneCharactersToolScope> {
                hoist<SceneRenamedNotifier> { projectScope }
                hoist<CharacterInvolvedInSceneNotifier> { projectScope }
                hoist<RemovedCharacterFromSceneNotifier> { projectScope }
                hoist<CharacterIncludedInSceneNotifier> { projectScope }
                hoist<CharacterRemovedFromStoryEventNotifier> { projectScope }
                hoist<CharacterRoleInSceneChangedNotifier> { projectScope }
                hoist<SourceAddedToCharacterInSceneNotifier>(SceneCharactersToolScope::projectScope)
                hoist<CharacterGainedMotivationInSceneNotifier>(SceneCharactersToolScope::projectScope)
                hoist<CharacterMotivationInSceneClearedNotifier>(SceneCharactersToolScope::projectScope)
            }
        }
    }
}
