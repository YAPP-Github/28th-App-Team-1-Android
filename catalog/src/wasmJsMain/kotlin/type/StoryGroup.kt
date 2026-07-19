package type

internal data class StoryGroup(
    val path: String,
    val description: String? = null,
    val stories: List<Story>,
) {
    val title: String
        get() = path.substringAfterLast("/")
}
