package assertions;

import assertions.conditions.MessageConditions;
import assertions.conditions.StatusCodeCondition;

public class Conditions {
    public static MessageConditions hasMessage(String expectedMessage){
        return new MessageConditions(expectedMessage);
    }

    public static StatusCodeCondition hasStatusCode(Integer expectedStatus){
        return new StatusCodeCondition(expectedStatus);
    }
}
