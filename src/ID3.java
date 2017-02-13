import java.util.ArrayList;

/**
 * Created by Hao Xiong on 1/26/2017.
 * <p>
 * This class contains the method which used to implement the ID3 algorithms
 * referencing the pseudo code in Tom Mitchell's Machine Learning P68.
 */

public class ID3 {
    /**
     * This is the basic ID3 decision tree method implemented from the pseudo code from text book
     */
    public Node<NodeData> growDecisionTree(NodeData nData, String heuristic) {

        Node<NodeData> node = new Node<>(nData);//Create a default leaf node;
        /**
         * If Attributes is empty,
         * Return the single-node tree Root, with label = most common value of Target attribute in Examples
         * */
        if (nData.isAttributesEempty()) {

            nData.purify();//Classify (purify) the leaf node value

        } else if (!nData.isPure()) {
            /**Split the values set using the given heuristic*/
            for (NodeData nda : nData.splitSet(heuristic)) {

                Node<NodeData> child;
                /**
                 * If ExamplesVi is empty:
                 * Then below this new branch add a leaf node with label = most common value of Target attribute in Examples
                 * Else below this new branch add the subtree
                 * */
                if (nda.isEmpty()) {
                    /**This child must be a leaf, so classify (purify) the node value associated with it*/
                    child = new Node<>(new NodeData(null, nda.attributeMask, nData.negNum, nData.posNum).purify());
                } else {
                    /**Let this child grow!*/
                    child = growDecisionTree(nda, heuristic);
                }
                node.addChild(child);
            }
        }
        return node;
    }

    /**
     * Post-pruning algorithm implementation, pseudo code given by HW1
     *
     * @param L user input integer, times of the pruning trial
     * @param K user input integer, the maximum number of nodes been replaced with leaf in each pruning trial
     */
    public Node<NodeData> postPruningTree(int L, int K, Node<NodeData> D_tree, NodeData validation) {
        //Let DBest = D;
        Node<NodeData> bTree = D_tree;
        double bestAccuracy = this.accuracyTest(bTree, validation);
        for (int i = 0; i < L; i++) {
            /**Copy the tree D into a new tree D′*/
            Node<NodeData> pTree = D_tree.copyTree();
            //The maximum nodes been purified at each pruning
            int M = (int) (Math.random() * K + 1);
            for (int j = 0; j < M; j++) {
                int N = pTree.orderNodes(0);
                int P = (int) (Math.random() * N + 1);//to get a random tree node position
                Node<NodeData> pNode = pTree.findNodeAt(P);
                //Replace the subtree rooted at P in D′ by a leaf node.
                if (pNode != null) {
                    pNode.getData().purify();
                    pNode.setChildren(null);
                }
            }
            /**
             * Evaluate the accuracy of D′ on the validation set; accuracy = percentage of correctly classified examples
             */
            double tempAccuracy = this.accuracyTest(pTree, validation);

            if (tempAccuracy > bestAccuracy) {
                bTree = pTree;
                bestAccuracy = tempAccuracy;
            }
        }
        return bTree;
    }

    public double accuracyTest(Node<NodeData> tree, NodeData test) {
        double accuracy = 0;
        ArrayList<Item> data = test.items;
        int matched = 0;
        int total = data.size();
        for (Item itm : data) {
            if (tree.match(itm)) matched++;
        }
        if (total != 0) accuracy = ((double) matched) / total;
        return accuracy;
    }

}