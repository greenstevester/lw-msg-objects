package net.greensill.lw.msg.objects;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractUnitTest {

    protected Path testDirectory;

    protected void setUp() {
        testDirectory = Paths.get("./src/test/resources").normalize().toAbsolutePath();
        assert Files.exists(testDirectory);
    }

}
