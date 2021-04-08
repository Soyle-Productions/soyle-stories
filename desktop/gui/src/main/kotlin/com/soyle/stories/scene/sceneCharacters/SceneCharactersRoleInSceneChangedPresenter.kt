package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.events.CharacterRoleInSceneChanged
import com.soyle.stories.domain.scene.events.CompoundEvent
import com.soyle.stories.gui.View
import com.soyle.stories.scene.charactersInScene.assignRole.CharacterRoleInSceneChangedReceiver
import com.soyle.stories.scene.sceneCharacters.SceneCharactersPresenter.Companion.toRoleInSceneViewModel

class SceneCharactersRoleInSceneChangedPresenter(
    private val view: View.Nullable<SceneCharactersViewModel>
) : CharacterRoleInSceneChangedReceiver {

    override suspend fun receiveCharacterRolesInSceneChanged(event: CompoundEvent<CharacterRoleInSceneChanged>) {
        view.updateIf({ targetSceneId == event.sceneId }) {
            copy(includedCharacters = includedCharacters?.map(updateCharacterRoleIfInUpdate(event.events)))
        }
    }

    private fun updateCharacterRoleIfInUpdate(
        updates: List<CharacterRoleInSceneChanged>
    ): (IncludedCharacterViewModel) -> IncludedCharacterViewModel {
        val updatesByCharacterId = updates.associateBy { it.characterId }

        return fun(characterViewModel: IncludedCharacterViewModel): IncludedCharacterViewModel {
            val update = updatesByCharacterId[characterViewModel.id] ?: return characterViewModel
            return characterViewModel.copy(roleInScene = update.newRole.toRoleInSceneViewModel())
        }
    }
}