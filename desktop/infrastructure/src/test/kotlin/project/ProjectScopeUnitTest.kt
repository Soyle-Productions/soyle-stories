package com.soyle.stories.project

import com.soyle.stories.di.DI
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.soylestories.ApplicationModel
import com.soyle.stories.soylestories.ApplicationScope
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.FX
import tornadofx.Scope
import tornadofx.find
import tornadofx.observableListOf
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.full.declaredMemberProperties

class ProjectScopeUnitTest : ApplicationTest() {

	private lateinit var appScope: ApplicationScope
	private lateinit var model: ApplicationModel

	@BeforeEach
	fun setupFX() {
		appScope = ApplicationScope()
		val primaryStage = FxToolkit.registerPrimaryStage()
		FX.setPrimaryStage(appScope, primaryStage)
		model = find(scope = appScope)
		model.openProjects.unbind()
	}

	@Test
	fun `project scopes are registered within application scope`() {
		var scope: ProjectScope? = null
		interact {
			model.openProjects.set(observableListOf(ProjectFileViewModel(UUID.randomUUID(), "Project Name", "Some place somewhere")))
			scope = appScope.projectScopes.single()
		}
		assertThat(scope!!.isRegistered.value).isTrue()
		assertThat(FX.Companion::class.declaredMemberProperties.find { it.name == "components" }!!.get(FX.Companion) as Map<Scope, HashMap<*, *>>).containsKey(scope)
		assertThat(DI.isScopeRegistered(scope!!)).isTrue()
	}

	@Test
	fun `close scope`() {
		var scope: ProjectScope? = null
		interact {
			model.openProjects.set(observableListOf(ProjectFileViewModel(UUID.randomUUID(), "Project Name", "Some place somewhere")))
			scope = appScope.projectScopes.single()
			model.openProjects.set(observableListOf())
		}
		assertThat(scope!!.isRegistered.value).isFalse()
		assertThat(FX.Companion::class.declaredMemberProperties.find { it.name == "components" }!!.get(FX.Companion) as Map<Scope, HashMap<*, *>>).doesNotContainKey(scope)
		assertThat(DI.isScopeRegistered(scope!!)).isFalse()
	}
}