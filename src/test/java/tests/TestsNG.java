package tests;


import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestsNG {
/*    @Test
    public void sumPlayers(){
        int a = 5;
        int b = 10;
        int z = 15;
        Assert.assertTrue(false);
    }*/


    @DataProvider(name = "TestFotball")
    public Object[] dateWithFotball() {
        People stas = new People("Stas", 18, "goalkeeper");
        People smolov = new People("smolov", 22, "forward");
        People stas1 = new People("Stas1", 18, "goalkeeper");
        People pogrebniak = new People("pogrebniak", 22, "forward");
        return new Object[]{stas, smolov, stas1, pogrebniak};
    }

    @Test(dataProvider = "TestFotball")
    public void testPlayersWithRole(People people) {
        System.out.println(people.getName());
        Assert.assertTrue(people.getAge() > 16);
        Assert.assertTrue(people.getName().contains("s"));
    }


}
