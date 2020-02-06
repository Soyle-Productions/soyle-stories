package com.soyle.studio.common

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:48 PM
 */
interface AggregateRoot<Id> : Entity<Id> {
    val events: List<DomainEvent<Id>>
}