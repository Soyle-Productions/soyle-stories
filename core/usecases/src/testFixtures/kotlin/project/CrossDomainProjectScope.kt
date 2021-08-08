package com.soyle.stories.usecase.project

import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.framework.CrossDomainTest

fun CrossDomainTest.`given the project`(project: Project): CrossDomainProjectScope = CrossDomainProjectScope(project, this)

class CrossDomainProjectScope(val project: Project, val test: CrossDomainTest)