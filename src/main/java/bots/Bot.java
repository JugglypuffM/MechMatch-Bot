package bots;

import bots.platforms.Platform;

public interface Bot {
    boolean start();
    boolean executePhoto(String id, String message, String photo);
    boolean executeText(String id, String message);
    Platform getPlatform();
}
