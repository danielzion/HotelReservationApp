package model;

public class FreeRoom extends Room{
    public FreeRoom(String RoomNumber, Double Price, RoomType roomType){
        super(RoomNumber, Price, roomType );
        this.Price = 0.0;
    }

    @Override
    public Double getRoomPrice() {
        return super.getRoomPrice();
    }
}
