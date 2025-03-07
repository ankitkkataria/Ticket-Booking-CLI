package ticket.booking.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAINS_PATH = "app/src/main/java/ticket/booking/localDb/trains.json";

    public List<Train> loadTrains() {
     try {
            File trains = new File(TRAINS_PATH);
            return objectMapper.readValue(trains, new TypeReference<List<Train>>() {
            });
        }
        catch (IOException ex) {
            System.out.println("Threw is IO exception in the TrainService my boi");
            ex.printStackTrace();
        }
     return new ArrayList<>();
    }

    public TrainService() {
        trainList = loadTrains();
    }

    public List<Train> searchTrains(String src, String destination) {
        return trainList.stream().filter(train -> validTrain(train, src, destination)).collect(Collectors.toList());
    }

    private Boolean validTrain(Train train, String src, String destination) {
        List<String> stationOrder = train.getStations();
        int srcIndex = stationOrder.indexOf(src.toLowerCase());
        int destinationIndex = stationOrder.indexOf(destination.toLowerCase());
        return srcIndex != -1 && destinationIndex != -1 && srcIndex < destinationIndex;
    }

    public void addTrain(Train t) {
        Optional<Train> existingTrain = trainList.stream().filter(train -> train.getTrainId().equalsIgnoreCase(t.getTrainId())).findFirst();
        if (existingTrain.isPresent()) {
            updateTrain(t);
        } else {
            trainList.add(t);
            saveTrainListToFile();
        }
    }

    public void updateTrain(Train t) {
        OptionalInt index = IntStream.range(0, trainList.size()).filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(t.getTrainId())).findFirst();
        if (index.isPresent()) {
            trainList.set(index.getAsInt(), t);
            saveTrainListToFile();
        } else {
            addTrain(t);
        }
    }

    public void saveTrainListToFile() {
        try {
            File trains = new File(TRAINS_PATH);
            objectMapper.writeValue(trains, trainList);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

