package com.barreto.android.data.provider

enum class Host(val baseUrl: String) {
    RELEASE("https://gateway.marvel.com/v1/public/"),
    DEBUG("https://gateway.marvel.com/v1/public/");

    companion object {
        fun fromBuildType(flavor: String, buildType: String): Host {
            return valueOf("${flavor}_$buildType".toUpperCase())
        }
        fun fromBuildType(buildType: String): Host {
            return valueOf(buildType.toUpperCase())
        }
    }
}
