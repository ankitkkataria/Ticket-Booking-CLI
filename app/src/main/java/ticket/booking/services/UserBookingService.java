package ticket.booking.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.Util.UserServiceUtil;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;

import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserBookingService {
    private User user;
    private List<User> userList;
    private ObjectMapper objectMapper; // For mapping the json objects in our database to the user class objects

    private static final String USERS_PATH = "../localDb/users.json";

    /// home/ankit/IRCTC/app/src/main/java/ticket/booking/localDb
    public List<User> loadUsers() {
        try {
            File users = new File(USERS_PATH);
            return objectMapper.readValue(users, new TypeReference<List<User>>() {
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    public UserBookingService() {
        loadUsers();
    }

    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUsers();
    }


    public Boolean loginUser() {
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(this.user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst(); // What find first does is just returns the first match of a username, password with the users stored in the database this might be done to save time.
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
        if(row > 0 && row < seats.size() && col > 0 && col < seats.get(row).size()) {
            if(seats.get(row).get(col) == 0) {
                seats.get(row).set(col, 1);
                trainSelectedForBooking.setSeats(seats);
                trainService.addTrain(trainSelectedForBooking);
                return Boolean.TRUE;
            }
            else {
                System.out.println("Couldn't book cause the seat has already been booked");
                return Boolean.FALSE;
            }
        } else {
            System.out.println("Couldn't book cause the row or col or both you've entered are wrong");
            return Boolean.FALSE;
        }
    }
}
