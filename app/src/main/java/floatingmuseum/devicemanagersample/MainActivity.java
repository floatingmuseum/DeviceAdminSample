package floatingmuseum.devicemanagersample;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import java.util.List;

/**
 * Created by yan on 2016/6/6.
 */
public class MainActivity extends AppCompatPreferenceActivity {
    DevicePolicyManager dpm;
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
