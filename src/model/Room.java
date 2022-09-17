package model;

import java.util.Objects;

public class Room implements IRoom {
    private final String RoomNumber;
    public Double Price;

    private final RoomType enumeration;

    public Room(String RoomNumber, Double Price, RoomType enumeration){
        this.RoomNumber = RoomNumber;
        this.Price = Price;
        this.enumeration = enumeration;
    }

    @Override
    public String getRoomNumber() {
        return this.RoomNumber;
    }

    @Override
    public Double getRoomPrice() {
        return this.Price;
    }

    @Override
    public RoomType getRoomType() {
        return this.enumeration;
    }

    @Override
    public boolean isFree() {
        return this.Price == (double) 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room room)) return false;
        return RoomNumber.equals(room.RoomNumber) && Price.equals(room.Price) && enumeration == room.enumeration;
        }

    @Override
    public int hashCode() {
        return Objects.hash(RoomNumber, Price, enumeration);
    }
}
