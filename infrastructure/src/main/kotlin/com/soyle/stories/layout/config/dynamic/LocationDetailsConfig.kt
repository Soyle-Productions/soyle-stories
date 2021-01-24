package com.soyle.stories.layout.config.dynamic

import com.soyle.stories.entities.Location
import com.soyle.stories.layout.config.ToolConfig
import com.soyle.stories.layout.config.ToolTabConfig
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.DynamicTool
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.locationDetails.LocationDetailsScope
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import tornadofx.find
import tornadofx.onChange
import tornadofx.tab
import java.util.*
import kotlin.reflect.KClass

object LocationDetailsConfig : ToolConfig<LocationDetails> {

	override fun getRegistration(): Pair<KClass<LocationDetails>, ToolConfig<LocationDetails>> {
		return LocationDetails::class to this
	}

	override fun getFixedType(): FixedTool? = null

	override fun getViewModelConfig(type: LocationDetails): ToolViewModelConfig {
		return object : ToolViewModelConfig {
			override fun toolName(): String = "Location: ${type.locationId}"
		}
	}

	override fun getTabConfig(tool: ToolViewModel, type: LocationDetails): ToolTabConfig {
		return object : ToolTabConfig {
			override fun getTab(tabPane: TabPane, projectScope: ProjectScope): Tab {
				val scope = LocationDetailsScope(projectScope, type)
				val structure = find<com.soyle.stories.location.locationDetails.LocationDetails>(scope = scope)
				val tab = tabPane.tab(structure)
				tab.tabPaneProperty().onChange {
					if (it == null) {
						scope.close()
					}
				}
				return tab
			}
		}
	}

}

data class LocationDetails(val locationId: UUID) : DynamicTool() {

	override val isTemporary: Boolean
		get() = false

	override suspend fun validate(context: OpenToolContext) {
		context.locationRepository.getLocationById(Location.Id(locationId))
		  ?: throw LocationDoesNotExist(locationId)
	}

	override fun identifiedWithId(id: UUID): Boolean =
	  id == locationId
}