package kusu.thegreenway.common.dagger

import javax.inject.Singleton

@Singleton
class Config(
    val versionName: String,
    val appId: String
)