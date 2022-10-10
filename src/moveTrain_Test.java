package src;

import org.junit.Assert;
import org.junit.Test;

public class moveTrain_Test {
    @Test( expected = IllegalArgumentException.class)
    public void addTrainThatDoesNotExist() {
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
        Assert.assertEquals("train", interlocking.getSection(1));
    }
    
}