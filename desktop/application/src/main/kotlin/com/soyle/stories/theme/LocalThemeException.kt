package com.soyle.stories.theme

abstract class LocalThemeException : Exception()

class RemoveCharacterFromComparisonFailure(override val cause: ThemeException) : LocalThemeException()