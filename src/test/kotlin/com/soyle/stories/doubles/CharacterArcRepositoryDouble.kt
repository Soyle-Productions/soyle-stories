package com.soyle.stories.doubles

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.CharacterArcRepository
import java.util.*

class CharacterArcRepositoryDouble(
    private val onAddNewCharacterArc: (CharacterArc) -> Unit = {},
    private val onRemoveCharacterArc: (Theme.Id, Character.Id) -> Unit = { _, _ -> }
) : CharacterArcRepository {

    val characterArcs = mutableMapOf<Pair<Theme.Id, Character.Id>, CharacterArc>()

    /**
     * Available for tests
     */
    fun givenCharacterArc(characterArc: CharacterArc)
    {
        characterArcs[characterArc.themeId to characterArc.characterId] = characterArc
    }

    override suspend fun getCharacterArcByCharacterAndThemeId(
        characterId: Character.Id,
        themeId: Theme.Id
    ): CharacterArc? = characterArcs[themeId to characterId]

    override suspend fun listCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc> =
        characterArcs.filterKeys { it.first == themeId }.map { it.value }

    override suspend fun addNewCharacterArc(characterArc: CharacterArc) {
        onAddNewCharacterArc.invoke(characterArc)
        characterArcs[characterArc.themeId to characterArc.characterId] = characterArc
    }

    override suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id) {
        onRemoveCharacterArc.invoke(themeId, characterId)
        characterArcs.remove(themeId to characterId)
    }

    override suspend fun listCharacterArcsForCharacter(characterId: Character.Id): List<CharacterArc> {
        return characterArcs.values.filter { it.characterId == characterId }
    }
}