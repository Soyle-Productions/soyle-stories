package com.soyle.stories.desktop.config.character

import com.soyle.stories.character.nameVariant.addNameVariant.CharacterNameVariantAddedNotifier
import com.soyle.stories.character.nameVariant.addNameVariant.CharacterNameVariantAddedReceiver
import com.soyle.stories.character.nameVariant.remove.CharacterNameVariantRemovedNotifier
import com.soyle.stories.character.nameVariant.remove.CharacterNameVariantRemovedReceiver
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.character.renameCharacter.CharacterRenamedNotifier
import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgumentNotifier
import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgumentReceiver
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemovedNotifier
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemovedReceiver
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.characters.tool.SceneCharactersToolScope
import com.soyle.stories.storyevent.coverage.uncover.StoryEventUncoveredBySceneNotifier
import com.soyle.stories.storyevent.remove.StoryEventNoLongerHappensNotifier
import com.soyle.stories.theme.removeSymbolicItem.RemoveSymbolicItemControllerImpl

object Notifiers {

    init {
        scoped<ProjectScope> {

            provide(CharacterArcSectionMovedInMoralArgumentReceiver::class) {
                CharacterArcSectionMovedInMoralArgumentNotifier()
            }

            provide(CharacterArcSectionRemovedReceiver::class) {
                CharacterArcSectionRemovedNotifier()
            }

            provide(CharacterRenamedReceiver::class) {
                CharacterRenamedNotifier()
            }

            provide(CharacterNameVariantAddedReceiver::class) { CharacterNameVariantAddedNotifier() }
            provide(CharacterNameVariantRemovedReceiver::class) { CharacterNameVariantRemovedNotifier() }
            provide { RemovedCharacterNotifier() }

            scoped<SceneCharactersToolScope> {
                hoist<CharacterRenamedNotifier> { projectScope }
                hoist<RemovedCharacterNotifier> { projectScope }
            }
        }
    }

}