package bg.mentormate.academy.radarapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import java.util.List;

import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by tl on 19.02.15.
 */
public class RegisterButton extends ToggleButton {

    public RegisterButton(Context context) {
        super(context);
    }

    public RegisterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(User currentUser, Room selectedRoom) {
        List<User> users = selectedRoom.getUsers();

        setChecked (users.contains(currentUser));
    }
}
