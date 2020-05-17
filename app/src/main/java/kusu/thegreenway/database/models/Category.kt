package kusu.thegreenway.database.models

import java.io.Serializable

data class Category(
    val title: String,
    val description: String
): Serializable