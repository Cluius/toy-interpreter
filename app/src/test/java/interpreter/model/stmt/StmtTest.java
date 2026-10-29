package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), null);
    }

    @Test
    void compStmtPushesStatementsInRightOrder() throws MyException {
        IStmt s1 = new NopStmt();
        IStmt s2 = new PrintStmt(new ValueExp(new IntValue(1)));
        new CompStmt(s1, s2).execute(state);
        assertEquals(s1, state.getExeStack().pop());
        assertEquals(s2, state.getExeStack().pop());
    }

    @Test
    void varDeclAddsDefaultValueAndRejectsDuplicates() throws MyException {
        IStmt decl = new VarDeclStmt("x", new IntType());
        decl.execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("x"));
        assertThrows(StatementException.class, () -> decl.execute(state));
    }

    @Test
    void assignStmtUpdatesVariableAndChecksTypes() throws MyException {
        new VarDeclStmt("x", new IntType()).execute(state);
        new AssignStmt("x", new ValueExp(new IntValue(42))).execute(state);
        assertEquals(new IntValue(42), state.getSymTable().lookup("x"));

        // Type mismatch
        assertThrows(StatementException.class, () ->
                new AssignStmt("x", new ValueExp(new BoolValue(true))).execute(state));

        // Undeclared variable
        assertThrows(StatementException.class, () ->
                new AssignStmt("y", new ValueExp(new IntValue(1))).execute(state));
    }

    @Test
    void printStmtAppendsToOut() throws MyException {
        new PrintStmt(new ValueExp(new IntValue(99))).execute(state);
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(99), state.getOut().get(0));
    }

    @Test
    void ifStmtPushesCorrectBranchAndChecksConditionType() throws MyException {
        IStmt thenBranch = new NopStmt();
        IStmt elseBranch = new PrintStmt(new ValueExp(new IntValue(1)));
        new IfStmt(new ValueExp(new BoolValue(true)), thenBranch, elseBranch).execute(state);
        assertEquals(thenBranch, state.getExeStack().pop());

        assertThrows(StatementException.class, () ->
                new IfStmt(new ValueExp(new IntValue(1)), thenBranch, elseBranch).execute(state));
    }
}