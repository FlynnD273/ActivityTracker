package com.flynnd273.activitytracker.database

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction


object ActivitiesTable : IntIdTable("activities") {
    val name = varchar("name", 256).uniqueIndex()
    val goalTime = long("goal_time")
    val accumulatedTime = long("accumulated_time")
    val startTime = datetime("start_time").nullable()
}

class ActivityTask(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ActivityTask>(ActivitiesTable)

    var name by ActivitiesTable.name
    var accumulatedTime by ActivitiesTable.accumulatedTime
    var goalTime by ActivitiesTable.goalTime
    var startTime by ActivitiesTable.startTime
}


fun initDb() {
    transaction {
        SchemaUtils.create(ActivitiesTable)
    }
}