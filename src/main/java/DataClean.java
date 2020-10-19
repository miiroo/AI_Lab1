import java.util.ArrayList;
import java.util.List;

public class DataClean {
    public  List<String[]> cleanData( List<String[]> arrayData) {
        //get attribute count
        String[] row = arrayData.get(0);
        int attrCount = row.length-1;
        for (int i=1; i<arrayData.size(); i++) {
            row = arrayData.get(i);
            for (int j = 0; j < attrCount; j++) {
                if (row[j].equals("")) arrayData.remove(i);
            }
        }
        return arrayData;
    }




    public  List<String[]> fromCatToBinary( List<String[]> arrayData) {
        List<String> wordAttr = new ArrayList();
        String[] row = arrayData.get(1);
        int wordAttrPosition = 0;

        //Find attribute position
        while((row[wordAttrPosition].equals(row[wordAttrPosition].toUpperCase())) && (wordAttrPosition < row.length-1)) {
            wordAttrPosition++;
        }
        if (wordAttrPosition != row.length-1) {
            //Make a list of all word types
            for (int j = 1; j < arrayData.size(); j++) {
                row = arrayData.get(j);
                if (!wordAttr.contains(row[wordAttrPosition]))
                    wordAttr.add(row[wordAttrPosition]);
            }

            //Replace it as binary [e.x. 0 1 0 0]
            for (int j = 1; j < arrayData.size(); j++) {
                row = arrayData.get(j);
                String newStr = "";
                for (int k = 0; k < wordAttr.size(); k++) {
                    if (wordAttr.get(k).equals(row[wordAttrPosition])) {
                        newStr += "1" + " ";
                    } else {
                        newStr += "0" + " ";
                    }
                }
                row[wordAttrPosition] = newStr;
                arrayData.set(j, row);
            }
        }
        return arrayData;
    }
}
