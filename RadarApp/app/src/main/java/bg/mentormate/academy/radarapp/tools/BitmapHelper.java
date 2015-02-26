package bg.mentormate.academy.radarapp.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.widget.ImageView;

import com.google.maps.android.ui.IconGenerator;
import com.parse.ParseException;

import bg.mentormate.academy.radarapp.R;
import bg.mentormate.academy.radarapp.models.User;

/**
 * Created by tl on 25.02.15.
 */
public class BitmapHelper {

    public static Bitmap buildAvatarIcon(Context context,
                                         User user,
                                         ImageView imageView,
                                         IconGenerator iconGenerator) {
        Bitmap avatarIcon = null;
        Bitmap scaledBitmap;

        try {
            Bitmap fetchedAvatar;

            if (user.getAvatar() != null) {
                byte[] bytes = user.getAvatar().getData();
                fetchedAvatar= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } else {
                fetchedAvatar = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_avatar);
            }

            scaledBitmap = Bitmap.createScaledBitmap(
                    fetchedAvatar,
                    100, 100,
                    true);

            imageView.setImageBitmap(scaledBitmap);

            setIconStyle(user, iconGenerator);

            avatarIcon = iconGenerator.makeIcon();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return avatarIcon;
    }

    private static void setIconStyle(User user, IconGenerator iconGenerator) {
        String provider = user.getCurrentLocation().getProvider();
        boolean isActive = user.getCurrentLocation().getActive();

        if (isActive) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                iconGenerator.setStyle(IconGenerator.STYLE_GREEN);
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                iconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
            }
        } else {
            iconGenerator.setStyle(IconGenerator.STYLE_RED);
        }
    }
}
