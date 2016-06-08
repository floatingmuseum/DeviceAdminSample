package floatingmuseum.devicemanagersample;

import android.app.Application;
import android.content.Context;

/**
 * Created by yan on 2016/6/6.
 */
public class App extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
