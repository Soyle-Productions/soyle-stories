package com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu

@Suppress("FunctionName")
interface TimelineRulerLabelMenuComponent {

    fun TimelineRulerLabelMenu(): TimelineRulerLabelMenu

    companion object {
        fun Implementation(): TimelineRulerLabelMenuComponent = object : TimelineRulerLabelMenuComponent {
            override fun TimelineRulerLabelMenu(): TimelineRulerLabelMenu {
                return com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenu()
            }
        }
    }

}