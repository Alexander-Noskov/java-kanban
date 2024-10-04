import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void notNull() {
        assertNotNull(Managers.getDefault());
        assertNotNull(Managers.getDefaultHistory());
    }

}