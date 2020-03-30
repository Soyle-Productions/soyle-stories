/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 10:26 AM
 */
package com.soyle.stories.layout

import java.util.*

abstract class LayoutException : Exception()

class LayoutDoesNotExist : LayoutException()
class ToolDoesNotExist(val toolId: UUID) : LayoutException()