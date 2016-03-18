package mrfu.swiperefreshboth;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by MrFu on 16/2/23.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "App", Toast.LENGTH_SHORT).show();
        Log.i("MrFu", "App");
    }
}
