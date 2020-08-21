package com.soyle.stories.entities.theme.valueWeb

import com.soyle.stories.common.ValidationException
import java.util.*

class DuplicateOppositionValuesInValueWeb(val valueWebId: UUID, val oppositionValueId: UUID, val count: Int) : ValidationException()