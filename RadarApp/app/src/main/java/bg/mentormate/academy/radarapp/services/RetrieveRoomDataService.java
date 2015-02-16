package bg.mentormate.academy.radarapp.services;

import android.app.IntentService;
import android.content.Intent;

import com.parse.ParseException;

import java.util.List;

import bg.mentormate.academy.radarapp.data.LocalDb;
import bg.mentormate.academy.radarapp.models.CurrentLocation;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;

public class RetrieveRoomDataService extends IntentService {

    public static final String BROADCAST_RESULT = "bg.mentormate.academy.BROADCAST_RESULT";

    public RetrieveRoomDataService() {
        super(RetrieveRoomDataService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocalDb.getInstance().getSelectedRoom() != null) {
            try {
                Room selectedRoom = LocalDb.getInstance().getSelectedRoom();

                selectedRoom.fetch();

                List<User> users = selectedRoom.getUsers();

                for (User user: users) {
                    user.fetchIfNeeded();
                    CurrentLocation currentLocation = user.getCurrentLocation();
                    currentLocation.fetch();
                }

                Intent broadcastIntent = new Intent(BROADCAST_RESULT);
                sendBroadcast(broadcastIntent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
