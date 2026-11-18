package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.IntType;
import interpreter.model.type.StringType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStmtTest {
    @TempDir
    Path dir;

    private PrgState state;
    private ValueExp file;

    @BeforeEach
    void setup() throws Exception {
        Path f = dir.resolve("numbers.in");
        Files.writeString(f, "7\n");
        file = new ValueExp(new StringValue(f.toString()));
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new IntType()).execute(state);
    }

    @AfterEach
    void tearDown() throws Exception {
        for (var reader : state.getFileTable().toMap().values()) {
            reader.close();
        }
    }

    @Test
    void openRFileAddsFileAndRejectsErrors() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertTrue(state.getFileTable().isDefined((StringValue) file.value()));

        // Opening twice throws
        assertThrows(StatementException.class, () -> new OpenRFileStmt(file).execute(state));

        // Non-string expression throws
        assertThrows(StatementException.class, () ->
                new OpenRFileStmt(new ValueExp(new IntValue(5))).execute(state));

        // Missing file throws
        ValueExp missing = new ValueExp(new StringValue(dir.resolve("does_not_exist.in").toString()));
        assertThrows(StatementException.class, () -> new OpenRFileStmt(missing).execute(state));
    }

    @Test
    void readGivesTheNextNumberThenZeroAtTheEnd() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(7), state.getSymTable().lookup("x"));
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("x"));
    }

    @Test
    void readFileErrorsThrowStatementException() throws Exception {
        // File not open
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "x").execute(state));

        new OpenRFileStmt(file).execute(state);

        // Undeclared variable
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "undeclared").execute(state));

        // Wrong variable type (string instead of int)
        new VarDeclStmt("strVar", new StringType()).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "strVar").execute(state));

        // Non-integer line
        Path badFile = dir.resolve("bad.in");
        Files.writeString(badFile, "not_a_number\n");
        ValueExp badExp = new ValueExp(new StringValue(badFile.toString()));
        new OpenRFileStmt(badExp).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(badExp, "x").execute(state));
    }

    @Test
    void closeRFileRemovesFileAndClosingUnopenedThrows() throws MyException {
        assertThrows(StatementException.class, () -> new CloseRFileStmt(file).execute(state));

        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        assertTrue(state.getFileTable().toMap().isEmpty());
    }
}