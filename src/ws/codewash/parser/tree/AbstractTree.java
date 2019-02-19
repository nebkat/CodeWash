package ws.codewash.parser.tree;

abstract class AbstractTree<T extends AbstractTreeNode> {
	private T mRoot;

	AbstractTree(T root) {
		mRoot = root;
	}

	public T getRoot() {
		return mRoot;
	}

	@Override
	public String toString() {
		return mRoot.toString();
	}

	public String toString(int maxDepth) {
		return mRoot.toString(maxDepth);
	}
}
