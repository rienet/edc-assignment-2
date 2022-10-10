package src;

import java.util.Scanner;
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
                throw new IllegalArgumentException("not a valid path southbound");
            } else if (directionLock != "south"){
                throw new IllegalArgumentException("cannot add southbound train when there are still northbound trains remaining");
            }
        }
        // ensure northbound trains are entering and exiting from correct sections
        else if (entryTrackSection == 4 || entryTrackSection == 9 ||
            entryTrackSection == 10 || entryTrackSection == 11){
            if(destinationTrackSection != 2 && destinationTrackSection != 3){
                throw new IllegalArgumentException("not a valid path northbound");
            } else if (directionLock != "north"){
                throw new IllegalArgumentException("cannot add northbound train when there are still southbound trains remaining");
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
        trainsInRailway++;
    }

    @Override
    public int moveTrains(String[] trainNames) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return 0;
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

    public static void main(String[] args) {
        inter1North.createIntersection1North();
        inter1South.createIntersection1South();
        inter2North.createIntersection2North();
        inter2South.createIntersection2South();
    }
}

// class representing a Petri net
class PetriNet {
    // represents a transition in a Petri net
    static class Transition {
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
        STATES_CHANGE_T2.put("empty", 1);
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
        STATES_CHANGE_T3.put("empty", 1);
        addTransition("t3", new Transition(IN_T3, OUT_T3, STATES_CHANGE_T3));

        ArrayList<Integer> IN_T4 = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0));
        ArrayList<Integer> OUT_T4 = new ArrayList<>(Arrays.asList(0,0,0,0,1,0,0,0,0,0,0));
        HashMap<String, Integer> STATES_CHANGE_T4 = new HashMap<>();
        STATES_CHANGE_T4.put("full", 0);
        STATES_CHANGE_T4.put("empty", 1);
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
