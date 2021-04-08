package com.soyle.stories.desktop.config.scene

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.charactersInScene.RenamedCharacterInSceneNotifier
import com.soyle.stories.scene.charactersInScene.RenamedCharacterInSceneReceiver
import com.soyle.stories.scene.charactersInScene.assignRole.CharacterRoleInSceneChangedNotifier
import com.soyle.stories.scene.charactersInScene.assignRole.CharacterRoleInSceneChangedReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneNotifier
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneNotifier
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneReceiver
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludedCharacterInSceneNotifier
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludedCharacterInSceneReceiver
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneNotifier
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneReceiver
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedNotifier
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedReceiver
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneNotifier
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneReceiver
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneNotifier
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneReceiver
import com.soyle.stories.scene.sceneFrame.SceneFrameValueChangedNotifier
import com.soyle.stories.scene.sceneFrame.SceneFrameValueChangedReceiver
import com.soyle.stories.scene.trackSymbolInScene.*

object Notifiers {

    init {
        scoped<ProjectScope> {
            provide(IncludedCharacterInSceneReceiver::class) {
                IncludedCharacterInSceneNotifier()
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
            provide(RemovedCharacterFromSceneReceiver::class) {
                RemovedCharacterFromSceneNotifier(applicationScope.get())
            }
            provide(RenamedCharacterInSceneReceiver::class) {
                RenamedCharacterInSceneNotifier()
            }
            provide(CharacterRoleInSceneChangedReceiver::class) {
                CharacterRoleInSceneChangedNotifier()
            }
        }
    }

}