package com.soyle.stories.project.layout

import javafx.beans.property.Property
import tornadofx.Fragment

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 4:32 PM
 */
abstract class WindowChild : Fragment() {

    val model: Property<WindowChildViewModel> by params

}