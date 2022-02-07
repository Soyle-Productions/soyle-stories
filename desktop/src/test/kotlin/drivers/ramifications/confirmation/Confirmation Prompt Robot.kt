package com.soyle.stories.desktop.config.drivers.ramifications.confirmation

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptView

fun ConfirmationPromptView<*>.confirm()
{
    robot.interact {
        viewModel.confirm()
    }
}

fun ConfirmationPromptView<*>.showRamifications() {
    robot.interact {
        viewModel.check()
    }
}