package ticket.booking.entities;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Train {

    private String trainId;
    private String trainNo;
    List<List<Integer>>seats;
    List<String>stations;
    Map<String, String> stationTimes; // Time is usually put in strings and later parsed


    public Train(){}

    public Train(String trainId, String trainNo, List<List<Integer>> seats, Map<String, String> stationTimes, List<String> stations){
        this.trainId = trainId;
        this.trainNo = trainNo;
        this.seats = seats;
        this.stationTimes = stationTimes;
        this.stations = stations;
    }

    public List<String> getStations(){
        return stations;
    }

    public List<List<Integer>> getSeats() {
        return seats;
    }

    public void setSeats(List<List<Integer>> seats){
        this.seats = seats;
    }

    public String getTrainId(){
        return trainId;
    }

    public Map<String, String> getStationTimes(){
        return stationTimes;
    }

    public String getTrainNo(){
        return trainNo;
    }

    public void setTrainNo(String trainNo){
        this.trainNo = trainNo;
    }

    public void setTrainId(String trainId){
        this.trainId = trainId;
    }

    public void setStationTimes(Map<String, String> stationTimes){
        this.stationTimes = stationTimes;
    }

    public void setStations(List<String> stations){
        this.stations = stations;
    }

    public String getTrainInfo(){
        // Returning a formated string
        return String.format("Train ID: %s Train No: %s", trainId, trainNo);
    }
}
