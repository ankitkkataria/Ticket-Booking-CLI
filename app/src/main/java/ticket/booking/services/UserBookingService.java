package ticket.booking.services;

import java.io.IOException;
import java.util.ArrayList;
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
        File users = new File(USERS_PATH);
        return objectMapper.readValue(users, new TypeReference<List<User>>() {
        });
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
            return user1.getName().equals(this.user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword())
        }).findFirst(); // What find first does is just returns the first match of a username, password with the users stored in the database this might be done to save time.
        return foundUser.isPresent(); // foundUser is declared as Optional to take care of nullPtrException
    }

    public Boolean signUp(User user) {
        try {
            userList.add(user);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException ex) {
            return Boolean.FALSE;
        }
    }

    public void saveUserListToFile() {
        File users = new File(USERS_PATH);
        objectMapper.writeValue(users, userList);
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
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(src, destination);
        } catch (IOException e) {
            System.out.println("Couldn't get train data");
            return new ArrayList<>();
        }
    }
}
