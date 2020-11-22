import java.io.IOException;

public class Node {
    public  float key;
    public  boolean isLeaf = false;
    public  boolean isNode = false;
    public Node leftChild = null;
    public  Node rightChild = null;
    public String attrOrClass;
    public  int attrIndex;


    public void printTree() throws IOException {
        if (rightChild != null) {
            rightChild.printTree(true, "");
        }
        printNodeValue();
        if (leftChild != null) {
            leftChild.printTree(false, "");
        }
    }

    private void printNodeValue() throws IOException {
        if (attrOrClass == "") {
            System.out.print("<null>");
        } else {
            System.out.println(attrOrClass);
        }
        System.out.println();
    }

    private void printTree(boolean isRight, String indent) throws IOException {
        if (rightChild != null) {
            rightChild.printTree(true, indent+(isRight ? "          " : " |        "));
        }
        System.out.print(indent);
        if (isRight) {
            System.out.print(" /");
        } else {
            System.out.print(" \\");
        }
        System.out.print("------- ");
        printNodeValue();
        if (leftChild != null) {
            leftChild.printTree(false, indent+(isRight ? " |        ": "          "  ));
        }
    }
}

