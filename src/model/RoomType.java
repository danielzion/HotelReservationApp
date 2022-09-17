package model;


import java.util.HashMap;
import java.util.Map;

public enum RoomType {
    SINGLE (1),
    DOUBLE (2);

    private final int numberOfBeds;

    private static final Map<Integer, RoomType> NUMBER_OF_BEDS = new HashMap<Integer, RoomType>();

    static {
        for (RoomType roomType : values()) {
            NUMBER_OF_BEDS.put(roomType.numberOfBeds, roomType);
        }
    }

    RoomType(int numberOfBeds) {
        this.numberOfBeds = numberOfBeds;
    }

    public static RoomType valueOfNumberOfBeds(int numberOfBeds) {
        return NUMBER_OF_BEDS.get(numberOfBeds);
    }
}
