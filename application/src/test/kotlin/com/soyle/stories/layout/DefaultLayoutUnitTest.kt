package com.soyle.stories.layout

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.defaultLayout
import com.soyle.stories.layout.tools.fixed.FixedTool
import org.junit.jupiter.api.Test
import java.util.*

class DefaultLayoutUnitTest {

	@Test
	fun `default layout contains all fixed tools`() {
		val layout = defaultLayout(Project.Id(), Layout.Id(UUID.randomUUID()))
		FixedTool::class.nestedClasses.forEach {

		}
	}

}