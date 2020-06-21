/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 11:00 AM
 */
package com.soyle.stories.theme

import com.soyle.stories.theme.usecases.SymbolNameCannotBeBlank
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*

fun ThemeDoesNotExist.themeIdMustEqual(value: Any) = assert(themeId == value) { "themeId of ThemeDoesNotExist does not equal $value" }

fun themeNameCannotBeBlank(actual: Any?) {
    actual as ThemeNameCannotBeBlank
}

fun themeDoesNotExist(themeId: UUID): (Any?) -> Unit = { actual ->
    actual as ThemeDoesNotExist
    assertEquals(themeId, actual.themeId)
}
fun symbolNameCannotBeBlank(actual: Any?)
{
    actual as SymbolNameCannotBeBlank
}