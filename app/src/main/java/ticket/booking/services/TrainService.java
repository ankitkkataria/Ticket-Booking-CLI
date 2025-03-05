package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class TrainService {

    private List<Train> trainList;
    private ObjectMapper objectMapper;

    private static final String TRAINS_PATH = "../localDb/trains.json";

    public List<Train> loadTrains() {
        File trains = new File(TRAINS_PATH);
        return objectMapper.readValue(trains, new TypeReference<List<Train>>() {
        });
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
}

