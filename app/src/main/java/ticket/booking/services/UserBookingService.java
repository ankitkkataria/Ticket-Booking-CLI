package ticket.booking.services;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.Util.UserServiceUtil;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;

import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserBookingService {
    private User user;
    private List<User> userList;
    private ObjectMapper objectMapper = new ObjectMapper(); // For mapping the json objects in our database to the user class objects

    private static final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    /// home/ankit/IRCTC/app/src/main/java/ticket/booking/localDb
    public List<User> loadUsers() {
        try {
            File users = new File(USERS_PATH);
            return objectMapper.readValue(users, new TypeReference<List<User>>() {
            });
        } catch (IOException ex) {
            System.out.println("Threw is IO exception my boi");
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    public UserBookingService() {
        userList = loadUsers();
    }

    public UserBookingService(User user) throws IOException {
        this.user = user;
        userList = loadUsers();
    }

    public void updateUserInFile(User u) {
        OptionalInt index = IntStream.range(0, userList.size()).filter(i -> userList.get(i).getUserId().equalsIgnoreCase(u.getUserId())).findFirst();
        if (index.isPresent()) {
            userList.set(index.getAsInt(), u);
            System.out.println("Printing each user's tickets");
            for(User x : userList) {
              x.printTicketsInfo();
            }
            saveUserListToFile();
        }
    }
    public Boolean loginUser() {
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(this.user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst(); // What find first does is just returns the first match of a username, password with the users stored in the database this might be done to save time.

        if (foundUser.isPresent())
            System.out.println("Login you in...");
        else
            System.out.println("Couldn't log you in!");
        return foundUser.isPresent(); // foundUser is declared as Optional to take care of nullPtrException
    }

    public Boolean signUp(User user) {
        userList.add(user);
        saveUserListToFile();
        return Boolean.TRUE;
    }

    public void saveUserListToFile() {
        try {
            File users = new File(USERS_PATH);
            objectMapper.writeValue(users, userList);
        } catch (IOException ex) {
            System.out.println("Not able to write in users file");
            ex.printStackTrace();
        }
    }

    public void fetchBooking() {
        user.printTicketsInfo();
    }

    public void cancelBooking(String ticketId) {
        List<Ticket> ticketsBooked = user.getTicketsBooked();
        List<Ticket> modifiedList = ticketsBooked.stream().filter(t -> !(t.getTicketId().equals(ticketId))).collect(Collectors.toList());
        user.setTicketsBooked(modifiedList);
    }

    public List<Train> searchTrains(String src, String destination) {
        TrainService trainService = new TrainService();
        return trainService.searchTrains(src, destination);
    }

    public List<List<Integer>> fetchSeats(Train t) {
        return t.getSeats();
    }


    public Boolean bookTicket(Train trainSelectedForBooking, int row, int col) {
        TrainService trainService = new TrainService();
        List<List<Integer>> seats = trainSelectedForBooking.getSeats();
        if (row > 0 && row < seats.size() && col > 0 && col < seats.get(row).size()) {
            if (seats.get(row).get(col) == 0) {
                seats.get(row).set(col, 1);
                trainSelectedForBooking.setSeats(seats);
                trainService.addTrain(trainSelectedForBooking);
                Ticket t = new Ticket(UUID.randomUUID().toString(), user.getUserId(), "random", "random", "xxxxxx", trainSelectedForBooking);
                List<Ticket> alreadyBookedTickets = user.getTicketsBooked();
                alreadyBookedTickets.add(t);
                user.setTicketsBooked(alreadyBookedTickets);
                UserBookingService userBookingService = new UserBookingService();
                userBookingService.updateUserInFile(user);
                System.out.println("Updated the users.json file as well");
                return Boolean.TRUE;
            } else {
                System.out.println("Couldn't book cause the seat has already been booked");
                return Boolean.FALSE;
            }
        } else {
            System.out.println("Couldn't book cause the row or col or both you've entered are wrong");
            return Boolean.FALSE;
        }
    }
}
