package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

public class InterlockingImp1 implements Interlocking{
    static PetriNet inter1North = new PetriNet();
    static PetriNet inter1South = new PetriNet();
    static PetriNet inter2North = new PetriNet();
    static PetriNet inter2South = new PetriNet();
    
    List<Train> allTrains = new ArrayList<Train>();
    int[] section = new int[11];
    String directionLock;
    int trainsInRailway = 0;

    // defines a train and related attributes that are part of a train
    static class Train {
        String name;
        int entry;
        int dest;
        int route;
        int currentSection;
        Train(String name, int entry, int dest)  {
            this.name = name;
            this.entry = entry;
            this.dest = dest;
            this.currentSection = entry;
        }
    };

    @Override
    public void addTrain(String trainName, int entryTrackSection, int destinationTrackSection)
            throws IllegalArgumentException, IllegalStateException {
        // locks the direction of travel when a train enters the railway to prevent deadlocks
        // only unlocks when railway is empty
        if (trainsInRailway == 0){
            if(destinationTrackSection == 2 || destinationTrackSection == 3){
                directionLock = "north";
            } else {
                directionLock = "south";
            }
        }

        // ensure southbound trains are entering and exiting from correct sections
        if (entryTrackSection == 1 || entryTrackSection == 3){
            if(destinationTrackSection != 4 && destinationTrackSection != 8 &&
                destinationTrackSection != 9 && destinationTrackSection != 11){
                throw new IllegalArgumentException("not a valid destination southbound");
            } else if (directionLock != "south"){
                throw new IllegalArgumentException("cannot add southbound train when there are still northbound trains remaining");
            } else if (entryTrackSection == 1){
                if(destinationTrackSection == 8 || destinationTrackSection == 9){
                    
                } else {
                    throw new IllegalArgumentException("not a valid path southbound, interlocking dlines");
                }
            } else if (entryTrackSection == 3){
                if(destinationTrackSection == 4 || destinationTrackSection == 11){
                    
                } else {
                    throw new IllegalArgumentException("not a valid path southbound, interlocking lines");
                }
            }
        }
        // ensure northbound trains are entering and exiting from correct sections
        else if (entryTrackSection == 4 || entryTrackSection == 9 ||
            entryTrackSection == 10 || entryTrackSection == 11){
            if(destinationTrackSection != 2 && destinationTrackSection != 3){
                throw new IllegalArgumentException("not a valid destination northbound");
            } else if (directionLock != "north"){
                throw new IllegalArgumentException("cannot add northbound train when there are still southbound trains remaining");
            } else if (destinationTrackSection == 2){
                if(entryTrackSection == 9 || entryTrackSection == 10){
    
                } else {
                    throw new IllegalArgumentException("not a valid path northbound, interlocking lines");
                }
            } else if (destinationTrackSection == 3){
                if(entryTrackSection == 4 || entryTrackSection == 11){
                    
                } else {
                    throw new IllegalArgumentException("not a valid path northbound, interlocking lines");
                }
            }
        }
        // train is not entering from correct sections
        else {
            throw new IllegalArgumentException("train is not entering from correct section");
        }

        // checks if train name already in use
        for (Train temp : allTrains) {
            if(temp.name == trainName){
                throw new IllegalArgumentException("train name already in use");
            }
        }
        // checks if railway section already in use
        for (Train temp : allTrains) {
            if(temp.currentSection == entryTrackSection){
                throw new IllegalStateException("railway section already in use");
            }
        }
        
        allTrains.add(new Train(trainName, entryTrackSection, destinationTrackSection));
        section[entryTrackSection-1] = 1;
        trainsInRailway++;
    }

    @Override
    public int moveTrains(String[] trainNames) throws IllegalArgumentException {
        // check if train is still in corrridor or if it exists
        for(int i = 0; i<trainNames.length; i++){
            if(getTrain(trainNames[i]) == -1){
                throw new IllegalArgumentException("cannot move a train, is out of corridor");
            }
        }
        
        inter1North.createIntersection1North();
        inter1South.createIntersection1South();
        inter2North.createIntersection2North();
        inter2South.createIntersection2South();
        int[] finalChanges = section.clone();
        int trainIsMoved = 0;
        
        for(int i = 0; i<trainNames.length; i++){
            int[] bufferSection = section.clone();
            Train currentTrain = trainObject(trainNames[i]);

            // if train has reached end destination, remove off railway
            if(currentTrain.currentSection == currentTrain.dest){
                trainsInRailway--;
                bufferSection[currentTrain.currentSection-1] = 0;
                currentTrain.currentSection = -1;
            }

            // check if destination is empty, only move if so
            generateRoute(currentTrain);
            System.out.println("train is: " + currentTrain.name + " iter: " + i + " route: " +currentTrain.route);
            if (section[currentTrain.route - 1 ] == 0){
                // premptive set states for certain situations - see diagram
                if(currentTrain.currentSection == 3 && currentTrain.route == 7){
                    inter1South.states.replace("3 is travel to 7", 1);
                }
                if(section[5] == 0){
                    inter1North.states.replace("6 is not travelling", 1);
                }

                // move trains using states and transitions
                // southbound
                if (currentTrain.currentSection < currentTrain.route) {
                    // Intersection 1
                    if(currentTrain.currentSection == 1 || currentTrain.currentSection == 3){
                        if(currentTrain.currentSection == 1 && inter1South.states.get("empty") == 1){
                            System.out.println("Invalid input detected, operator cannot be first"); 
                            bufferSection = takeTransition(bufferSection, "t1", "inter1south");
                            bufferSection = takeTransition(bufferSection, "t4", "inter1south");
                        } else if (currentTrain.currentSection == 3) {
                            if(currentTrain.route == 7 && inter1South.states.get("3 is travel to 7") == 1){
                                bufferSection = takeTransition(bufferSection, "t5", "inter1south");
                            } else if (inter1South.states.get("empty") == 1){
                                System.out.println("detetc"); 
                                bufferSection = takeTransition(bufferSection, "t2", "inter1south");
                                bufferSection = takeTransition(bufferSection, "t3", "inter1south");
                                System.out.println(inter1South.states.get("empty")); 
                            }
                        }
                    // intersection 2
                    } else {
                        if(currentTrain.currentSection == 5 && inter2South.states.get("empty") == 1){
                            bufferSection = takeTransition(bufferSection, "t1", "inter2south");
                            if(currentTrain.route == 8) {
                                bufferSection = takeTransition(bufferSection, "t2", "inter2south");
                            } else {
                                bufferSection = takeTransition(bufferSection, "t3", "inter2south");
                            }
                        } else if (currentTrain.currentSection == 7) {
                            bufferSection = takeTransition(bufferSection, "t4", "inter2south");
                        }
                    }
                // northbound
                } else {
                    // Intersection 1
                    if(currentTrain.currentSection == 6 || currentTrain.currentSection == 4 ||
                        currentTrain.currentSection == 7){
                        if(currentTrain.currentSection == 6){
                            System.out.println("why");
                            bufferSection = takeTransition(bufferSection, "t1", "inter1north");
                        } else if (currentTrain.currentSection == 4 && inter1North.states.get("empty") == 1
                            && inter1North.states.get("6 is not travelling") == 1 ) {
                            bufferSection = takeTransition(bufferSection, "t3", "inter1north");
                            bufferSection = takeTransition(bufferSection, "t2", "inter1north");
                        } else if (currentTrain.currentSection == 7 && inter1North.states.get("empty") == 1){
                            System.out.println("huh"); 
                            bufferSection = takeTransition(bufferSection, "t4", "inter1north");
                            bufferSection = takeTransition(bufferSection, "t2", "inter1north");
                        }
                    // intersection 2
                    } else {
                        if(currentTrain.currentSection == 9 && inter2North.states.get("empty") == 1){
                            bufferSection = takeTransition(bufferSection, "t2", "inter2north");
                            bufferSection = takeTransition(bufferSection, "t1", "inter2north");
                        } else if (currentTrain.currentSection == 10 && inter2North.states.get("empty") == 1) {
                            bufferSection = takeTransition(bufferSection, "t3", "inter2north");
                            bufferSection = takeTransition(bufferSection, "t1", "inter2north");
                        } else if (currentTrain.currentSection == 11){
                            bufferSection = takeTransition(bufferSection, "t4", "inter2north");
                        }
                    }
                }
            }
            // track changes from original section
            int[] tracker= new int[11];
            System.out.println("iter" + i);
            for(int j = 0; j<finalChanges.length; j++){
                System.out.print(bufferSection[j]); 
                if(bufferSection[j] != section[j]){
                    tracker[j] = 1;

                    // update currenttrain location
                    if(bufferSection[j] == 1){
                        currentTrain.currentSection = j+1;
                        trainIsMoved += 1;
                    }
                }
            }

            System.out.println(); 

            // add change to final change
            for(int j = 0; j<finalChanges.length; j++){
                if(tracker[j] == 1){
                    finalChanges[j] = bufferSection[j];
                }
            }
        }
        section = finalChanges;

        return trainIsMoved;
    }

    @Override
    public String getSection(int trackSection) throws IllegalArgumentException {
        // checks for section that does not exist
        if(trackSection < 1 || trackSection > 11){
            throw new IllegalArgumentException("section does not exist");
        }

        String trainName;
        for (Train temp : allTrains) {
            if(temp.currentSection == trackSection){
                trainName = temp.name;
                return trainName;
            }
        }
        return null;
    }

    @Override
    public int getTrain(String trainName) throws IllegalArgumentException {
        for (Train temp : allTrains) {
            if(temp.name == trainName){
                return temp.currentSection;
            }
        }

        // cannot find name in list of all train names
        throw new IllegalArgumentException("train does not exist");
    }

    public Train trainObject(String trainName) {
        for (Train temp : allTrains) {
            if(temp.name == trainName){
                return temp;
            }
        }
        return null;
    }

    public void generateRoute(Train currentTrain) {
        System.out.println("hi "+ currentTrain.name + currentTrain.dest + currentTrain.entry);
        // southbound
        if (currentTrain.entry < currentTrain.dest){
            // start of transit - intersection 1
            if(currentTrain.entry == currentTrain.currentSection){
                if(currentTrain.currentSection == 1){
                    currentTrain.route = 5;
                } else if(currentTrain.currentSection == 3){
                    if(currentTrain.dest == 4){
                        currentTrain.route = 4;
                    } else{
                        currentTrain.route = 7;
                    }
                }
            // middle of transit
            } else {
                if(currentTrain.currentSection == 5){
                    if(currentTrain.dest == 8){
                        currentTrain.route = 8;
                    } else{
                        currentTrain.route = 9;
                    }
                } else if(currentTrain.currentSection == 7){
                    currentTrain.route = 11;
                }
            }
        // northbound
        } else {
             // start of transit - intersection 2
             if(currentTrain.entry == currentTrain.currentSection){
                if(currentTrain.currentSection == 9 || currentTrain.currentSection == 10){
                    currentTrain.route = 6;
                } else if(currentTrain.currentSection == 11){
                    currentTrain.route = 7;
                } else if(currentTrain.currentSection == 4){
                    currentTrain.route = 3;
                }
            // middle of transit
            } else {
                if(currentTrain.currentSection == 4 || currentTrain.currentSection == 7){
                    currentTrain.route = 3;
                } else if(currentTrain.currentSection == 6){
                    currentTrain.route = 2;
                }
            }
        }
    }

    public int[] takeTransition(int[] bufferSection, String gate, String petriNet){
        if(petriNet == "inter1south"){
            for(int x = 0; x<11; x++){
                bufferSection[x] -= inter1South.transitionList.get(gate).in.get(x);
                bufferSection[x] += inter1South.transitionList.get(gate).out.get(x);
            }
            inter1South.transitionList.get(gate).statesChange.forEach((k,v) -> inter1South.states.replace(k,v));    
        } else if(petriNet == "inter2south"){
            for(int x = 0; x<11; x++){
                bufferSection[x] -= inter2South.transitionList.get(gate).in.get(x);
                bufferSection[x] += inter2South.transitionList.get(gate).out.get(x);
            }
            inter2South.transitionList.get(gate).statesChange.forEach((k,v) -> inter2South.states.replace(k,v));    
        } else if(petriNet == "inter1north"){
            for(int x = 0; x<11; x++){
                bufferSection[x] -= inter1North.transitionList.get(gate).in.get(x);
                bufferSection[x] += inter1North.transitionList.get(gate).out.get(x);
            }
            inter1North.transitionList.get(gate).statesChange.forEach((k,v) -> inter1North.states.replace(k,v));    
        } else if(petriNet == "inter2north"){
            for(int x = 0; x<11; x++){
                bufferSection[x] -= inter2North.transitionList.get(gate).in.get(x);
                bufferSection[x] += inter2North.transitionList.get(gate).out.get(x);
            }
            inter2North.transitionList.get(gate).statesChange.forEach((k,v) -> inter2North.states.replace(k,v));    
        }

        return bufferSection;
    }

    public static void main(String[] args) {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 4, 3);
        interlocking.addTrain("train2", 10, 2);
        String[] stringArray = {"train2"};
        String[] stringArray2 = {"train", "train2"};
        interlocking.moveTrains(stringArray);
        interlocking.moveTrains(stringArray2);
    }
}

// class representing a Petri net
class PetriNet {
    // represents a transition in a Petri net
    class Transition {
        // in/out denotes a section of the railway
        // (see create intersection methods for examples)
        List<Integer> in = new ArrayList<>();
        List<Integer> out = new ArrayList<>();
        // represents additional states THAT CHANGES in a TRANSITION as shown in diagrams
        HashMap<String, Integer> statesChange = new HashMap<String, Integer>();

        Transition(List<Integer> in, List<Integer> out, 
                   HashMap<String, Integer> statesChange) {
            this.in = in;
            this.out = out;
            this.statesChange = statesChange;
        }
    };

    // represents additional states in a Petri net as shown in diagrams
    public HashMap<String, Integer> states = new HashMap<String, Integer>();
    
    // a Petri Net is also made up of transitions
    public HashMap<String, Transition> transitionList = new HashMap<String, Transition>();

    // add a state to the petri net
    public void addState(String newName, Integer newState) {
        states.put(newName, newState);    
    }

    // add a transition to the petri net
    public void addTransition(String newName, Transition newTransition) {
        transitionList.put(newName, newTransition);    
    }

    // populate a petri net for intersection 1 for trains travellign north
    public void createIntersection1North(){
        // initialise extra states
        states.put("empty", 1);
        states.put("full", 0);
        states.put("6 is not travelling", 0);

        // initialise transition
        ArrayList<Integer> IN_T1 = new ArrayList<>(Arrays.asList(0,0,0,0,0,1,0,0,0,0,0));
        ArrayList<Integer> OUT_T1 = new ArrayList<>(Arrays.asList(0,1,0,0,0,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T1 = new HashMap<>();
        addTransition("t1", new Transition(IN_T1, OUT_T1, STATES_CHANGE_T1));

        ArrayList<Integer> IN_T2 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T2 = new ArrayList<>(Arrays.asList(0,0,1,0,0,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T2 = new HashMap<>();
        STATES_CHANGE_T2.put("full", 0);
        //STATES_CHANGE_T2.put("empty", 1);
        addTransition("t2", new Transition(IN_T2, OUT_T2, STATES_CHANGE_T2));

        ArrayList<Integer> IN_T3 = new ArrayList<>(Arrays.asList(0,0,0,1,0,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T3 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T3 = new HashMap<>();
        STATES_CHANGE_T3.put("6 is not travelling", 0);
        STATES_CHANGE_T3.put("full", 1);
        STATES_CHANGE_T3.put("empty", 0);
        addTransition("t3", new Transition(IN_T3, OUT_T3, STATES_CHANGE_T3));

        ArrayList<Integer> IN_T4 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,1,0,0,0,0));
        ArrayList<Integer> OUT_T4 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T4 = new HashMap<>();
        STATES_CHANGE_T4.put("full", 1);
        STATES_CHANGE_T4.put("empty", 0);
        addTransition("t4", new Transition(IN_T4, OUT_T4, STATES_CHANGE_T4));
    }

    // populate a petri net for intersection 1 for trains travellign south
    public void createIntersection1South(){
        // initialise extra states
        states.put("empty", 1);
        states.put("full", 0);
        states.put("3 is travel to 7", 0);

        // initialise transition
        ArrayList<Integer> IN_T1 = new ArrayList<>(Arrays.asList(1,0,0,0,0,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T1 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T1 = new HashMap<>();
        STATES_CHANGE_T1.put("full", 1);
        STATES_CHANGE_T1.put("empty", 0);
        addTransition("t1", new Transition(IN_T1, OUT_T1, STATES_CHANGE_T1));

        ArrayList<Integer> IN_T2 = new ArrayList<>(Arrays.asList(0,0,1,0,0,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T2 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T2 = new HashMap<>();
        STATES_CHANGE_T2.put("full", 1);
        STATES_CHANGE_T2.put("empty", 0);
        addTransition("t2", new Transition(IN_T2, OUT_T2, STATES_CHANGE_T2));

        ArrayList<Integer> IN_T3 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T3 = new ArrayList<>(Arrays.asList(0,0,0,1,0,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T3 = new HashMap<>();
        STATES_CHANGE_T3.put("full", 0);
        //STATES_CHANGE_T3.put("empty", 1);
        addTransition("t3", new Transition(IN_T3, OUT_T3, STATES_CHANGE_T3));

        ArrayList<Integer> IN_T4 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T4 = new ArrayList<>(Arrays.asList(0,0,0,0,1,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T4 = new HashMap<>();
        STATES_CHANGE_T4.put("full", 0);
        //STATES_CHANGE_T4.put("empty", 1);
        addTransition("t4", new Transition(IN_T4, OUT_T4, STATES_CHANGE_T4));

        ArrayList<Integer> IN_T5 = new ArrayList<>(Arrays.asList(0,0,1,0,0,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T5 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,1,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T5 = new HashMap<>();
        STATES_CHANGE_T5.put("3 is travel to 7", 0);
        addTransition("t5", new Transition(IN_T5, OUT_T5, STATES_CHANGE_T5));
    }

    // populate a petri net for intersection 2 for trains travellign north
    public void createIntersection2North(){
        // initialise extra states
        states.put("empty", 1);
        states.put("full", 0);

        // initialise transition
        ArrayList<Integer> IN_T1 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T1 = new ArrayList<>(Arrays.asList(0,0,0,0,0,1,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T1 = new HashMap<>();
        STATES_CHANGE_T1.put("full", 0);
        STATES_CHANGE_T1.put("empty", 1);
        addTransition("t1", new Transition(IN_T1, OUT_T1, STATES_CHANGE_T1));

        ArrayList<Integer> IN_T2 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,1,0,0));
        ArrayList<Integer> OUT_T2 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T2 = new HashMap<>();
        STATES_CHANGE_T2.put("full", 1);
        STATES_CHANGE_T2.put("empty", 0);
        addTransition("t2", new Transition(IN_T2, OUT_T2, STATES_CHANGE_T2));

        ArrayList<Integer> IN_T3 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,1,0));
        ArrayList<Integer> OUT_T3 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T3 = new HashMap<>();
        STATES_CHANGE_T3.put("full", 1);
        STATES_CHANGE_T3.put("empty", 0);
        addTransition("t3", new Transition(IN_T3, OUT_T3, STATES_CHANGE_T3));

        ArrayList<Integer> IN_T4 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,1));
        ArrayList<Integer> OUT_T4 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,1,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T4 = new HashMap<>();
        addTransition("t4", new Transition(IN_T4, OUT_T4, STATES_CHANGE_T4));
    }

    // populate a petri net for intersection 2 for trains travellign south
    public void createIntersection2South(){
        // initialise extra states
        states.put("empty", 1);
        states.put("full", 0);

        // initialise transition
        ArrayList<Integer> IN_T1 = new ArrayList<>(Arrays.asList(0,0,0,0,1,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T1 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T1 = new HashMap<>();
        STATES_CHANGE_T1.put("full", 1);
        STATES_CHANGE_T1.put("empty", 0);
        addTransition("t1", new Transition(IN_T1, OUT_T1, STATES_CHANGE_T1));

        ArrayList<Integer> IN_T2 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T2 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,1,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T2 = new HashMap<>();
        STATES_CHANGE_T2.put("full", 0);
        STATES_CHANGE_T2.put("empty", 1);
        addTransition("t2", new Transition(IN_T2, OUT_T2, STATES_CHANGE_T2));

        ArrayList<Integer> IN_T3 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T3 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,1,0,0));
        HashMap<String, Integer> STATES_CHANGE_T3 = new HashMap<>();
        STATES_CHANGE_T3.put("full", 0);
        STATES_CHANGE_T3.put("empty", 1);
        addTransition("t3", new Transition(IN_T3, OUT_T3, STATES_CHANGE_T3));

        ArrayList<Integer> IN_T4 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,1,0,0,0,0));
        ArrayList<Integer> OUT_T4 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,1));
        HashMap<String, Integer> STATES_CHANGE_T4 = new HashMap<>();
        addTransition("t4", new Transition(IN_T4, OUT_T4, STATES_CHANGE_T4));
    }
}
