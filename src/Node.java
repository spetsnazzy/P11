/*  a Node holds one node of a parse tree
    with several pointers to children used
    depending on the kind of node
*/

import java.util.*;
import java.awt.*;
import java.util.List;

public class Node {

    public static int count = 0;  // maintain unique id for each node

    private int id;

    private String kind;  // non-terminal or terminal category for the node
    private String info;  // extra information about the node such as
    // the actual identifier for an I

    // references to children in the parse tree
    private Node first, second, third;

    // stack of memories for all pending calls
    private static ArrayList<MemTable> memStack = new ArrayList<>();
    // convenience reference to top MemTable on stack
    private static MemTable table = new MemTable();

    // value being returned
    private static double returnValue = 0;

    private static Node root;  // root of the entire parse tree

    private static Scanner keys = new Scanner(System.in);

    // construct a common node with no info specified
    public Node(String k, Node one, Node two, Node three) {
        kind = k;
        info = "";
        first = one;
        second = two;
        third = three;
        id = count;
        count++;
        System.out.println(this);
    }

    // construct a node with specified info
    public Node(String k, String inf, Node one, Node two, Node three) {
        kind = k;
        info = inf;
        first = one;
        second = two;
        third = three;
        id = count;
        count++;
        System.out.println(this);
    }

    // construct a node that is essentially a token
    public Node(Token token) {
        kind = token.getKind();
        info = token.getDetails();
        first = null;
        second = null;
        third = null;
        id = count;
        count++;
        System.out.println(this);
    }

    public String toString() {
        return "#" + id + "[" + kind + "," + info + "]<" + nice(first) +
                " " + nice(second) + ">";
    }

    public String nice(Node node) {
        if (node == null) {
            return "-";
        } else {
            return "" + node.id;
        }
    }

    // produce array with the non-null children
    // in order
    private Node[] getChildren() {
        int count = 0;
        if (first != null) count++;
        if (second != null) count++;
        if (third != null) count++;
        Node[] children = new Node[count];
        int k = 0;
        if (first != null) {
            children[k] = first;
            k++;
        }
        if (second != null) {
            children[k] = second;
            k++;
        }
        if (third != null) {
            children[k] = third;
            k++;
        }

        return children;
    }

    //******************************************************
    // graphical display of this node and its subtree
    // in given camera, with specified location (x,y) of this
    // node, and specified distances horizontally and vertically
    // to children
    public void draw(Camera cam, double x, double y, double h, double v) {

        System.out.println("draw node " + id);

        // set drawing color
        cam.setColor(Color.black);

        String text = kind;
        if (!info.equals("")) text += "(" + info + ")";
        cam.drawHorizCenteredText(text, x, y);

        // positioning of children depends on how many
        // in a nice, uniform manner
        Node[] children = getChildren();
        int number = children.length;
        System.out.println("has " + number + " children");

        double top = y - 0.75 * v;

        if (number == 0) {
            return;
        } else if (number == 1) {
            children[0].draw(cam, x, y - v, h / 2, v);
            cam.drawLine(x, y, x, top);
        } else if (number == 2) {
            children[0].draw(cam, x - h / 2, y - v, h / 2, v);
            cam.drawLine(x, y, x - h / 2, top);
            children[1].draw(cam, x + h / 2, y - v, h / 2, v);
            cam.drawLine(x, y, x + h / 2, top);
        } else if (number == 3) {
            children[0].draw(cam, x - h, y - v, h / 2, v);
            cam.drawLine(x, y, x - h, top);
            children[1].draw(cam, x, y - v, h / 2, v);
            cam.drawLine(x, y, x, top);
            children[2].draw(cam, x + h, y - v, h / 2, v);
            cam.drawLine(x, y, x + h, top);
        } else {
            System.out.println("no Node kind has more than 3 children???");
            System.exit(1);
        }

    }// draw

    public static void error(String message) {
        System.out.println(message);
        System.exit(1);
    }

    // ===============================================================
    //   execute/evaluate nodes
    // ===============================================================

    // ask this node to execute itself
    // (for nodes that don't return a value)
//    public void execute() {
//
////      System.out.println("Executing node " + id + " of kind " + kind );
//
//        if (kind.equals("program")) {
//            root = this;  // note the root node of entire tree
//            first.execute();  // execute the "main" funcCall
//        }// program
//
//        else if (kind.equals("stmts")) {
//            first.execute();
//            // returning is a flag saying that first
//            // wants to return, so don't do this node's second
//            if (second != null && !returning) {
//                second.execute();
//            }
//        }// stmts
//
//        else if (kind.equals("funcCall")) {
//            // execute a function call as a statement
//
//            String funcName = info;
//
//            // handle bifs
//            if (funcName.equals("print")) {
//                // evaluate the single <expr>
//                double value = first.first.evaluate().getNum();
//                if ((int) value == value)
//                    System.out.print((int) value);
//                else
//                    System.out.print(value);
//            } else if (funcName.equals("nl")) {
//                System.out.println();
//            } else {// user-defined function
//
//                Node body = passArgs(this, funcName);
//                body.second.execute();
//
//                returning = false;
//
//            }// user-defined function
//
//        }// funcCall
//
//        else if (kind.equals("str")) {
//            System.out.print(info);
//        }// str
//
//        else if (kind.equals("sto")) {
//            double value = first.evaluate().getNum();
//            table.store(info, value);
//        }// sto
//
//        else if (kind.equals("if")) {
//            double question = first.evaluate().getNum();
//            if (question != 0) {
//                second.execute();
//            } else {
//                third.execute();
//            }
//        }// if
//
//        else if (kind.equals("return")) {
//            returnValue = first.evaluate().getNum();
//            // System.out.println("return value is set to " + returnValue );
//
//            returning = true;
//
//            // manage memtables
//            // pop the top mem table
//            memStack.remove(memStack.size() - 1);
//
//            // convenience note new top (if any)
//            if (memStack.size() > 0)
//                table = memStack.get(memStack.size() - 1);
//            else {// notice program is over
//                System.out.println(".......execution halting");
//                System.exit(0);
//            }
//
//        }// return
//
//        else {
//            error("Executing unknown kind of node [" + kind + "]");
//        }
//
//    }// execute

    public static double evaluateDouble(double n){

        return 0;
    }

    public static List<Double> evaluateList(List<Double> li){

        return null;
    }

    // needs to return Pist objects
    public Pist evaluate(Pist p) {
        Pist sub = p; // use sub to make changes to, so we don't change input

//      System.out.println("Evaluating node " + id + " of kind " + kind );
        if(p.next != null){
            /* Need to find a way to get the value of top level pist,
             * find the operation for said pist, then go to the next pist.
             * This will allow us to go deeper in to the pists, evaluate them,
             * change their next to null, then change their value to
             * what it was equating to.
             * We may need to rewrite all of evaluate
            */
        }
        if (kind.equals("var")) {
            return new Pist(table.retrieve(info));
        }// var
        else if (kind.equals("num")) {
            return new Pist(Double.parseDouble(info));
        }
        else if (kind.equals("+") || kind.equals("-")) {
            double value1 = first.evaluate().getNum();
            double value2 = second.evaluate().getNum();
            if (kind.equals("+"))
                return new Pist(value1 + value2);
            else
                return new Pist(value1 - value2);
        }
        else if (kind.equals("*") || kind.equals("/")) {
            double value1 = first.evaluate().getNum();
            double value2 = second.evaluate().getNum();
            if (kind.equals("*"))
                return new Pist(value1 * value2);
            else
                return new Pist(value1 / value2);
        }
        else if (kind.equals("opp")) {
            double value = first.evaluate().getNum();
            return new Pist(-value);
        }
        else if (kind.equals("funcCall")) {
            // execute a function call to produce a value

            String funcName = info;

            double value;  // have all function calls put their value here
            // to return once at the bottom

            // handle bifs

            if (member(funcName, bif0)) {
                if (funcName.equals("input"))
                    value = keys.nextDouble();
                else {
                    error("unknown bif0 name [" + funcName + "]");
                    value = -1;
                }
            }
            else if (member(funcName, bif1)) {
                double arg1 = first.first.evaluate().getNum();

                if (funcName.equals("sqrt"))
                    value = Math.sqrt(arg1);
                else if (funcName.equals("cos"))
                    value = Math.cos(Math.toRadians(arg1));
                else if (funcName.equals("sin"))
                    value = Math.sin(Math.toRadians(arg1));
                else if (funcName.equals("atan"))
                    value = Math.toDegrees(Math.atan(arg1));
                else if (funcName.equals("round"))
                    value = Math.round(arg1);
                else if (funcName.equals("trunc"))
                    value = (int) arg1;
                else if (funcName.equals("not"))
                    value = arg1 == 0 ? 1 : 0;
                else if (funcName.equals("null"))
                    if (arg1 == 0)
                        value = 1;
                    else
                        value = 0;
                else if (funcName.equals("num")) {
                    if (Double.toString(arg1).matches("-?\\d+(\\.\\d+)?"))
                        value = 1;
                    else
                        value = 0;
                }
                else if (funcName.equals("list")) {
                    if (arg1 instanceof Collection<?>) {
                        value = 1;
                    }
                    else {
                        error("unknown bif1 name [" + funcName + "]");
                        value = 0;
                    }
                }
                else if (member(funcName, bif2)) {
                    arg1 = first.first.evaluate().getNum();
                    double arg2 = first.second.first.evaluate();

                    if (funcName.equals("lt"))
                        value = arg1 < arg2 ? 1 : 0;
                    else if (funcName.equals("le"))
                        value = arg1 <= arg2 ? 1 : 0;
                    else if (funcName.equals("eq"))
                        value = arg1 == arg2 ? 1 : 0;
                    else if (funcName.equals("ne"))
                        value = arg1 != arg2 ? 1 : 0;
                    else if (funcName.equals("pow"))
                        value = Math.pow(arg1, arg2);
                    else if (funcName.equals("and"))
                        value = arg1 != 0 && arg2 != 0 ? 1 : 0;
                    else if (funcName.equals("or"))
                        value = arg1 != 0 || arg2 != 0 ? 1 : 0;
                    else if (funcName.equals("plus"))
                        value = arg1 + arg2;
                    else if (funcName.equals("minus"))
                        value = arg1 - arg2;
                    else if (funcName.equals("times"))
                        value = arg1 * arg2;
                    else if (funcName.equals("div"))
                        value = arg1 / arg2;
                    else {
                        error("unknown bif2 name [" + funcName + "]");
                        value = -1;
                    }
                }
                else {// user-defined function

                    Node body = passArgs(this, funcName);
                    body.second.execute();

                    value = returnValue;

                    returning = false;
                }// user-defined function call

                // uniformly finish
                return value;
            }// funcCall

            else {
                error("Evaluating unknown kind of node [" + kind + "]");
                return -1;
            }

        }// evaluate
        return 0;
    }
    private final static String[] bif0 = {"input", "nl"};
    private final static String[] bif1 = {"sqrt", "cos", "sin", "atan",
                                            "round", "trunc", "not"};
    private final static String[] bif2 = {"lt", "le", "eq", "ne", "pow",
                "or", "and"
    };

    // return whether target is a member of array
    private static boolean member (String target, String[]array ){
        for (int k = 0; k < array.length; k++) {
            if (target.equals(array[k])) {
                return true;
            }
        }
        return false;
    }

    // given a funcCall node, and for convenience its name,
    // locate the function in the function defs and
    // create new memory table with arguments values assigned
    // to parameters
    // Also, return root node of body of the function being called
    private static Node passArgs (Node funcCallNode, String funcName ){

        // locate the function in the function definitions

        Node node = root;  // the program node
        node = node.second;  // now is the funcDefs node
        Node fdnode = null;
        while (node != null && fdnode == null) {
            if (node.first.info.equals(funcName)) {// found it
                fdnode = node.first;
                // System.out.println("located " + funcName + " at node " +
                //                     fdnode.id );
            } else {
                node = node.second;
            }
        }

        MemTable newTable = new MemTable();

        if (fdnode == null) {// function not found
            error("Function definition for [" + funcName + "] not found");
            return null;
        } else {// function name found
            Node pnode = fdnode.first; // current params node
            Node anode = funcCallNode.first;  // current args node
            while (pnode != null && anode != null) {
                // store argument value under parameter name
                newTable.store(pnode.first.info,
                        anode.first.evaluate());
                // move ahead
                pnode = pnode.second;
                anode = anode.second;
            }

            // detect errors
            if (pnode != null) {
                error("there are more parameters than arguments");
            } else if (anode != null) {
                error("there are more arguments than parameters");
            }

//         System.out.println("at start of call to " + funcName +
//                           " memory table is:\n" + newTable );

            // manage the memtable stack
            memStack.add(newTable);
            table = newTable;

            return fdnode;

        }// function name found
    }// passArguments

}// Node