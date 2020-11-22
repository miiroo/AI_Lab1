public class Tree {

    public Node root;

    //0 - right 1 - left
    public  Node insertNode(Node toInsert, boolean leftOrRight, Node parent) {
        if (parent == null) {
            root = toInsert;
            return  root;
        } else {
            if (leftOrRight) {
                parent.leftChild = toInsert;
            }
            else parent.rightChild = toInsert;
            return toInsert;
        }
    }
}


/*
  public  void printTree(){

        GraphW app = new GraphW();
        app.setSize(1200,800);
        app.setVisible(true);

        int xPos = 0;
        int yPos = 0;
        Stack<Node> globalStack = new Stack<Node>();
        globalStack.push(decisionTree.root);
        int emptyLeaf = 600 ;
        boolean isRowEmpty = false;
        while (isRowEmpty==false) {
            Stack<Node> localStack = new Stack<Node>();
            isRowEmpty = true;
            for(int j= 0; j<emptyLeaf; j++)
              xPos++;
            while(globalStack.isEmpty()==false) {
                Node temp = globalStack.pop();
                if(temp != null) {
                    JLabel lab;
                    if(!temp.isLeaf)
                    lab = new JLabel(temp.attrIndex+"<"+temp.key);
                    else lab = new JLabel(temp.attrOrClass);
                    app.labelList.add(lab);
                    app.labelList.get(app.labelList.size()-1).setBounds(xPos,yPos, 40,10);
                    app.labelList.get(app.labelList.size()-1).setFont(new Font("TimesRoman", Font.BOLD, 10));
                    localStack.push(temp.leftChild);
                    localStack.push(temp.rightChild);
                    emptyLeaf -= 40;
                    if(temp.leftChild != null || temp.rightChild != null) isRowEmpty = false;
                }
                else {
                    JLabel lab = new JLabel("--");
                    app.labelList.add(lab);
                    app.labelList.get(app.labelList.size()-1).setBounds(xPos,yPos, 50,10);
                    app.labelList.get(app.labelList.size()-1).setFont(new Font("TimesRoman", Font.BOLD, 10));
                    emptyLeaf -= 20;
                    localStack.push(null);
                    localStack.push(null);
                }
                for(int j=0;j<60;j++) xPos++;
            }
            xPos = 0;
            yPos += 8;
            //System.out.println();

            while (localStack.isEmpty()==false)
                globalStack.push(localStack.pop());
        }
        app.Show();
        app.setVisible(true);
    }
    */

/*
 if () {
                        /*for (int j = 0; j < datas.size(); j++) {
                            countAvF += Float.parseFloat(datas.get(j)[i]);
                        }
                        countAvF = countAvF / datas.size();//
         bestDelim[i] = countAvF;
         for (int j = 0; j < datas.size(); j++) {
        if (Float.parseFloat(datas.get(j)[i]) < countAvF) {
        leftSide.add(datas.get(j));
        } else //>countAv
        rightSide.add(datas.get(j));
        }

        beforeDiv = getEntropy(datas, classID, 1);
        arrOfEntr[i] = beforeDiv - ((leftSide.size()/datas.size())*getEntropy(leftSide, classID, 1)+
        (rightSide.size()/datas.size())*getEntropy(rightSide,  classID, 1));

        //  beforeDiv = getEntropy(datas, i, countAvF);
        //   arrOfEntr[i] = beforeDiv - ((leftSide.size()/datas.size())*getEntropy(leftSide, i, countAvF)+(rightSide.size()/datas.size())*getEntropy(rightSide,  i, countAvF));
       // }
        */
