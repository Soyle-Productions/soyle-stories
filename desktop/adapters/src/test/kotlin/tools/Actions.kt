package com.soyle.stories.desktop.adapter.tools

import com.soyle.stories.desktop.adapter.project.ProjectEvent

interface ToolsListEvent : ProjectEvent

class ToolOpened() : ToolsListEvent
class ToolClosed() : ToolsListEvent