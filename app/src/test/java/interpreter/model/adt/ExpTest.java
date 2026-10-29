package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpTest {
    private IDictionary<String, IValue> symTable;

    private static IExp num(int n) {
        return new ValueExp(new IntValue(n));
    }

    private static IExp bool(boolean b) {
        return new ValueExp(new BoolValue(b));
    }

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    @Test
    void arithmeticOperationsEvaluateCorrectly() throws MyException {
        assertEquals(new IntValue(5), new ArithExp(ArithOp.ADD, num(2), num(3)).eval(symTable));
        assertEquals(new IntValue(4), new ArithExp(ArithOp.SUB, num(7), num(3)).eval(symTable));
        assertEquals(new IntValue(15), new ArithExp(ArithOp.MUL, num(3), num(5)).eval(symTable));
        assertEquals(new IntValue(2), new ArithExp(ArithOp.DIV, num(6), num(3)).eval(symTable));
    }

    @Test
    void respectsOperatorPrecedenceEncodedInTheTree() throws MyException {
        IExp e = new ArithExp(ArithOp.ADD, num(2), new ArithExp(ArithOp.MUL, num(3), num(5)));
        assertEquals(new IntValue(17), e.eval(symTable));
    }

    @Test
    void divisionByZeroThrows() {
        IExp e = new ArithExp(ArithOp.DIV, num(1), num(0));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("division by zero", ex.getMessage());
    }

    @Test
    void nonIntegerOperandInArithExpThrows() {
        IExp e1 = new ArithExp(ArithOp.ADD, bool(true), num(1));
        assertThrows(ExpressionException.class, () -> e1.eval(symTable));

        IExp e2 = new ArithExp(ArithOp.ADD, num(1), bool(false));
        assertThrows(ExpressionException.class, () -> e2.eval(symTable));
    }

    @Test
    void logicOperationsEvaluateCorrectly() throws MyException {
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.AND, bool(true), bool(false)).eval(symTable));
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.OR, bool(false), bool(true)).eval(symTable));
    }

    @Test
    void nonBooleanOperandInLogicExpThrows() {
        IExp e = new LogicExp(LogicOp.AND, num(1), bool(true));
        assertThrows(ExpressionException.class, () -> e.eval(symTable));
    }

    @Test
    void undefinedVariableLookupThrows() {
        IExp e = new VarExp("x");
        assertThrows(ExpressionException.class, () -> e.eval(symTable));
    }
}