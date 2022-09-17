package service;

import model.Customer;
import model.IRoom;
import model.Reservation;

import java.util.*;

public class ReservationService {

    private static final Map<String, IRoom> roomMap = new HashMap<String, IRoom>();
    private static final Map<String, Collection<Reservation>> reservationMap = new HashMap<String, Collection<Reservation>>();

    public static void addRoom(IRoom room){
        roomMap.put(room.getRoomNumber(), room);
    }

    public static Collection<IRoom> getAllRooms() {
        return roomMap.values();
    }


    public static Reservation reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate){
        // validate room is not already reserved
        if (isRoomReserved(room, checkInDate, checkOutDate)) {
            return null;
        }
        Reservation reservation = new Reservation(customer, room, checkInDate, checkOutDate);
        Collection<Reservation> customerReservations = getCustomersReservation(customer);
        if (customerReservations == null) {
            customerReservations = new LinkedList<>();
        }
        customerReservations.add(reservation);
        reservationMap.put(customer.getEmail(), customerReservations);
        return reservation;
    }

    public static Collection<IRoom> findRooms(Date checkInDate, Date checkOutDate){
        // get all rooms reserved within the check-in and check-out dates
        Collection<IRoom> reservedRooms = getAllReservedRooms(checkInDate, checkOutDate);
        // get all available rooms (all rooms that are not reserved)
        Collection<IRoom> availableRooms = new LinkedList<>();
        for (IRoom room : getAllRooms()) {
            if (!reservedRooms.contains(room)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }


    public static Collection<Reservation> getCustomersReservation(Customer customer){
        return reservationMap.get(customer.getEmail());
    }

    public static Collection<Reservation> getAllReservations() {
        Collection<Reservation> allReservations = new LinkedList<>();
        for (Collection<Reservation> customerReservations : reservationMap.values()) {
            allReservations.addAll(customerReservations);
        }
        return allReservations;
    }

    private static Collection<IRoom> getAllReservedRooms(Date checkInDate, Date checkOutDate) {
        Collection<IRoom> reservedRooms = new LinkedList<>();
        for (Reservation reservation : getAllReservations()) {
            if (reservation.isRoomReserved(checkInDate, checkOutDate)) {
                reservedRooms.add(reservation.getARoom());
            }
        }
        return reservedRooms;
    }

    private static boolean isRoomReserved(IRoom room, Date checkInDate, Date checkOutDate) {
        // get all rooms reserved within the check-in and check-out dates
        Collection<IRoom> reservedRooms = getAllReservedRooms(checkInDate, checkOutDate);
        return reservedRooms.contains(room);
    }

    public static IRoom getRoom(String roomNumber) {
        return roomMap.get(roomNumber);
    }
}
