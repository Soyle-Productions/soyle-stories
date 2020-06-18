/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 11:00 AM
 */
package com.soyle.stories.theme

fun ThemeDoesNotExist.themeIdMustEqual(value: Any) = assert(themeId == value) { "themeId of ThemeDoesNotExist does not equal $value" }

fun themeNameCannotBeBlank(actual: Any?) {
    actual as ThemeNameCannotBeBlank
}