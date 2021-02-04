package com.soyle.stories.project

abstract class ProjectException : Exception()

object NameCannotBeBlank : ProjectException()