import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap; // import the HashMap class
import java.util.Iterator;
import java.util.List;
import java.lang.Character;

public class RegexEngine {
    static HashMap<Character, Character> operators = new HashMap<Character, Character>();
    static boolean first = true;
    static int opCount = 0;

    // parses given regex and creates a e-nfa out of it
    static Graph parseLine(String line) {
        Graph epsilonNFA = new Graph();
        for(int i = 0 ; i<line.length(); i++){
            Character currentChar = line.charAt(i);

            // handling operators
            if(operators.containsValue(currentChar)){
                //an operator cannot be the first character in a string
                // except for brackets
                if(first == true && currentChar != '('){
                    // invalid input
                    System.out.println("Invalid input detected, operator cannot be first"); 
                    System.exit(1);
                }

                // handling kleene star
                if(currentChar == '*'){
                    // there cannot be an operator behind a kleene star
                    Character previousChar = line.charAt(i-1);
                    if(operators.containsValue(previousChar)){
                        // invalid input
                        System.out.println("Invalid input detected, invalid operator behind operator"); 
                        System.exit(1);
                    }

                    boolean last = false;
                    // check if next char is an operator, if so skip to process operator
                    try{
                        if(operators.containsValue(line.charAt(i+1))){
                            //skip = true;
                        }
                    // must be last character if exception occured
                    } catch(Exception e){
                        // append onto latest node on block
                        epsilonNFA.addNode(epsilonNFA.adj_list.size()-2);
                        // change for alternators later
                        int size = epsilonNFA.adj_list.size();
                        epsilonNFA.addEdge(size-3, size-2, "epsilon");
                        epsilonNFA.addEdge(size-2, size-1, "epsilon");
                        epsilonNFA.addEdge(size-2, size-2, Character.toString(previousChar));
                        last = true;
                    }

                    if(!last){
                        // append onto latest node on block
                        epsilonNFA.addNode(epsilonNFA.adj_list.size()-2);
                        // change for alternators later
                        int size = epsilonNFA.adj_list.size();
                        epsilonNFA.addEdge(size-3, size-2, "epsilon");
                        epsilonNFA.addEdge(size-2, size-2, Character.toString(previousChar));
                    }

                    //track and increment the number of operations so far
                    opCount++;

                // handling plus
                } else if(currentChar == '+') {
                    // there cannot be an operator behind a plus
                    Character previousChar = line.charAt(i-1);
                    if(operators.containsValue(previousChar)){
                        // invalid input
                        System.out.println("Invalid input detected, invalid operator behind operator"); 
                        System.exit(1);
                    }

                    boolean last = false;
                    // check if next char is an operator, if so skip to process operator
                    try{
                        if(operators.containsValue(line.charAt(i+1))){
                            //skip = true;
                        }
                    // must be last character if exception occured
                    } catch(Exception e){
                        // append onto latest node on block
                        epsilonNFA.addNode(epsilonNFA.adj_list.size()-2);
                        epsilonNFA.addNode(epsilonNFA.adj_list.size()-2);
                        // change for alternators later
                        int size = epsilonNFA.adj_list.size();
                        epsilonNFA.addEdge(size-4, size-3, Character.toString(previousChar));
                        epsilonNFA.addEdge(size-3, size-2, "epsilon");
                        epsilonNFA.addEdge(size-2, size-1, "epsilon");
                        epsilonNFA.addEdge(size-2, size-2, Character.toString(previousChar));
                        last = true;
                    }

                    if(!last){
                        // append onto latest node on block
                        epsilonNFA.addNode(epsilonNFA.adj_list.size()-2);
                        epsilonNFA.addNode(epsilonNFA.adj_list.size()-2);
                        // change for alternators later
                        int size = epsilonNFA.adj_list.size();
                        epsilonNFA.addEdge(size-4, size-3, Character.toString(previousChar));
                        epsilonNFA.addEdge(size-3, size-2, "epsilon");
                        epsilonNFA.addEdge(size-2, size-2, Character.toString(previousChar));
                    }
                }

            // handling non-operators
            } else if(Character.isLetter(currentChar) || Character.isDigit(currentChar) || 
                      Character.isWhitespace(currentChar)){
                        
                boolean skip = false;
                boolean last = false;
                // check if next char is an operator, if so skip to process operator
                try{
                    if(operators.containsValue(line.charAt(i+1))){
                        skip = true;
                        first = false;
                    }
                // must be last character if exception occured
                } catch(Exception e){
                    epsilonNFA.addNode(epsilonNFA.adj_list.size()-2);
                    int size = epsilonNFA.adj_list.size();

                    // check aainst single letter regex
                    if (line.length() > 1){
                        Character previousChar = line.charAt(i-1);
                        // operators fucks with add edges for some arcane reason idk
                        if(previousChar == '*' || previousChar == '+'){
                            String previousPreviousChar = Character.toString(line.charAt(i-2));
                            epsilonNFA.addEdge(size-3, size-3, previousPreviousChar);
                            epsilonNFA.deleteEdge(size-2, size-3, previousPreviousChar);
                        }
                    }
                        
                    epsilonNFA.addEdge(size-3, size-2, Character.toString(currentChar));
                    epsilonNFA.addEdge(size-2, size-1, "epsilon");
                    last = true;
                }

                if(!skip && !last){
                    // handle first character in a block
                    if (first){
                        // append onto latest node on block
                        epsilonNFA.addNode(epsilonNFA.adj_list.size()-2);
                        epsilonNFA.addNode(epsilonNFA.adj_list.size()-2);
                        // change for alternators later
                        int size = epsilonNFA.adj_list.size();
                        epsilonNFA.addEdge(size-4, size-3, "epsilon");
                        epsilonNFA.addEdge(size-3, size-2, Character.toString(currentChar));

                        first = false;
                    } else {
                        Character previousChar = line.charAt(i-1);
                        // append onto latest node on block

                        epsilonNFA.addNode(epsilonNFA.adj_list.size()-2);
                        int size = epsilonNFA.adj_list.size();

                        // operators fucks with add edges for some arcane reason idk
                        if(previousChar == '*' || previousChar == '+'){
                            String previousPreviousChar = Character.toString(line.charAt(i-2));
                            epsilonNFA.addEdge(size-3, size-3, previousPreviousChar);
                            epsilonNFA.deleteEdge(size-2, size-3, previousPreviousChar);
                        }
                        
                        epsilonNFA.addEdge(size-3, size-2, Character.toString(currentChar));
                  
                    }
                }
            } else {
                // invalid input
                System.out.println("Invalid input detected"); 
                System.exit(1);
            }
        }
        //epsilonNFA.printGraph(epsilonNFA);
        return epsilonNFA;
    }

    // evaluates given input on a line against e nfa
    static String evaluateInput(Graph epsilonNFA, String line) {
        ArrayList<Character> baseState = epsilonNFA.initialiseBaseState(epsilonNFA);
        
        //System.out.print("Base state: ");
        //System.out.println(baseState);

        String eval = new String();
        epsilonNFA.helperState(epsilonNFA, line);
        if (epsilonNFA.state.get(epsilonNFA.state.size() - 1).equals('a')){
            eval = "true";
        } else{
            eval = "false";
        }

        epsilonNFA.flushState();
        return eval;
    }

    public static void main(String[] args) {
        operators.put('(', '(');
        operators.put(')', ')');
        operators.put('|', '|');
        operators.put('+', '+');
        operators.put('*', '*');


        Scanner myObj = new Scanner(System.in);
        String regex = myObj.nextLine();
        Graph stateDiagram = parseLine(regex);
        if(args.length > 0){
            stateDiagram.printTable(stateDiagram);
        }

        System.out.println("ready");
        
        while(true){
            String input = myObj.nextLine();
            String print = evaluateInput(stateDiagram, input);
            System.out.println(print);
        }
    }
}

// class that defines transition of states
class Edge {
    int src;
    int dest;
    String transition;
    Edge(int src, int dest, String transition) {
        this.src = src;
        this.dest = dest;
        this.transition = transition;
    }
}
// Graph class
class Graph {
    // node of adjacency list 
    static class Node {
        int dest;
        String transition;
        Node(int dest, String transition)  {
            this.dest = dest;
            this.transition = transition;
        }
    };
 
    // define adjacency list
    List<List<Node>> adj_list = new ArrayList<>();

    // keeps track of state of nfa
    ArrayList<Character> state = new ArrayList<Character>();
    ArrayList<Character> bufferState = new ArrayList<Character>();
 
    // Graph Constructor
    public Graph()
    {
        // add start and end node
        adj_list.add(new ArrayList<>());
        adj_list.add(new ArrayList<>());
 
    }

    // add a transition to a source node to destination node
    public void addEdge(int src, int dest, String transition) {
        adj_list.get(src).add(new Node(dest, transition));
    }