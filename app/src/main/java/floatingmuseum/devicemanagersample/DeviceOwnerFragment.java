package floatingmuseum.devicemanagersample;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by yan on 2016/6/6.
 */
public class DeviceOwnerFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_deviceowner);
    }
}
