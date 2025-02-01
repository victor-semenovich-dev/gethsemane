package by.geth.gethsemane

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform