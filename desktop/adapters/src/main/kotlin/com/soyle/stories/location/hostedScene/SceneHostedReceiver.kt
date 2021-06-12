package com.soyle.stories.location.hostedScene

import com.soyle.stories.domain.location.events.SceneHostedAtLocation

interface SceneHostedReceiver {

    suspend fun receiveSceneHostedAtLocation(event: SceneHostedAtLocation)

}
