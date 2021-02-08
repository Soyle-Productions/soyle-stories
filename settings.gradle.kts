rootProject.name = "Soyle Stories"

include(":domain")
project(":domain").projectDir = file("core/domain")
include(":usecases")
project(":usecases").projectDir = file("core/usecases")
include(":desktop")
include(":desktop:application")
include(":desktop:adapters")
include(":desktop:gui")
include(":desktop:views")