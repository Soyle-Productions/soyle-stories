package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.character.list.CharacterListView
import com.soyle.stories.desktop.view.theme.characterConflict.`Character Conflict View Access`.Companion.access
import com.soyle.stories.desktop.view.theme.characterConflict.`Character Conflict View Access`.Companion.drive
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.theme.characterConflict.CharacterConflict
import com.soyle.stories.theme.characterConflict.CharacterConflictScope
import com.soyle.stories.theme.themeList.ThemeList
import org.junit.jupiter.api.Assertions.assertTrue
import tornadofx.FX
import tornadofx.Stylesheet.Companion.field

fun ThemeList.givenCharacterConflictToolHasBeenOpenedFor(themeId: Theme.Id): CharacterConflict {
    return getOpenCharacterConflictToolFor(themeId) ?: openCharacterConflict(themeId).run {
        getOpenCharacterConflictToolFor(themeId) ?: error("character conflict tool not opened for $themeId")
    }
}

fun ThemeList.getOpenCharacterConflictToolFor(themeId: Theme.Id): CharacterConflict? {
    val characterConflictScope = (scope as ProjectScope).toolScopes.asSequence().filterIsInstance<CharacterConflictScope>()
        .find { it.themeId == themeId.uuid.toString() } ?: return null
    return (FX.getComponents(characterConflictScope)[CharacterConflict::class] as? CharacterConflict)
        ?.takeIf { it.currentStage?.isShowing == true }
}

fun CharacterConflict.givenFocusedOnPerspectiveCharacter(characterId: Character.Id): CharacterConflict
{
    if (! access().isFocusedOn(characterId)) focusOnPerspectiveCharacter(characterId)
    assertTrue(access().isFocusedOn(characterId)) { "Did not focus on $characterId" }
    return this
}

fun CharacterConflict.focusOnPerspectiveCharacter(characterId: Character.Id)
{
    drive {
        perspectiveCharacterSelection.show()
        val characterItem = perspectiveCharacterSelection.characterItem(characterId)!!
        characterItem.fire()
    }
}

fun CharacterConflict.promoteCharacter(characterId: Character.Id)
{
    drive {
        perspectiveCharacterSelection.show()
        val characterItem = perspectiveCharacterSelection.characterItem(characterId)!!
        characterItem.fire()
    }
}

fun CharacterConflict.changePsychologicalWeaknessTo(weakness: String)
{
    drive {
        psychologicalWeaknessInput!!.requestFocus()
        psychologicalWeaknessInput!!.text = weakness
        moralWeaknessInput!!.requestFocus()
    }
}

fun CharacterConflict.changeFieldValueTo(fieldName: String, newValue: String)
{
    val field = when (fieldName) {
        "desire" -> access().desireInput
        "moral weakness" -> access().moralWeaknessInput
        "psychological weakness" -> access().psychologicalWeaknessInput
        else -> error("no field defined for $fieldName")
    }
    drive {
        field!!.requestFocus()
        field.text = newValue
        addOpponentSelection.requestFocus()
    }
}

fun CharacterConflict.changeMoralWeaknessTo(weakness: String)
{
    drive {
        moralWeaknessInput!!.requestFocus()
        moralWeaknessInput!!.text = weakness
        psychologicalWeaknessInput!!.requestFocus()
    }
}

fun CharacterConflict.addOpponentCharacter(character: Character)
{
    drive {
        addOpponentSelection.show()
        addOpponentSelection.items.single { it.id == character.id.uuid.toString() }.fire()
    }
}