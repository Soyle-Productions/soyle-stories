package com.soyle.stories.desktop.adapter.project

import com.soyle.stories.desktop.adapter.app.ApplicationEvent
import com.soyle.stories.domain.project.Project

interface ProjectListEvent : ApplicationEvent

class ProjectOpened(val projectId: Project.Id, val projectName: String) : ProjectListEvent
class ProjectClosed(val projectId: Project.Id) : ProjectListEvent


interface ProjectEvent : ProjectListEvent

class ProjectRenamed(val projectId: Project.Id, val newName: String, val oldName: String) : ProjectEvent