package util

import type.StoryDirectoryNode
import type.StoryGroup
import type.StoryGroupNode
import type.StoryLeafNode
import type.StoryTreeNode

internal fun List<StoryGroup>.buildStoryTree(): List<StoryTreeNode> {
    validateStories()

    val root =
        MutableDirectory(
            title = "",
            path = "",
        )

    forEach { group ->
        val segments = group.path.split("/")
        val directorySegments = segments.dropLast(1)
        val groupTitle = segments.last()

        var currentDirectory = root
        var currentPath = ""

        directorySegments.forEach { segment ->
            currentPath = if (currentPath.isBlank()) segment else "$currentPath/$segment"
            currentDirectory =
                currentDirectory.getOrCreateDirectory(
                    title = segment,
                    path = currentPath,
                )
        }

        currentDirectory.addGroup(
            group = group,
            title = groupTitle,
        )
    }

    return root.toNodeList()
}

internal fun List<StoryTreeNode>.findFirstStoryLeaf(): StoryLeafNode? {
    forEach { node ->
        val leaf = node.findFirstStoryLeaf()
        if (leaf != null) return leaf
    }

    return null
}

internal fun List<StoryTreeNode>.findStoryLeafById(id: String?): StoryLeafNode? {
    if (id == null) return null

    forEach { node ->
        val leaf = node.findStoryLeafById(id)
        if (leaf != null) return leaf
    }

    return null
}

private fun StoryTreeNode.findFirstStoryLeaf(): StoryLeafNode? =
    when (this) {
        is StoryLeafNode -> {
            this
        }

        else -> {
            children.forEach { child ->
                val leaf = child.findFirstStoryLeaf()
                if (leaf != null) return leaf
            }

            null
        }
    }

private fun StoryTreeNode.findStoryLeafById(id: String): StoryLeafNode? =
    when (this) {
        is StoryLeafNode -> {
            if (this.id == id) this else null
        }

        else -> {
            children.forEach { child ->
                val leaf = child.findStoryLeafById(id)
                if (leaf != null) return leaf
            }

            null
        }
    }

private class MutableDirectory(
    private val title: String,
    private val path: String,
) {
    private val children = mutableListOf<MutableNode>()
    private val directoriesByPath = mutableMapOf<String, MutableDirectory>()

    fun getOrCreateDirectory(
        title: String,
        path: String,
    ): MutableDirectory {
        directoriesByPath[path]?.let { return it }

        val directory =
            MutableDirectory(
                title = title,
                path = path,
            )

        directoriesByPath[path] = directory
        children += MutableNode.Directory(directory)

        return directory
    }

    fun addGroup(
        group: StoryGroup,
        title: String,
    ) {
        children +=
            MutableNode.Group(
                group = group,
                title = title,
            )
    }

    fun toNodeList(): List<StoryTreeNode> = children.map { it.toStoryTreeNode() }

    fun toStoryDirectoryNode(): StoryDirectoryNode =
        StoryDirectoryNode(
            id = "directory:$path",
            title = title,
            path = path,
            children = children.map { it.toStoryTreeNode() },
        )
}

private sealed interface MutableNode {
    fun toStoryTreeNode(): StoryTreeNode

    data class Directory(
        val value: MutableDirectory,
    ) : MutableNode {
        override fun toStoryTreeNode(): StoryTreeNode = value.toStoryDirectoryNode()
    }

    data class Group(
        val group: StoryGroup,
        val title: String,
    ) : MutableNode {
        override fun toStoryTreeNode(): StoryTreeNode {
            val storyLeaves =
                group.stories.map { story ->
                    StoryLeafNode(
                        id = "story:${group.path}#${story.id}",
                        title = story.title,
                        path = "${group.path}/${story.id}",
                        group = group,
                        story = story,
                    )
                }

            return StoryGroupNode(
                id = "group:${group.path}",
                title = title,
                path = group.path,
                group = group,
                children = storyLeaves,
            )
        }
    }
}
