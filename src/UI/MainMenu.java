package UI;

import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Scanner;

public class MainMenu {

    public static void showOptions() {
        System.out.println("Welcome to the Hotel Reservation Application");
        System.out.println("\n-------------------------------------------------------");
        System.out.println("1. Find and reserve a room");
        System.out.println("2. See my reservation");
        System.out.println("3. Create an account");
        System.out.println("4. Admin");
        System.out.println("5. Exit");
        System.out.println("-------------------------------------------------------");
        System.out.println("Please select a number for the menu option");
    }


    public static boolean executeOption(Scanner scanner, int selection) {
        boolean keepRunning = true;
        switch (selection) {
            case 1 -> findAndReserveARoom(scanner);
            case 2 -> seeMyReservation(scanner);
            case 3 -> createAnAccount(scanner);
            case 4 -> runAdminMenu(scanner);
            case 5 -> {
                System.out.println("Goodbye, hope to see you soon");
                keepRunning = false;
            }
            default -> System.out.println("Please Enter a valid Input between 1 and 5");

//                HotelResource.main();
        }

        return keepRunning;
    }

    private static void runAdminMenu(Scanner scanner) {
        boolean keepAdminRunning = true;
        while (keepAdminRunning) {
                try {
                    AdminMenu.showOptions();
                    int adminSelection = Integer.parseInt(scanner.nextLine());
                    keepAdminRunning = AdminMenu.executeAdminOptions( scanner, adminSelection);
                } catch (Exception ex) {
                    System.out.println("\nPlease enter a number between 1 and 5\n");
                }
            }

    }

    private static String createAnAccount(Scanner scanner) {
        System.out.println("First name: ");
        String firstName = scanner.nextLine();
        System.out.println("Last name: ");
        String lastName = scanner.nextLine();
        String email = null;
        boolean validateEmail = false;
        while (!validateEmail) {
            try {
                System.out.println("Email (format: name@domain.com): ");
                email = scanner.nextLine();
                HotelResource.createCustomer(email, firstName, lastName);
                System.out.println("Account created successfully\n");
                validateEmail = true;
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        }
        return email;
    }

    private static void seeMyReservation(Scanner scanner) {
        System.out.println("Please enter your registered Email Address: ");
        String email = scanner.nextLine();
        Customer customer = HotelResource.getCustomer(email);
        if (customer == null) {
            System.out.println("No account exists with email address " + email);
            return;
        }
        Collection<Reservation> reservations = HotelResource.getCustomerReservations(customer.getEmail());
        if (reservations.isEmpty()) {
            System.out.println("You do not have any reservation at the moment");
            return;
        }
        for (Reservation reservation: reservations) {
            System.out.println(reservation.toString());
        }
    }

    private static void findAndReserveARoom(Scanner scanner) {
        // get valid check-in date input
        Date checkInDate = getValidCheckInDate(scanner);
        // get valid check-out date input
        Date checkOutDate = getValidCheckOutDate(scanner, checkInDate);
        // display available rooms (if any) and asks if user wants to book
        Collection<IRoom> availableRooms = HotelResource.findRooms(checkInDate, checkOutDate);
        boolean wantsToBook = false;
        if (availableRooms.isEmpty()) {
            // recommend other dates: expand check-in/out dates by 7 days and check for available rooms again
            Date newCheckInDate = getRecommendedDate(checkInDate);
            Date newCheckOutDate = getRecommendedDate(checkOutDate);
            availableRooms = HotelResource.findRooms(newCheckInDate, newCheckOutDate);
            if (!availableRooms.isEmpty()) {
                System.out.println("There are no available rooms for those dates. Rooms available for alternative dates, check-in on " + newCheckInDate + " and check-out on " + newCheckOutDate);
                wantsToBook = showAvailableRoomsAndAskToBook(scanner, availableRooms);
                checkInDate = newCheckInDate;
                checkOutDate = newCheckOutDate;
            } else {
                System.out.println("There are no available rooms for those dates");
            }
        } else {
            System.out.println("Available rooms for check-in on " + checkInDate + " and check-out on " + checkOutDate);
            wantsToBook = showAvailableRoomsAndAskToBook(scanner, availableRooms);
        }
        if (!wantsToBook) {
            return;
        }
        // ask if user has an account, if yes, ask for their email, else create a new account
        Customer customer = getCustomerForReservation(scanner);
        if (customer == null) {
            System.out.println("Sorry, no account exists for that email");
            return;
        }
        // ask what room would they like to reserve, ask for room number, validate it's available
        IRoom room = getRoomForReservation(scanner, availableRooms);
        // finally, book room and show reservation details
        Reservation reservation = HotelResource.bookRoom(customer.getEmail(), room, checkInDate, checkOutDate);
        if (reservation == null) {
            System.out.println("Couldn't process your booking, the room is not available"); // this shouldn't happen as we validated that the room is available previously
        } else {
            System.out.println("Thank you! Your room was booked successfully!");
            System.out.println(reservation);
        }
        
    }

    private static Customer getCustomerForReservation(Scanner scanner) {
        String email;
        boolean hasAccount = false;
        System.out.println("Do you already have an account with us? Enter y/yes, or any other character for no:");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yes")) {
            hasAccount = true;
        }
        if (hasAccount) {
            System.out.println("Please enter your Email (format: name@example.com): ");
            email = scanner.nextLine();
        } else {
            email = createAnAccount(scanner);
        }
        return HotelResource.getCustomer(email);
    }

    private static IRoom getRoomForReservation(Scanner scanner, Collection<IRoom> availableRooms) {
        IRoom room = null;
        String roomNumber;
        boolean validRoomNumber = false;
        while (!validRoomNumber) {
            System.out.println("What room would you like to reserve? Enter the room number: ");
            roomNumber = scanner.nextLine();
            room = HotelResource.getARoom(roomNumber);
            if (room == null) { // room do not exist, ask again
                System.out.println("That room doesn't exists, please enter a valid room number");
            } else { // room exists, validate it's available
                if (!availableRooms.contains(room)) { // room not available, ask again
                    System.out.println("That room is not available, please enter a valid room number");
                } else {
                    validRoomNumber = true;
                }
            }
        }
        return room;
    }

    private static boolean showAvailableRoomsAndAskToBook(Scanner scanner, Collection<IRoom> availableRooms) {
        for (IRoom room : availableRooms) {
            System.out.println(room.toString());
        }
        System.out.println();
        System.out.println("Would you like to book a room? Enter y/yes, or any other character for no:");
        String choice = scanner.nextLine();
        return choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yes");
    }

    private static Date getRecommendedDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 7);
        return c.getTime();
    }

    private static Date getValidCheckOutDate(Scanner scanner, Date checkInDate) {
        SimpleDateFormat DateFor = new SimpleDateFormat("MM/dd/yyyy");
        Date checkOutDate = null;
        boolean validCheckOutDate = false;
        while (!validCheckOutDate) {
            System.out.println("Check-out date (mm/dd/yyyy): ");
            String inputCheckOutDate = scanner.nextLine();
            try {
                checkOutDate = DateFor.parse(inputCheckOutDate);
                if (checkOutDate.before(checkInDate)) { // check-out date can't be before the check-in date
                    System.out.println("The check-out date can't be before the check-in date");
                } else {
                    validCheckOutDate = true;
                }
            } catch (ParseException ex) {
                System.out.println("Invalid date format, please use dd/mm/yyyy");
            }
        }
        return checkOutDate;
    }

    private static Date getValidCheckInDate(Scanner scanner) {
        SimpleDateFormat DateFor = new SimpleDateFormat("MM/dd/yyyy");
        Date checkInDate = null;
        boolean validCheckInDate = false;
        while (!validCheckInDate) {
            System.out.println("Check-in date (mm/dd/yyyy): ");
            String inputCheckInDate = scanner.nextLine();
            try {
                checkInDate = DateFor.parse(inputCheckInDate);
                Date today = new Date();
                if (checkInDate.before(today)) { // check-in date can't be in the past
                    System.out.println("The check-in date cannot be in the past");
                } else {
                    validCheckInDate = true;
                }
            } catch (ParseException ex) {
                System.out.println("Invalid date format, please use dd/mm/yyyy");
            }
        }
        return checkInDate;
    }



}
