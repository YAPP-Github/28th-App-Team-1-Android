package util

import type.DEFAULT_STORY_ID
import type.StoryGroup

internal fun List<StoryGroup>.validateStories(): List<StoryGroup> {
    val groupPaths = mutableSetOf<String>()

    forEach { group ->
        group.validatePath()

        require(groupPaths.add(group.path)) {
            "Duplicated StoryGroup path: '${group.path}'."
        }

        require(group.stories.isNotEmpty()) {
            "StoryGroup '${group.path}' must contain at least one story."
        }

        require(group.stories.any { it.id == DEFAULT_STORY_ID }) {
            "StoryGroup '${group.path}' must contain a '$DEFAULT_STORY_ID' story."
        }

        val storyIds = mutableSetOf<String>()

        group.stories.forEach { story ->
            require(story.id.isNotBlank()) {
                "Story id in '${group.path}' must not be blank."
            }

            require('/' !in story.id) {
                "Story id '${story.id}' in '${group.path}' must not contain '/'."
            }

            require(story.id == story.id.trim()) {
                "Story id '${story.id}' in '${group.path}' must not have leading or trailing whitespace."
            }

            require(storyIds.add(story.id)) {
                "Duplicated Story id '${story.id}' in '${group.path}'."
            }
        }
    }

    return this
}

private fun StoryGroup.validatePath() {
    require(path.isNotBlank()) {
        "StoryGroup path must not be blank."
    }

    require(path == path.trim()) {
        "StoryGroup path '$path' must not have leading or trailing whitespace."
    }

    require(!path.startsWith("/")) {
        "StoryGroup path '$path' must not start with '/'."
    }

    require(!path.endsWith("/")) {
        "StoryGroup path '$path' must not end with '/'."
    }

    require("//" !in path) {
        "StoryGroup path '$path' must not contain empty path segments."
    }

    val segments = path.split("/")

    require(segments.size >= 2) {
        "StoryGroup path '$path' should contain at least one directory and one group name, e.g. 'Components/Button'."
    }

    segments.forEach { segment ->
        require(segment.isNotBlank()) {
            "StoryGroup path '$path' contains a blank path segment."
        }

        require(segment == segment.trim()) {
            "StoryGroup path '$path' contains a segment with leading or trailing whitespace: '$segment'."
        }
    }
}
