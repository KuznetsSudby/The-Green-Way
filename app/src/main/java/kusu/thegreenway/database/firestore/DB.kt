package kusu.thegreenway.database.firestore

import kusu.thegreenway.database.models.Category
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.DotType
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.utils.Result

interface DB {
    suspend fun getRoutes(): Result<HashMap<String, Route>>
    suspend fun getCategories(): Result<HashMap<String, Category>>
    suspend fun getDots(): Result<HashMap<String, Dot>>
    suspend fun getDotTypes(): Result<HashMap<String, DotType>>
}