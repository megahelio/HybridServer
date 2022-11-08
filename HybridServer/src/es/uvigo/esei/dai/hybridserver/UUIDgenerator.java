package es.uvigo.esei.dai.hybridserver;

import java.util.UUID;

public class UUIDgenerator {

    public static String generate() {
        UUID randonUuid = UUID.randomUUID();
        String uuid = randonUuid.toString();
        return uuid;
    }

}