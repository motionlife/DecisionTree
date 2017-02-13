/**
 * Created by Hao Xiong on 1/28/2017.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileParser {

    public static ArrayList<Item> parseCSV(String fileName) {

        String line;
        String csvSplitter = ",";
        ArrayList<Item> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            /**Get the names of those feature stored in the first line of the csv file*/
            line = br.readLine();
            String[] names = line.split(csvSplitter);
            /**The attribute "Class" is the last column of the parsed data set;*/
            Item.setAttributes(names, names[20]);

            while ((line = br.readLine()) != null) {

                String[] values = line.split(csvSplitter);
                byte[] item = new byte[values.length];
                for (int i = 0; i < item.length; i++) {
                    item[i] = (byte) (values[i].equals("0") ? 0 : 1);
                }
                data.add(new Item(item));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
