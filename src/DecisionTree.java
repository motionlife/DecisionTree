/**
 * Created by Hao Xiong on 1/26/2017.
 */

import java.util.ArrayList;

public class DecisionTree {
    /**
     * Indicates that attributes for splitting are selected
     * based on Variance impurity.
     */
    public static final String SPLIT_VARIANCE_IMP = "Variance_Impurity";

    /**
     * Indicates that attributes for splitting are selected
     * based on maximum information gain.
     */
    public static final String SPLIT_INFO_GAIN = "Information_Gain";


    public static void main(String[] args) {
        //debug();
        production(args);
    }

    private static void production(String[] args) {
        int L, K;
        String trainingSet, validationSet, testSet;
        boolean printTree;
        try {
            L = Integer.parseInt(args[0]);
            K = Integer.parseInt(args[1]);
            trainingSet = chooseSet(args[2]) + "/training_set.csv";
            validationSet = chooseSet(args[3]) + "/validation_set.csv";
            testSet = chooseSet(args[4]) + "/test_set.csv";
            printTree = args[5].toLowerCase().equals("yes");
        } catch (Exception e) {
            System.out.println("Wrong Input Parameters, programs runs on the default setting! Please follow the format bellow:\n" +
                    ".\\DecisionTree <L> <K> <training-set> <validation-set> <test-set> <to-print>\n" +
                    "            L: maximum number of post-pruning trials\n" +
                    "            K: maximum number of nodes want to prune in each pruning trial\n" +
                    "            training-set: {set1, set2}\n" +
                    "            validation-set: {set1, set2}\n" +
                    "            test-set: {set1, set2}\n" +
                    "            to-print: {yes no}");
            L = 1500;
            K = 7;
            trainingSet = "data_sets1/training_set.csv";
            validationSet = "data_sets1/validation_set.csv";
            testSet = "data_sets1/test_set.csv";
            printTree = false;
        }

        NodeData training_data = new NodeData(FileParser.parseCSV(trainingSet));
        NodeData validation_data = new NodeData(FileParser.parseCSV(validationSet));
        NodeData test_data = new NodeData(FileParser.parseCSV(testSet));

        ID3 id3 = new ID3();
        /**heuristic = Information gain*/
        Node<NodeData> treeIG = id3.growDecisionTree(training_data, DecisionTree.SPLIT_INFO_GAIN);
        Node<NodeData> prunedTreeIG = id3.postPruningTree(L, K, treeIG, validation_data);
        double acIgNp = id3.accuracyTest(treeIG, test_data);
        double acIgPp = id3.accuracyTest(prunedTreeIG, test_data);

        /**heuristic = Variance impurity*/
        Node<NodeData> treeVI = id3.growDecisionTree(training_data, DecisionTree.SPLIT_VARIANCE_IMP);
        Node<NodeData> prunedTreeVI = id3.postPruningTree(L, K, treeVI, validation_data);
        double acViNp = id3.accuracyTest(treeVI, test_data);
        double acViPp = id3.accuracyTest(prunedTreeVI, test_data);

        System.out.println("Accuracy Table: L = " + L + ", K = " + K);
        System.out.println("Training-set: " + trainingSet + "; Validation-set: " + validationSet + "; Test-set: " + testSet);
        System.out.println("Print pruned tree: " + (printTree ? "Yes" : "No"));
        outputTable(new double[]{acIgNp, acIgPp, acViNp, acViPp});
        if (printTree) {
            System.out.format("\n+---------------+---Decision Tree (Information Gain) ---+---------------+%n");
            System.out.println(prunedTreeIG.printTree(new StringBuilder(), 0));
            System.out.format("\n+---------------+---Decision Tree (Variance Impurity)---+--------------+%n");
            System.out.println(prunedTreeVI.printTree(new StringBuilder(), 0));
        }

    }

    private static String chooseSet(String set) {
        return set.contains("1") ? "data_sets1" : "data_sets2";
    }

    private static void outputTable(double[] data) {
        System.out.format("+-------------------+----------------+--------------+%n");
        System.out.format("|    Heuristics     |  No Pruning    | Post-pruning |%n");
        System.out.format("+-------------------+----------------+--------------+%n");
        System.out.format("| Information Gain  |  %-13f | %-12f |%n", data[0], data[1]);
        System.out.format("| Variance Impurity |  %-13f | %-12f |%n", data[2], data[3]);
        System.out.format("+-------------------+----------------+--------------+%n");
    }


    /**
     * The debug method used to debug this project
     */
    public static void debug() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item(new byte[]{1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1}));
        items.add(new Item(new byte[]{1, 0, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0}));
        //conflict data
        items.add(new Item(new byte[]{1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0}));
        items.add(new Item(new byte[]{1, 0, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1}));

//        NodeData training_data = new NodeData(FileParser.parseCSV("data_sets1/training_set.csv"));
//        NodeData validation = new NodeData(FileParser.parseCSV("data_sets1/validation_set.csv"));

        ID3 id3 = new ID3();
        Node<NodeData> tree = id3.growDecisionTree(new NodeData(items), DecisionTree.SPLIT_INFO_GAIN);
        System.out.println("Total Nodes in the Tree: " + tree.orderNodes(0));
        System.out.println(tree.printTree(new StringBuilder(), 0));

        //id3.postPruningTree(10, 7, tree, validation);

        //System.gc();
        testMemory();
    }

    public static void testMemory() {

        double mb = 1024 * 1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }
}
