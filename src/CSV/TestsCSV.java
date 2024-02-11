package CSV;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
public class TestsCSV {
    @BeforeEach
    public void beforeEach() throws IOException {
        String testPath = "files\\test.csv";
        UserCSV.setFilePath(testPath);
        Files.delete(Path.of(testPath));
        UserCSV.createCSV();
    }
    @Test
    void testCreateUser() {
        assertTrue(UserCSV.createUser("Test1", "123"));
        assertTrue(UserCSV.createUser("Test2", "Abc"));
    }

    @Test
    void testUserExists() {
        UserCSV.createUser("Test1" , "123");
        assertTrue(UserCSV.userExists("Test1"));
        assertTrue(UserCSV.userExists("TEST1"));
        assertFalse(UserCSV.userExists("Test3"));
    }

    @Test
    void testAuthenticate() {
        UserCSV.createUser("Test1" , "123");
        UserCSV.createUser("Test2" , "Abc");

        assertTrue(UserCSV.authenticate("Test1", "123"));
        assertTrue(UserCSV.authenticate("TEST2", "Abc"));
        assertFalse(UserCSV.authenticate("TeSt2", "abc"));
    }

    @Test
    void testChangePwd() {
        UserCSV.createUser("Test1" , "123");
        assertTrue(UserCSV.changePwd("Test1", "123", "321"));
        assertFalse(UserCSV.changePwd("Test1", "123", "321"));
        assertFalse(UserCSV.changePwd("test", "12", "321"));
    }
}