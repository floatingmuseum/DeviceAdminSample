package floatingmuseum.devicemanagersample;

import android.os.Bundle;
import android.os.PersistableBundle;

import java.util.List;

/**
 * Created by floatingmuseum on 2016/6/6.
 */
public class MainActivity extends AppCompatPreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return DeviceAdminFragment.class.getName().equals(fragmentName)
                || DeviceOwnerFragment.class.getName().equals(fragmentName);
    }
}
