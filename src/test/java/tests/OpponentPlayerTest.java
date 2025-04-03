package tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class OpponentPlayerTest {


    @ParameterizedTest
    @CsvSource({"smolov,forward,RU", "ngamale,forward,CAM", "shunin,goalkeeper,RU"})
    public void checkingForRussianPlayers(String name, String role, String country) {
        Assertions.assertTrue(country.contains("RU"));
    }


    @Test
    public void sum1() {
        int a = 3;
        int b = 10;
        Assertions.assertTrue(a<b);
    }

    @Test
    public void sum2() {
        int a = 13;
        int b = 1110;
        Assertions.assertTrue(a>b);
    }
}
