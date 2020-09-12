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

    private val characterArcsByThemeAndCharacter = mutableMapOf<Pair<Theme.Id, Character.Id>, CharacterArc>()
    private val characterArcsById = mutableMapOf<CharacterArc.Id, CharacterArc>()
    /**
     * Available for tests
     */
    fun givenCharacterArc(characterArc: CharacterArc)
    {
        characterArcsByThemeAndCharacter[characterArc.themeId to characterArc.characterId] = characterArc
        characterArcsById[characterArc.id] = characterArc
    }

    fun getCharacterArc(id: CharacterArc.Id): CharacterArc? = characterArcsById[id]

    override suspend fun getCharacterArcByCharacterAndThemeId(
        characterId: Character.Id,
        themeId: Theme.Id
    ): CharacterArc? = characterArcsByThemeAndCharacter[themeId to characterId]

    override suspend fun listCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc> =
        characterArcsByThemeAndCharacter.filterKeys { it.first == themeId }.map { it.value }

    override suspend fun addNewCharacterArc(characterArc: CharacterArc) {
        onAddNewCharacterArc.invoke(characterArc)
        characterArcsByThemeAndCharacter[characterArc.themeId to characterArc.characterId] = characterArc
        characterArcsById[characterArc.id] = characterArc
    }

    override suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id) {
        onRemoveCharacterArc.invoke(themeId, characterId)
        characterArcsByThemeAndCharacter.remove(themeId to characterId)?.let {
            characterArcsById.remove(it.id)
        }
    }

    override suspend fun listCharacterArcsForCharacter(characterId: Character.Id): List<CharacterArc> {
        return characterArcsByThemeAndCharacter.values.filter { it.characterId == characterId }
    }
}