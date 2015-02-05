package bg.mentormate.academy.radarapp.tools;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by tl on 05.02.15.
 */
public class DialogHelper {

    public static void showAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
