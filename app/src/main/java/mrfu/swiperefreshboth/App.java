package mrfu.swiperefreshboth;

import android.app.Application;
import android.util.Log;

/**
 * Created by MrFu on 16/2/23.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("MrFu", "App");
    }
}
