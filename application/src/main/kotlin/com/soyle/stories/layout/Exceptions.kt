/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 10:26 AM
 */
package com.soyle.stories.layout

import com.soyle.stories.layout.tools.FixedTool
import java.util.*

abstract class LayoutException : Exception()

class LayoutDoesNotExist : LayoutException()
class LayoutDoesNotContainFixedTool(val fixedTool: FixedTool) : Error()
class ToolDoesNotExist(val toolId: UUID) : LayoutException()