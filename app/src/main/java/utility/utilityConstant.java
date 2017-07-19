package utility;

import android.content.Context;
import android.widget.Toast;

import com.example.abdull.scorebatao.R;

import java.util.HashMap;

/**
 * Created by abdull on 5/31/17.
 */

public class utilityConstant {
    public static final String ON = "ON";
    public static final int REQUEST_SELECT_CONTACT = 1;
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
    public static String FIRSTCLASS = "First-class";
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

    public static HashMap storeFlag()
    {
        HashMap<String,Integer> flags=new HashMap();
        flags.put("India", R.drawable.india);
        flags.put("Pakistan", R.drawable.pakistan);
        flags.put("Zimbabwe", R.drawable.zimbabwe);
        flags.put("Afghanistan", R.drawable.afghanistan);
        flags.put("Australia", R.drawable.australia);
        flags.put("Bangladesh", R.drawable.bangladesh);
        flags.put("South Africa", R.drawable.southafrica);
        flags.put("New Zealand", R.drawable.newzeland);
        flags.put("UAE", R.drawable.uae);
        flags.put("Ireland", R.drawable.ireland);
        flags.put("England", R.drawable.england);
        flags.put("Sri Lanka", R.drawable.srilanka);
        flags.put("West Indies", R.drawable.westindies);
        flags.put("Scotland", R.drawable.scotland);
        flags.put("Worcestershire", R.drawable.worcestershire);
        flags.put("Gloucestershire", R.drawable.gloucestershire);
        flags.put("Yorkshire", R.drawable.yorkshire);
        flags.put("Northamptonshire", R.drawable.northamptonshire);
        flags.put("Hampshire", R.drawable.hampshire);
        flags.put("Sussex", R.drawable.sussex);
        flags.put("Kent", R.drawable.kent);
        flags.put("Surrey", R.drawable.surrey);
        flags.put("Middlesex", R.drawable.middlesex);
        flags.put("Somerset", R.drawable.somerset);
        flags.put("Essex", R.drawable.essex);
        flags.put("Leicestershire", R.drawable.leicestershire);
        flags.put("Warwickshire", R.drawable.warwickshire);
        flags.put("Lancashire", R.drawable.lancashire);
        flags.put("Nottinghamshire", R.drawable.nottinghamshire);


        flags.put("Derbyshire", R.drawable.derbishire);
        flags.put("Canada", R.drawable.canada);
        flags.put("Bermuda", R.drawable.bermuda);
        flags.put("Durham", R.drawable.durham);
        flags.put("Singapore", R.drawable.singapoor);
        flags.put("Malaysia", R.drawable.malaysia);
        flags.put("Nepal", R.drawable.nepal);
        flags.put("Glamorgan", R.drawable.glamorgan);
        flags.put("United States of America", R.drawable.usa);
        return flags;
    }

    public static  int flagOfTeam(String team)
    {
        HashMap<String,Integer> flag=utilityConstant.storeFlag();

        for (HashMap.Entry set:flag.entrySet()) {

            if(team.contains(set.getKey().toString()))
            {
                return (int) set.getValue();
            }
        }
        return R.drawable.noteamfound;
    }








    public static void showToast(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
    }


}
