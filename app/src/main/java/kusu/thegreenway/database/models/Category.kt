package kusu.thegreenway.database.models

import java.io.Serializable

data class Category(
    val id: String,
    val title: String,
    val description: String
): Serializable