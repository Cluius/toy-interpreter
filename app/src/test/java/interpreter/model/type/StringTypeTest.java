package interpreter.model.type;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringTypeTest {
    @Test
    void stringVariableGetsEmptyStringDefaultValue() throws MyException {
        PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), null);
        new VarDeclStmt("s", new StringType()).execute(state);
        assertEquals(new StringValue(""), state.getSymTable().lookup("s"));
    }

    @Test
    void equalStringValuesHaveSameEqualsAndHashCode() {
        StringValue s1 = new StringValue("test.in");
        StringValue s2 = new StringValue("test.in");
        StringValue s3 = new StringValue("other.in");

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertNotEquals(s1, s3);
    }
}