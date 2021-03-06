import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

enum Color { red, black }

public class HW4 {
    public static void main(String[] args) throws IOException {
        File dir = new File("./input/");
        File[] fileList = dir.listFiles();
        try{
            for(int i = 0 ; i < fileList.length ; i++){
                File file = fileList[i];
                if(file.isFile()) {
                    BufferedReader br = new BufferedReader(new FileReader(file.getPath()));

                    RBTree t = new RBTree();
                    int n;
                    while (true) {
                        String line = br.readLine();
                        line.replaceAll(" ", "");
                        if (line == null)
                            break;
                        n = Integer.parseInt(line);

                        if (n == 0) {
                            break;
                        }
                        else {
                            if (n > 0)
                                t.rbInsert(n);
                            else {
                                if (t.isWrongDeletion(Math.abs(n))) {
                                    // System.out.println("Wrong deletion: " + Math.abs(n));
                                    t.deleteErrorCount++;
                                }
                                else
                                    t.rbDelete(-1 * n);
                            }
                        }
                    }

                    System.out.println("filename = " + file.getName());
                    System.out.println("total = " + t.nodeNum);
                    System.out.println("insert = " + t.insertCount);
                    System.out.println("deleted = " + t.deleteCount);
                    System.out.println("miss = " + t.deleteErrorCount);
                    System.out.println("nb = " + t.countBNodeNum());
                    System.out.println("bh = " + t.countBHeight());
                    t.print(t.root);

                    br.close();

                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

class RBTree {
    class Node {
        int val;
        Node left, right;
        Node parent;
        int color;

        Node(int newval) {
            val = newval;
            left = nil;
            right = nil;
            parent = nil;
            color = black;
        }
    }

    int nodeNum = 0;
    int bNodeNum = 0;
    int insertCount = 0;
    int deleteCount = 0;
    int deleteErrorCount = 0;

    final int red = 0;
    final int black = 1;
    public Node root;
    final Node nil = new Node(-1);
    public RBTree() {
        root = nil;
    }

    public void rbInsert(int newval) {
        rbInsert(new Node(newval));
        nodeNum++;
        insertCount++;
    }
    public void rbInsert(Node z) {
        Node y = nil;
        Node x = root;
        while (x != nil) {
            y = x;
            if (z.val < x.val)
                x = x.left;
            else
                x = x.right;
        }
        z.parent = y;
        if (y == nil)
            root = z;
        else if (z.val < y.val)
            y.left = z;
        else
            y.right = z;
        z.color = red;
        rbInsertFixup(z);
    }
    public void rbInsertFixup(Node z) {
        Node y;
        while (z.parent.color == red) {
            if (z.parent == z.parent.parent.left) {
                y = z.parent.parent.right;
                if (y.color == red) {
                    z.parent.color = black;
                    y.color = black;
                    z.parent.parent.color = red;
                    z = z.parent.parent;
                }
                else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.color = black;
                    z.parent.parent.color = red;
                    rightRotate(z.parent.parent);
                }
            }
            else {
                y = z.parent.parent.left;
                if (y.color == red) {
                    z.parent.color = black;
                    y.color = black;
                    z.parent.parent.color = red;
                    z = z.parent.parent;
                }
                else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = black;
                    z.parent.parent.color = red;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = black;
    }

    public void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != nil) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == nil)
            root = y;
        else if (x == x.parent.left)
            x.parent.left = y;
        else
            x.parent.right = y;
        y.left = x;
        x.parent = y;
    }
    public void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != nil) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == nil)
            root = y;
        else if (x == x.parent.right)
            x.parent.right = y;
        else
            x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    public Node treeSearch (Node tree, int val) {
        if (tree == nil)
            return nil;
        else if (val == tree.val)
            return tree;
        else if (val < tree.val)
            return treeSearch(tree.left,val);
        else
            return treeSearch(tree.right,val);
    }
    public void rbDelete(int newval) {
        Node z = treeSearch(root, newval);
        rbDelete(z);
        nodeNum--;
        deleteCount++;
    }
    public void rbDelete(Node z) {
        Node x;
        Node y = z;
        int originalColor = y.color;
        if (z.left == nil) {
            x = z.right;
            rbTransplant(z, z.right);
        }
        else if (z.right == nil) {
            x = z.left;
            rbTransplant(z, z.left);
        }
        else {
            y = treeMinimum(z.right);
            originalColor = y.color;
            x = y.right;
            if (y.parent == z)
                x.parent = y;
            else {
                rbTransplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            rbTransplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        if (originalColor == black)
            rbDeleteFixup(x);
    }
    public void rbDeleteFixup(Node x) {
        Node w;
        while (x != root && x.color == black) {
            if (x == x.parent.left) {
                w = x.parent.right;
                if (w.color == red) {
                    w.color = black;
                    x.parent.color = red;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (w.left.color == black && w.right.color == black) {
                    w.color = red;
                    x = x.parent;
                }
                else {
                    if (w.right.color == black) {
                        w.left.color = black;
                        w.color = red;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    w.color = x.parent.color;
                    x.parent.color = black;
                    w.right.color = black;
                    leftRotate(x.parent);
                    x = root;
                }
            }
            else {
                w = x.parent.left;
                if (w.color == red) {
                    w.color = black;
                    x.parent.color = red;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (w.right.color == black && w.left.color == black) {
                    w.color = red;
                    x = x.parent;
                }
                else {
                    if (w.left.color == black) {
                        w.right.color = black;
                        w.color = red;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = black;
                    w.left.color = black;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = black;
    }
    public void rbTransplant(Node u, Node v) {
        if (u.parent == nil)
            root = v;
        else if (u == u.parent.left)
            u.parent.left = v;
        else
            u.parent.right = v;
        v.parent = u.parent;
    }
    public Node treeMinimum(Node x) {
        while (x.left != nil)
            x = x.left;
        return x;
    }

    public void print(Node tree) {
        if (tree.left != nil)
            print(tree.left);

        System.out.print(tree.val);
        if (tree.color == red)
            System.out.println(" R");
        else
            System.out.println(" B");

        if (tree.right != nil)
            print(tree.right);
    }
    public void inorder(Node tree){
        if (tree.right != nil)
            inorder(tree.right);
        if (tree.color == black) {
            bNodeNum++;
        }
        if (tree.left != nil)
            inorder(tree.left);
    }
    public int countBNodeNum() {
        bNodeNum = 0;
        inorder(root);
        return bNodeNum;
    }

    public int countBHeight() {
        Node x = root;
        int count = 0;
        while (x != nil) {
            if (x.color == black)
                count++;
            x = x.left;
        }
        return count;
    }
    public boolean isWrongDeletion(int n) {
        return treeSearch(root, n) == nil;

    }
}