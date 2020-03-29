package com.soyle.stories.entities

import arrow.core.Either
import arrow.core.right

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 2:30 PM
 */
class CharacterArc(
    val characterId: Character.Id,
    val template: CharacterArcTemplate,
    val themeId: Theme.Id,
    val name: String
) {

    companion object {

        fun planNewCharacterArc(characterId: Character.Id, themeId: Theme.Id, name: String): Either<*, CharacterArc> {
            return CharacterArc(
                characterId,
                CharacterArcTemplate.default(),
                themeId,
                name
            ).right()
        }
/*
		fun planNewCharacterArc(characterId: Character.Id, themeId: Theme.Id, name: String): Either<*, CharacterArc> {
			val template = CharacterArcTemplate.default()

			return CharacterArc(characterId, template, template.sections.map {
				CharacterArcSection(CharacterArcSection.Id(UUID.randomUUID()), it, "")
			}, themeId, name).right()
		}*/
    }

}