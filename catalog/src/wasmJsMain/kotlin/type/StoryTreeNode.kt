package type

internal sealed interface StoryTreeNode {
    val id: String
    val title: String
    val path: String
    val children: List<StoryTreeNode>
}
