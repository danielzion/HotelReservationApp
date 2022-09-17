package UI;

import model.*;
import api.AdminResource;
import api.HotelResource;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Scanner;

public class AdminMenu {
    public static void showOptions(){
        System.out.println("Admin Menu");
        System.out.println("-------------------------------------------------------");
        System.out.println("1. See all Customers");
        System.out.println("2. See all Rooms");
        System.out.println("3. See all Reservations");
        System.out.println("4. Add a Room");
        System.out.println("5. Add Test Data");
        System.out.println("6. Back to Main Menu");
        System.out.println("-------------------------------------------------------");
        System.out.println("Please enter your selection");
    }

    public static boolean executeAdminOptions(Scanner scanner, int adminSelection) {
        boolean keepRunningAdmin = true;
        switch (adminSelection) {
            case 1 -> seeAllCustomers();
            case 2 -> seeAllRooms();
            case 3 -> seeAllReservations();
            case 4 -> addARoom(scanner);
            case 5 -> addTestData();
            case 6 -> keepRunningAdmin = false;
            default -> System.out.println("Please Enter a valid Input between 1 and 5");
        }
        return keepRunningAdmin;
    }



    private static void seeAllCustomers() {
        Collection<Customer> allCustomers = AdminResource.getAllCustomers();
        if (allCustomers.isEmpty()) {
            System.out.println("There are no customers at the moment");
        } else {
            for (Customer customer: allCustomers) {
                System.out.println(customer.toString());
            }
        }
        System.out.println();
    }

    private static void seeAllRooms() {
        Collection<IRoom> allRooms = AdminResource.getAllRooms();
        if (allRooms.isEmpty()) {
            System.out.println("There are no rooms at the moment");
        } else {
            for (IRoom room: allRooms) {
                System.out.println(room.toString());
            }
        }
        System.out.println();
    }

    private static void seeAllReservations() {
        Collection<Reservation> allReservations = AdminResource.getAllReservation();
        if (allReservations.isEmpty()) {
            System.out.println("There are no reservations at the moment");
        } else {
            for (Reservation reservation: allReservations) {
                System.out.println(reservation.toString());
            }
        }
        System.out.println();
    }

    private static void addARoom(Scanner scanner) {
    // get room number input
    String roomNumber = null;
    boolean validateRoomNumber = false;
    while (!validateRoomNumber) {
        System.out.println("Enter room number: ");
        roomNumber = scanner.nextLine();
        IRoom roomExist = HotelResource.getARoom(roomNumber);
        if (roomExist == null) {
            // room does not exist, continue
            validateRoomNumber = true;
        } else {
            // room exists, either continue and edit its type and price, or enter a new room number
            System.out.println("That room already exists. Enter y/yes to update room, or any other character to enter another room number: ");
            String userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("yes")) {
                validateRoomNumber = true;
            }
        }

    }
    // get valid room type input
    RoomType roomType = null;
    boolean validateRoomType = false;
    while (!validateRoomType) {
        try {
            System.out.println("Enter a room type (1 for 'SINGLE' bed and 2 for 'DOUBLE' bed): ");
            roomType = RoomType.valueOfNumberOfBeds(Integer.parseInt((scanner.nextLine())));
            if (roomType == null) {
                System.out.println("Please enter a valid room type");
            } else {
                validateRoomType = true;
            }
        } catch (Exception ex) {
            System.out.println("Please enter a valid room type");
        }
    }

    // get valid room price input
    double price = 0.00;
    boolean validateRoomPrice = false;
    while (!validateRoomPrice) {
        try {
            System.out.println("Enter a price per night: ");
            price = Double.parseDouble(scanner.nextLine());
            if (price < 0) {
                System.out.println("The price must be greater than or equal to 1");
            } else {
                validateRoomPrice = true;
            }
        } catch (Exception ex) {
            System.out.println("Please enter a valid Price");
        }
    }

    // create and add a new room
    Room newRoom = new Room(roomNumber, price, roomType);
    AdminResource.addRoom(newRoom);

    }
    private static void addTestData() {
        // add some rooms
        String roomNumber;
        Double price;
        RoomType roomType;
        for (int i = 1; i <=3; i++) {
            roomNumber = Integer.toString(i);
            if (i % 2 == 0) {
                roomType = RoomType.valueOfNumberOfBeds(2);
                price = 200.00;
            } else {
                roomType = RoomType.valueOfNumberOfBeds(1);
                price = 100.00;
            }
            Room newRoom = new Room(roomNumber, price, roomType);
            AdminResource.addRoom(newRoom);
        }
        // add some customer accounts
        HotelResource.createCustomer("test1@mail.com", "Williams", "Richard");
        HotelResource.createCustomer("test2@mail.com", "Steve", "Jobs");
        HotelResource.createCustomer("test3@mail.com", "Elon", "Musk");
        HotelResource.createCustomer("test4@mail.com", "Bill", "Gates");
        HotelResource.createCustomer("test5@mail.com", "Albert", "Einstein");
        // book some rooms
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        Date checkInDate;
        Date checkOutDate;
        // reservation 1
        c.setTime(today);
        c.add(Calendar.DATE, 2);
        checkInDate = c.getTime();
        c.setTime(checkInDate);
        c.add(Calendar.DATE, 5);
        checkOutDate = c.getTime();
        HotelResource.bookRoom("test1@mail.com", HotelResource.getARoom("1"), checkInDate, checkOutDate);
        // reservation 2
        c.setTime(today);
        c.add(Calendar.DATE, 4);
        checkInDate = c.getTime();
        c.setTime(checkInDate);
        c.add(Calendar.DATE, 10);
        checkOutDate = c.getTime();
        HotelResource.bookRoom("test3@mail.com", HotelResource.getARoom("2"), checkInDate, checkOutDate);
        // reservation 3
        c.setTime(today);
        c.add(Calendar.DATE, 5);
        checkInDate = c.getTime();
        c.setTime(checkInDate);
        c.add(Calendar.DATE, 3);
        checkOutDate = c.getTime();
        HotelResource.bookRoom("test4@mail.com", HotelResource.getARoom("3"), checkInDate, checkOutDate);

        System.out.println("Test data added!");

    }
}

