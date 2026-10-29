package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ADTTest {
    private IStack<Integer> stack;
    private IDictionary<String, Integer> dict;
    private IList<String> list;

    @BeforeEach
    void setUp() {
        stack = new MyStack<>();
        dict = new MyDictionary<>();
        list = new MyList<>();
    }

    @Test
    void stackFollowsLifoOrder() throws ADTException {
        stack.push(10);
        stack.push(20);
        assertFalse(stack.isEmpty());
        assertEquals(20, stack.pop());
        assertEquals(10, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void popOnEmptyStackThrows() {
        assertThrows(ADTException.class, () -> stack.pop());
    }

    @Test
    void dictionaryPutLookupAndUpdate() throws ADTException {
        dict.put("a", 1);
        assertTrue(dict.isDefined("a"));
        assertEquals(1, dict.lookup("a"));
        dict.update("a", 5);
        assertEquals(5, dict.lookup("a"));
    }

    @Test
    void lookupMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.lookup("missing"));
    }

    @Test
    void listAddGetAndSize() throws ADTException {
        list.add("first");
        list.add("second");
        assertEquals(2, list.size());
        assertEquals("first", list.get(0));
        assertEquals("second", list.get(1));
        assertThrows(ADTException.class, () -> list.get(5));
    }
}