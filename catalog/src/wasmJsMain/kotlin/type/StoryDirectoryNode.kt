package type

internal data class StoryDirectoryNode(
    override val id: String,
    override val title: String,
    override val path: String,
    override val children: List<StoryTreeNode>,
) : StoryTreeNode
