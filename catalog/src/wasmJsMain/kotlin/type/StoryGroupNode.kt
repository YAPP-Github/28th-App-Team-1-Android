package type

internal data class StoryGroupNode(
    override val id: String,
    override val title: String,
    override val path: String,
    val group: StoryGroup,
    override val children: List<StoryLeafNode>,
) : StoryTreeNode
