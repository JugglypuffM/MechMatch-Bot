package bots;

import bots.platforms.Platform;

public interface Bot {
    boolean start();
    boolean executePhoto(String platformId, String message, String photo);
    boolean executeText(String platformId, String message);
    Platform getPlatform();
}
