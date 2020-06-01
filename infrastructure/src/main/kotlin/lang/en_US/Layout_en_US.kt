package lang.en_US

import com.soyle.stories.layout.tools.ToolType
import com.soyle.stories.layout.tools.dynamic.BaseStoryStructure
import com.soyle.stories.layout.tools.dynamic.CharacterComparison
import com.soyle.stories.layout.tools.dynamic.LocationDetails
import com.soyle.stories.layout.tools.dynamic.StoryEventDetails
import com.soyle.stories.layout.tools.fixed.FixedTool
import com.soyle.stories.layout.tools.temporary.Ramifications
import com.soyle.stories.project.layout.LayoutLocale

class Layout_en_US : LayoutLocale {

	companion object {
		private val toolNames = mapOf(
		  FixedTool.LocationList::class to "Locations",
		  FixedTool.CharacterList::class to "Characters",
		  FixedTool.StoryEventList::class to "Story Events",
		  FixedTool.SceneList::class to "Scenes",
		  BaseStoryStructure::class to "Base Story Structure",
		  CharacterComparison::class to "Character Comparison",
		  LocationDetails::class to "Location",
		  StoryEventDetails::class to "Story Event",
		  Ramifications::class to "Ramifications"
		)
	}

	override fun toolName(toolType: ToolType): String {
		return toolNames.getValue(toolType::class)
	}

}