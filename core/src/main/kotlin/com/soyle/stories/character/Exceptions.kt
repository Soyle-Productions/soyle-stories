package com.soyle.stories.character

import java.util.*

/**
 * Created by Brendan
 * Date: 2/6/2020
 * Time: 8:32 PM
 */
abstract class CharacterException : Exception()
class CharacterDoesNotExist(val characterId: UUID) : CharacterException()