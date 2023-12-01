package bots.platforms;

import java.util.HashMap;
import java.util.Map;

public class PlatformFSM {
    private final Map<String, Platform> platformMap;

    public PlatformFSM() {
        this.platformMap = new HashMap<>();
        platformMap.put("TELEGRAM", Platform.TELEGRAM);
        platformMap.put("DISCORD", Platform.DISCORD);
    }

    public Map<String, Platform> getPlatformMap() {
        return platformMap;
    }
}
