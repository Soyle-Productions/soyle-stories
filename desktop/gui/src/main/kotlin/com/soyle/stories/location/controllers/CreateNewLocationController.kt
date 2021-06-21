package com.soyle.stories.location.controllers

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

interface CreateNewLocationController {

	companion object {
		operator fun invoke(
			threadTransformer: ThreadTransformer,
			createNewLocation: CreateNewLocation,
			createNewLocationOutputPort: CreateNewLocation.OutputPort
		) = object : CreateNewLocationController {

			private fun output(deferred: CompletableDeferred<CreateNewLocation.ResponseModel>) = object : CreateNewLocation.OutputPort {
				override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
					createNewLocationOutputPort.receiveCreateNewLocationResponse(response)
					deferred.complete(response)
				}

				override fun receiveCreateNewLocationFailure(failure: Exception) {
					createNewLocationOutputPort.receiveCreateNewLocationFailure(failure)
					deferred.completeExceptionally(failure)
				}
			}

			override fun createNewLocation(name: SingleNonBlankLine, description: String): Deferred<CreateNewLocation.ResponseModel> {
				val deferred = CompletableDeferred<CreateNewLocation.ResponseModel>()
				threadTransformer.async {
					createNewLocation.invoke(name, description.takeIf { it.isNotBlank() }, output(deferred))
				}
				return deferred
			}
		}
	}

	fun createNewLocation(name: SingleNonBlankLine, description: String): Deferred<CreateNewLocation.ResponseModel>

}