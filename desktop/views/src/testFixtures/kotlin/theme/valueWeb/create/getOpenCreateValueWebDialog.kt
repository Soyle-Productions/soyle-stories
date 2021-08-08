package com.soyle.stories.desktop.view.theme.valueWeb.create

import com.soyle.stories.theme.valueWeb.create.CreateValueWebForm
import org.testfx.api.FxRobot

fun FxRobot.getOpenCreateValueWebDialog(): CreateValueWebForm? =
    listWindows().asSequence()
        .mapNotNull { it.scene.root as? CreateValueWebForm }
        .firstOrNull { it.scene.window?.isShowing == true }