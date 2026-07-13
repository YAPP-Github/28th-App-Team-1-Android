package type

internal data class StoryLeafNode(
    override val id: String,
    override val title: String,
    override val path: String,
    val group: StoryGroup,
    val story: Story,
) : StoryTreeNode {
    override val children: List<StoryTreeNode> = emptyList()
}
