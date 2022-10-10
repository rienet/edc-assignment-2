package src;

import org.junit.Assert;
import org.junit.Test;

public class addTrain_Test {
    @Test
    public void addSouthTrain() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 1, 8);
        Assert.assertEquals("train", interlocking.getSection(1));
    }

    @Test
    public void addNorthTrain() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 9, 2);
        Assert.assertEquals("train", interlocking.getSection(9));
    }

    @Test
    public void addMultipleTrain() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 4, 3);
        Assert.assertEquals("train", interlocking.getSection(4));
        interlocking.addTrain("train1", 9, 2);
        Assert.assertEquals("train1", interlocking.getSection(9));
        interlocking.addTrain("train2", 10, 2);
        Assert.assertEquals("train2", interlocking.getSection(10));
    }

    @Test( expected = IllegalArgumentException.class)
    public void preventDeadLock() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 4, 3);
        Assert.assertEquals("train", interlocking.getSection(4));
        interlocking.addTrain("train1", 9, 2);
        Assert.assertEquals("train1", interlocking.getSection(9));
        interlocking.addTrain("train2", 10, 2);
        Assert.assertEquals("train2", interlocking.getSection(10));
        interlocking.addTrain("train3", 1, 4);
    }

    @Test( expected = IllegalArgumentException.class)
    public void TrainNotEnteringFromValidSection() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 20, 3);
    }

    @Test( expected = IllegalArgumentException.class)
    public void TrainNotExitingFromValidSection() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 3, 40);
    }

    @Test( expected = IllegalArgumentException.class)
    public void TrainPathingFromFreightToPassenger() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 3, 9);
    }

    @Test( expected = IllegalArgumentException.class)
    public void TrainPathingFromPassengerToFreight() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 8, 3);
    }

    @Test( expected = IllegalArgumentException.class)
    public void TrainPathingFromEntryToEntry() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 1, 2);
    }

    @Test( expected = IllegalArgumentException.class)
    public void TrainAttemptingToEnterInMiddleOfRailway() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 5, 2);
    }

    @Test( expected = IllegalArgumentException.class)
    public void TrainNameAlreadyInUse() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 1, 8);
        interlocking.addTrain("train", 3, 7);
    }

    @Test( expected = IllegalStateException.class)
    public void TrainSectionAlreadyOccupied() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 1, 8);
        interlocking.addTrain("train2", 1, 9);
    }
}
