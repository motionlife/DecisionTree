import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Hao Xiong on 1/26/2017.
 * A class contains most of the statistic about one section of data items
 */
public class NodeData {

    //The values in this set, should be released after splitting to save memory
    public ArrayList<Item> items;

    //Some patterns characterizing a certain values set.
    public long negNum;
    public long posNum;
    public byte classifier;// 0 means negative, 1 means positive, -1 means not pure

    //The remaining attributes that has not been chosen to split the values set
    public byte[] attributeMask;

    //The index of the attribute that will be used to split the set best.
    //After the tree was build this will index the last 0 byte generated in attributeMask
    public int bestAttributeIndex;

    public NodeData(ArrayList<Item> items) {
        this(items, Item.FULLMASK, 1, 1);
    }

    public NodeData(ArrayList<Item> items, byte[] mask, long neg, long pos) {
        //initialization when construct this values set
        this.items = items;
        this.attributeMask = mask;
        this.negNum = neg;
        this.posNum = pos;
        //this.bestAttributeIndex = bestIndex;
        this.classifier = -1;//set the default data as not pure.
    }

    /**
     * Check if all the attributes had been used.
     */
    public boolean isAttributesEempty() {
        for (byte b : this.attributeMask) {
            if (b != 0) return false;
        }
        return true;
    }

    public boolean isPure() {
        return classifier != -1;
    }

    /**
     * return if this data set is empty
     */
    public boolean isEmpty() {
        return posNum + negNum == 0;
    }

    /**
     * Get the purified representation of this data set that is purifying it by the
     * dominate target value, make it to leaf data
     */
    public NodeData purify() {
        if (this.negNum < this.posNum) {
            classifier = 1;//represent positive leaf node
        } else {
            classifier = 0;//represent negative leaf
        }
        return this;
    }

    /**
     * Classify the data when just finished splitting if needed
     */
    public NodeData classify() {
        if (this.posNum == 0 || negNum == 0) {
            this.purify();
        } else {
            this.classifier = -1;
        }
        return this;
    }

    /**
     * Method used the given heuristic to split the current values set into multiple
     * sub-sets based on the chosen attribute. This is the core method of the program
     * How to reclaim the heap memory occupied by the old values set ds??? Something must be done after splitting
     */
    public NodeData[] splitSet(String heuristic) {

        ArrayList<Item> items0 = new ArrayList<>();
        ArrayList<Item> items1 = new ArrayList<>();
        int bestIndex = 0;
        double min_metric = 2;
        long feature_0_neg = 0, feature_0_pos = 0, feature_1_neg = 0, feature_1_pos = 0;
        long size = this.items.size();

        for (int i = 0; i < attributeMask.length; i++) {
            if (attributeMask[i] != 0 && size != 0) {
                //Calculate some statistics in data set items.
                long feature_0 = 0, f0_neg = 0, feature_1 = 0, f1_pos = 0;
                for (Item itm :
                        this.items) {
                    if (itm.getAttributeValue(i) == 0) {
                        feature_0++;
                        if (itm.getAttributeValue(Item.CLASS_INDEX) == 0) f0_neg++;
                    } else {
                        feature_1++;
                        if (itm.getAttributeValue(Item.CLASS_INDEX) != 0) f1_pos++;
                    }
                }
                double total_entropy = ((double) feature_0 / size) * this.calImpurity(f0_neg, feature_0 - f0_neg, heuristic)
                        + ((double) feature_1 / size) * this.calImpurity(feature_1 - f1_pos, f1_pos, heuristic);

                //System.out.println(total_entropy);//-------------------------------DEBUG-----------------------------

                if (total_entropy < min_metric) {
                    bestIndex = i;
                    min_metric = total_entropy;
                    feature_0_neg = f0_neg;
                    feature_0_pos = feature_0 - f0_neg;
                    feature_1_pos = f1_pos;
                    feature_1_neg = feature_1 - f1_pos;
                }
            }
        }

        //Split the items into to sub items set based on the best attribute value
        if (items != null) {
            for (Item itm : this.items) {
                if (itm.getAttributeValue(bestIndex) == 0) {
                    items0.add(itm);
                } else {
                    items1.add(itm);
                }
            }
        }

        //release useless memory: set the List reference to null to allow the garbage collector to potentially reclaim this object
        //items = null;

        //Save the index of the chosen best attribute
        this.bestAttributeIndex = bestIndex;

        //mask out the used attribute
        attributeMask[bestIndex] = 0;

        return new NodeData[]{new NodeData(items0, Arrays.copyOf(attributeMask, attributeMask.length), feature_0_neg, feature_0_pos).classify(),
                new NodeData(items1, Arrays.copyOf(attributeMask, attributeMask.length), feature_1_neg, feature_1_pos).classify()};
    }

    /**
     * Returns a string summary of these data in this node,i.e node label of the output tree
     */
    public String toString() {
        String label;
        switch (classifier) {
            case 0:
                label = " 0";// + negNum + ")";
                break;
            case 1:
                label = " 1";// + posNum + ")";
                break;
            case -1:
                label = Item.ATTRIBUTES[bestAttributeIndex];// + "(" + (posNum + negNum) + ")";
                break;
            default:
                label = "error";
        }
        return label;
    }

    /**
     * Calculate the impurity regarding to the given heuristic
     * Define of Entropy from information theory
     * and the define of variance impurity given by the homework
     */
    private double calImpurity(double neg, double pos, String heuristic) {
        double metric = 0;
        //Notice that the impurity is 0 when the data is pure.
        if (neg != 0 && pos != 0) {
            double total = neg + pos;
            double p0 = neg / total;
            double p1 = pos / total;
            if (heuristic.equals(DecisionTree.SPLIT_INFO_GAIN)) {
                //Base on the definition of entropy
                metric = -1 * p0 * (Math.log(p0) / Math.log(2)) - p1 * (Math.log(p1) / Math.log(2));

            } else if (heuristic.equals(DecisionTree.SPLIT_VARIANCE_IMP)) {
                //Based on the definition of variance impurity
                metric = (p0 * p1) / (total * total);
            }
        }
        return metric;
    }

}

/**
 * The values item class
 **/
class Item {

    //set the default names of the attributes of the values set to be learned.
    public static String[] ATTRIBUTES
            = {"XB", "XC", "XD", "XE", "XF", "XG", "XH", "XI", "XJ", "XK", "XL", "XM", "XN", "XO", "XP", "XQ", "XR", "XS", "XT", "XU", "CLASS"};

    //The initial full mask;
    public static byte[] FULLMASK = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    //The default target attribute index is 20 based on the given data set
    public static int CLASS_INDEX = 20;

    /**
     * Use the byte array to store every data item to save memory
     */
    public byte[] values;

    Item(byte[] values) {
        this.values = values;
    }

    public byte getAttributeValue(int index) {
        return this.values[index];
    }

    /**
     * Set the names of attributes and the index of the target attribute.
     * target = 0-->negative; target = 1-->positive
     */
    public static void setAttributes(String[] names, String target) {
        Item.ATTRIBUTES = names;
        //"CLASS" is in the last column of the headline;
        for (int i = 0; i < names.length; i++) {
            if (target.equals(names[i])) {
                Item.CLASS_INDEX = i;
            }
        }
    }
}
