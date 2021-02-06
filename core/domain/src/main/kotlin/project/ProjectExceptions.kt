package com.soyle.stories.domain.project

abstract class ProjectException : Exception()

object NameCannotBeBlank : ProjectException()