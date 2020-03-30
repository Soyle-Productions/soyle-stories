package com.soyle.stories.characterarc

abstract class LocalCharacterArcException : Exception()

class FailedToDemoteCharacter(override val cause: Exception) : LocalCharacterArcException()