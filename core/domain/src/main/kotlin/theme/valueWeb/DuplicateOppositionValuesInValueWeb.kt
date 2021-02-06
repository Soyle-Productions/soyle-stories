package com.soyle.stories.domain.theme.valueWeb

import com.soyle.stories.domain.validation.ValidationException
import java.util.*

class DuplicateOppositionValuesInValueWeb(val valueWebId: UUID, val oppositionValueId: UUID, val count: Int) : ValidationException()