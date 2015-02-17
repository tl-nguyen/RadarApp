package bg.mentormate.academy.radarapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.Constants;
import bg.mentormate.academy.radarapp.models.Room;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by tl on 10.02.15.
 */
public class RoomsAdapter extends ParseQueryAdapter<Room> {

    private static final int LIMIT = 50;

    private Context mContext;

    public RoomsAdapter(final Context context, final String searchQuery) {
        super(context, new QueryFactory<Room>() {

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

        mContext = context;
    }

    @Override
    public View getItemView(Room room, View v, ViewGroup parent) {
        View row = v;
        final Room selectedRoom = room;
        final RoomHolder holder;

        if (row == null) {
            row = LayoutInflater.from(mContext).inflate(R.layout.item_list_room, parent, false);

            holder = new RoomHolder();
            holder.roomName = (TextView) row.findViewById(R.id.tvRoomName);
            holder.username = (TextView) row.findViewById(R.id.tvUsername);
            holder.avatar = (ParseImageView) row.findViewById(R.id.pivAvatar);

            row.setTag(holder);
        } else {
            holder = (RoomHolder) row.getTag();
        }

        super.getItemView(selectedRoom, v, parent);

        if (room != null) {
            final User owner = room.getCreatedBy();

            owner.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    holder.roomName.setText(selectedRoom.getName());
                    holder.username.setText(owner.getUsername());
                    holder.avatar.setParseFile(owner.getAvatar());
                    holder.avatar.loadInBackground();
                }
            });
        }

        return row;
    }

    static class RoomHolder {
        private TextView roomName;
        private TextView username;
        private ParseImageView avatar;
    }
}
