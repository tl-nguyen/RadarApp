package bg.mentormate.academy.radarapp.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;
import bg.mentormate.academy.radarapp.views.RoomItem;

/**
 * Created by tl on 10.02.15.
 */
public class RoomAdapter extends ParseQueryAdapter<Room> {

    private static final int LIMIT = 50;

    public RoomAdapter(final Activity hostActivity, final String searchQuery) {
        super(hostActivity, new QueryFactory<Room>() {

            @Override
            public ParseQuery<Room> create() {
                ParseQuery query = new ParseQuery(Constants.ROOM_TABLE);
                query.orderByDescending(Constants.PARSE_COL_CREATED_AT);

                if (searchQuery != null) {
                    query.whereContains(Constants.ROOM_COL_NAME, searchQuery);
                }

                query.setLimit(LIMIT);

                return query;
            }
        });
    }

    @Override
    public View getItemView(Room room, View v, ViewGroup parent) {
        if (v == null) {
            v = new RoomItem(getContext());
        }

        User owner = room.getCreatedBy();

        ((RoomItem) v).setData(room, owner);

        return v;
    }
}
