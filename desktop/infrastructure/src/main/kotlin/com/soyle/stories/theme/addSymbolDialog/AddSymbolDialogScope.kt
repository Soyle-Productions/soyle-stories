package com.soyle.stories.theme.addSymbolDialog

import com.soyle.stories.common.SubProjectScope
import com.soyle.stories.project.ProjectScope

class AddSymbolDialogScope(
    projectScope: ProjectScope,
    val themeId: String,
    val oppositionId: String
) : SubProjectScope(projectScope)