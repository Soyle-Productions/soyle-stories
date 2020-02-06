package com.soyle.studio.common

import java.util.*

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:49 PM
 */
abstract class DomainEvent<Id> {
	val dateOccurred = Date()
	abstract val aggregateId: Id
}