package es.uvigo.esei.dai.hybridserver.dao;

import java.util.UUID;
import java.util.regex.Pattern;

public class UUIDgenerator {

    public static String generate() {
        UUID randonUuid = UUID.randomUUID();
        String uuid = randonUuid.toString();
        return uuid;
    }

    public static Boolean validate(String uuidCandidate) {
        return Pattern.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                uuidCandidate);
    }

}