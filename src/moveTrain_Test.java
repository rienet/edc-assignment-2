package src;

import org.junit.Assert;
import org.junit.Test;

public class moveTrain_Test {
    @Test( expected = IllegalArgumentException.class)
    public void moveTrainThatDoesNotExist() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 1, 8);
        String[] stringArray = {"train", "does not exist"};
        interlocking.moveTrains(stringArray);
    }

    @Test
    public void MoveOneTrain() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 1, 8);
        String[] stringArray = {"train"};
        interlocking.moveTrains(stringArray);
        Assert.assertEquals("train", interlocking.getSection(5));
    }

    @Test
    public void MoveTrainToEndAndConfirmExit() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 1, 8);
        String[] stringArray = {"train"};
        interlocking.moveTrains(stringArray);
        interlocking.moveTrains(stringArray);
        Assert.assertEquals("train", interlocking.getSection(8));
        interlocking.moveTrains(stringArray);
        Assert.assertEquals(-1, interlocking.getTrain("train"));
    }
    
    @Test( expected = IllegalArgumentException.class)
    public void MoveTrainNotInCorridor() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 1, 8);
        String[] stringArray = {"train"};
        interlocking.moveTrains(stringArray);
        interlocking.moveTrains(stringArray);
        Assert.assertEquals("train", interlocking.getSection(8));
        interlocking.moveTrains(stringArray);
        Assert.assertEquals(-1, interlocking.getTrain("train"));
        interlocking.moveTrains(stringArray);
    }

    @Test
    public void ConfirmTrainOnlyMovesIfNextSectionIsEmpty() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 1, 8);
        String[] stringArray = {"train"};
        interlocking.moveTrains(stringArray);
        interlocking.addTrain("train2", 1, 8);
        String[] stringArray2 = {"train", "train2"};
        interlocking.moveTrains(stringArray2);
        Assert.assertEquals("train", interlocking.getSection(8));
        Assert.assertEquals("train2", interlocking.getSection(1));
    }
    
    @Test
    public void LeftIntersectionSouthBoundNoClash() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 1, 8);
        interlocking.addTrain("train2", 3, 11);
        String[] stringArray2 = {"train", "train2"};
        interlocking.moveTrains(stringArray2);
        Assert.assertEquals("train", interlocking.getSection(5));
        Assert.assertEquals("train2", interlocking.getSection(7));
    }

    @Test
    public void LeftIntersectionSouthBoundWithClash() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 3, 4);
        interlocking.addTrain("train2", 1, 9);
        String[] stringArray2 = {"train", "train2"};
        interlocking.moveTrains(stringArray2);
        Assert.assertEquals("train", interlocking.getSection(4));
        Assert.assertEquals("train2", interlocking.getSection(1));
    }

    @Test
    public void LeftIntersectionNorthBoundNoClash() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 11, 3);
        interlocking.addTrain("train2", 10, 2);
        String[] stringArray2 = {"train", "train2"};
        interlocking.moveTrains(stringArray2);
        interlocking.moveTrains(stringArray2);
        Assert.assertEquals("train", interlocking.getSection(3));
        Assert.assertEquals("train2", interlocking.getSection(2));
    }

    @Test
    public void LeftIntersectionNorthBoundWithClash() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 4, 3);
        interlocking.addTrain("train2", 10, 2);
        String[] stringArray = {"train2"};
        String[] stringArray2 = {"train", "train2"};
        interlocking.moveTrains(stringArray);
        interlocking.moveTrains(stringArray2);
        Assert.assertEquals("train", interlocking.getSection(4));
        Assert.assertEquals("train2", interlocking.getSection(2));
    }
}