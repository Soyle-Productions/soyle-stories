package com.soyle.stories.characterarc

import java.util.*

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 3:35 PM
 */
abstract class CharacterArcException : Exception()

class CharacterArcSectionDoesNotExist(val characterArcSectionId: UUID) : CharacterArcException()