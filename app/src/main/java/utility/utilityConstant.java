package utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by abdull on 5/31/17.
 */

public class utilityConstant {
    public static final String ON = "ON";
    public static int spinnerItemPosition = 0;
    public static String MyPREFERENCES = "ScoreBatao";
    public static String requestCatche = "requestCatche";
    public static String emailRequest = "emailRequest";
    public static String signInMethod = "signInMethod";
    public static String facebook = "facebook";
    public static String custom = "custom";
    public static String email = "email";
    public static long NOTIFY_INTERVAL = 10 * 1000; // 10 seconds
    public static int CHECKCOUNT = 0; // 10 seconds
    public static String ODI = "One-Day Internationals";
    public static String STATUS = "ON";

    public static String OFF = "OFF";
    public static String UPDATE = "OFF";
    public static String INTEVAL = "INTERVAL";
    public static String EVENT = "EVENT";
    public static final String EVEN_FOUR = "FOUR";
    public static final String EVEN_SIX = "SIX";
    public static final String EVEN_OUT = "OUT";
    public static final String EVEN_NO_RUN = "no run";
    public static String EVEN_FOUR_DETAIL = "";
    public static String EVEN_OUT_DETAIL = "";
    public static String EVEN_SIX_DETAIL = "";
    public static String EVEN_NO_RUN_DETAIL = "";
    public static String lastRetireveOver = "lastRetireveOver";







    public static void showToast(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
    }


}
