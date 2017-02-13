/**
 * Created by Hao Xiong on 1/26/2017.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node of the Tree<T> class. The Node<T> is also a container, and
 * can be thought of as instrumentation to determine the location of the type T
 * in the Tree<T>.
 */
public class Node<T> {

    /**
     * Store the associated data
     */
    private T data;
    private List<Node<T>> children;

    /**
     * Used to mark each tree node in the family tree started at this node
     */
    public int position;

    /**
     * Default ctor.
     */
    public Node() {
        super();
        //Mark the position of leaf node as -1
        this.position = -1;////////////////////////
    }

    /**
     * Convenience ctor to create a Node<T> with an instance of T.
     *
     * @param data an instance of T.
     */
    public Node(T data) {
        this();
        setData(data);
    }

    /**
     * Return the children of Node<T>. The Tree<T> is represented by a single
     * root Node<T> whose children are represented by a List<Node<T>>. Each of
     * these Node<T> elements in the List can have children. The getChildren()
     * method will return the children of a Node<T>.
     *
     * @return the children of Node<T>
     */
    public List<Node<T>> getChildren() {
        if (this.children == null) {
            return new ArrayList<Node<T>>();
        }
        return this.children;
    }

    /**
     * Sets the children of a Node<T> object. See docs for getChildren() for
     * more information.
     *
     * @param children the List<Node<T>> to set.
     */
    public void setChildren(List<Node<T>> children) {
        this.children = children;
    }

    /**
     * Returns the number of immediate children of this Node<T>.
     *
     * @return the number of immediate children.
     */
    public int getNumberOfChildren() {
        if (children == null) {
            return 0;
        }
        return children.size();
    }

    /**
     * Adds a child to the list of children for this Node<T>. The addition of
     * the first child will create a new List<Node<T>>.
     *
     * @param child a Node<T> object to set.
     */
    public void addChild(Node<T> child) {
        if (children == null) {
            children = new ArrayList<Node<T>>();
        }
        children.add(child);
    }

    /**
     * Inserts a Node<T> at the specified position in the child list. Will     * throw an ArrayIndexOutOfBoundsException if the index does not exist.
     *
     * @param index the position to insert at.
     * @param child the Node<T> object to insert.
     * @throws IndexOutOfBoundsException if thrown.
     */
    public void insertChildAt(int index, Node<T> child) throws IndexOutOfBoundsException {
        if (index == getNumberOfChildren()) {
            // this is really an append
            addChild(child);
        } else {
            children.get(index); //just to throw the exception, and stop here
            children.add(index, child);
        }
    }

    /**
     * Remove the Node<T> element at index index of the List<Node<T>>.
     *
     * @param index the index of the element to delete.
     * @throws IndexOutOfBoundsException if thrown.
     */
    public void removeChildAt(int index) throws IndexOutOfBoundsException {
        children.remove(index);
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * Used to print the node and its children.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(getData().toString()).append(",[");
        int i = 0;
        for (Node<T> e : getChildren()) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(e.getData().toString());
            i++;
        }
        sb.append("]").append("}");
        return sb.toString();
    }

    /**
     * Used to print the tree when given the root node.
     */
    public String printTree(StringBuilder sb, int tab) {
        if (this.children == null) {
            sb.append(this.data.toString());//.append(position);// for debugging
        } else {
            for (int i = 0; i < this.children.size(); i++) {
                sb.append("\n");
                for (int j = 0; j < tab; j++) sb.append("| ");
                sb.append(this.data.toString()).append(" = ").append(i).append(" :");//.append(position)
                this.children.get(i).printTree(sb, tab + 1);
            }
        }
        return sb.toString();
    }

    /**
     * Retrieve the node at a given position
     *
     * @param pos the position of the target node at this node's family tree
     */
    public Node<T> findNodeAt(int pos) {
        Node<T> node = null;
        if (this.children != null) {
            if (this.position == pos) {
                return this;
            }
            for (Node<T> nd : this.children) {
                node = nd.findNodeAt(pos);
                if (node != null) break;
            }
        }
        return node;
    }

    /**
     * find if a given data item matches the decision tree or not
     */
    public boolean match(Item item) {
        NodeData nodeData = (NodeData) this.data;
        if (children == null) {
            return nodeData.classifier == item.getAttributeValue(Item.CLASS_INDEX);
        } else {
            Node<T> child = children.get(item.getAttributeValue(nodeData.bestAttributeIndex));
            return child.match(item);
        }
    }

    /**
     * Order the non leaf nodes in this tree return the total number of non-leaf node
     * Store the order number in position, let all leaf node's position = -1
     */
    public int orderNodes(int start) {
        if (this.children != null) {
            position = start + 1;
            int sum = position;
            for (Node<T> child : children) {
                sum = child.orderNodes(sum);
            }
            return sum;
        }
        return start;
    }

    /**
     * Copy the entire tree rooted at this node include the statistics stored in node.data
     */
    public Node<NodeData> copyTree() {
        Node<NodeData> nTree = this.copyNode();
        if (children != null) {
            for (Node<T> child : children) {
                nTree.addChild(child.copyTree());
            }
        }
        return nTree;
    }

    private Node<NodeData> copyNode() {
        NodeData data = (NodeData) this.data;
        NodeData nData = new NodeData(null, data.attributeMask, data.negNum, data.posNum);
        nData.bestAttributeIndex = data.bestAttributeIndex;
        nData.classifier = data.classifier;
        return new Node<>(nData);
    }
}