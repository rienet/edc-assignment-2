package src;

import org.junit.Assert;
import org.junit.Test;

public class addTrain_Test {
    @Test
    public void evaluatesExpression() {
        InterlockingImp1 interlocking = new InterlockingImp1();
        interlocking.addTrain("train", 1, 4);
        Assert.assertEquals("train", interlocking.getSection(1));
    }
}
