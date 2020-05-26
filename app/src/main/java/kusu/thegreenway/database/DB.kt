package kusu.thegreenway.database

import kusu.thegreenway.database.models.Category
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.DotType
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.utils.Result

interface DB {

    suspend fun getRoutes(): Result<List<Route>>
}