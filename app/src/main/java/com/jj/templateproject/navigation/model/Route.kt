package com.jj.templateproject.navigation.model

import com.jj.templateproject.navigation.model.Argument.SecondaryScreenArgument

private const val REQUIRED_ARG_SEPARATOR = "/"
private const val OPTIONAL_ARG_SEPARATOR = "?"

sealed class Route(
    val route: String,
    val path: String,
) {
    object MainScreen : Route(
        route = "main_screen",
        path = "main_screen",
    )

    object SecondaryScreen : Route(
        route = "secondary_screen" +
                "/{${SecondaryScreenArgument.Text.name}}" +
                "?${SecondaryScreenArgument.TextOptionalSecondary.name}={${SecondaryScreenArgument.TextOptionalSecondary.name}}" +
                "?${SecondaryScreenArgument.TextOptionalTertiary.name}={${SecondaryScreenArgument.TextOptionalTertiary.name}}",
        path = "secondary_screen",
    )
}

sealed class Argument(val name: String) {
    sealed class SecondaryScreenArgument(argName: String) : Argument(name = argName) {
        object Text : SecondaryScreenArgument("text")
        object TextOptionalSecondary : SecondaryScreenArgument("textSecondary")
        object TextOptionalTertiary : SecondaryScreenArgument("textTertiary")
    }
}

fun Route.createPath(
    requiredArgsList: List<String> = emptyList(),
    optionalArgsMap: Map<String, String?> = mutableMapOf()
): String {
    val pathBuilder = StringBuilder(path)
    requiredArgsList.forEach { value ->
        pathBuilder.append("$REQUIRED_ARG_SEPARATOR$value")
    }
    optionalArgsMap.forEach { entry ->
        pathBuilder.append("$OPTIONAL_ARG_SEPARATOR${entry.key}=${entry.value.orEmpty()}")
    }
    return pathBuilder.toString()
}