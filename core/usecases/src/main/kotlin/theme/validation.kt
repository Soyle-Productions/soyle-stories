package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.validation.DuplicateOperationException
import com.soyle.stories.domain.validation.EntityNotFoundException
import java.util.*

class ThemeDoesNotExist(val themeId: UUID) : EntityNotFoundException(themeId)
class SymbolDoesNotExist(val symbolId: UUID) : EntityNotFoundException(symbolId)

class SymbolAlreadyHasName(val symbolId: UUID, val symbolName: String) : DuplicateOperationException()

class ValueWebDoesNotExist(val valueWebId: UUID) : EntityNotFoundException(valueWebId)
class SymbolicRepresentationNotInOppositionValue(val oppositionValueId: UUID, val symbolicRepresentationId: UUID) :
    EntityNotFoundException(symbolicRepresentationId)

class OppositionValueAlreadyHasName(val oppositionValueId: UUID, val oppositionValueName: String) : DuplicateOperationException()
class ValueWebAlreadyHasName(val valueWebId: UUID, val valueWebName: String) : DuplicateOperationException()
