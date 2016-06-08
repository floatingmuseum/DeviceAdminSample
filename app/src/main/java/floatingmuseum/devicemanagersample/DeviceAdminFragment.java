package floatingmuseum.devicemanagersample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.inputmethod.InputMethodManager;

import com.orhanobut.logger.Logger;

/**
 * Created by yan on 2016/6/6.
 */
public class DeviceAdminFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private DevicePolicyManager dpm;
    private PackageManager pm;
    private ComponentName mComponentName;
    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_deviceadmin);
        initUI();
        initListener();
        activity = DeviceAdminFragment.this.getActivity();
        if (dpm == null) {
            dpm = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        }

        if (pm == null) {
            pm = activity.getPackageManager();
        }
        mComponentName = MyDeviceAdminReceiver.getComponentName(activity);

    }

    private void initUI() {
//        int number = SPUtil.getInt(App.context, "min_length", 0);
//
//        Preference p = findPreference("min_length");
//        Logger.d("number:" + number + "...p:" + p);
//        EditTextPreference et = (EditTextPreference) findPreference("min_length");
//        et.setSummary("" + number);
//        ((EditTextPreference) findPreference("min_letters")).setSummary(SPUtil.getInt(App.context, "min_letters", 0) + "");
    }

    private void initListener() {
//        findPreference("deviceadmin_support").setOnPreferenceClickListener(this);
        findPreference("deviceadmin_enabled").setOnPreferenceClickListener(this);
        findPreference("get_deviceadmin").setOnPreferenceClickListener(this);
        findPreference("remove_deviceadmin").setOnPreferenceClickListener(this);
        findPreference("camera_status").setOnPreferenceClickListener(this);
        findPreference("disabled_camera").setOnPreferenceClickListener(this);
        findPreference("lock_screen").setOnPreferenceClickListener(this);
        findPreference("reset_password").setOnPreferenceChangeListener(this);
        findPreference("password_quality").setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
//            case "deviceadmin_support":
//                ToastUtil.show("是否支持DeviceAdmin:"+checkSupportDeviceAdmin());
//            break;
            case "deviceadmin_enabled":
                ToastUtil.show("是否已激活Device Admin权限:" + checkDeviceAdminEnabled());
                break;
            case "get_deviceadmin":
                getDeviceAdmin();
                break;
            case "remove_deviceadmin":
                removeAdmin();
                break;
            case "camera_status":
                if (checkCameraState()) {
                    ToastUtil.show("相机已禁用");
                } else {
                    ToastUtil.show("相机已开启");
                }
                break;
            case "disabled_camera":
                disableCamera();
                break;
            case "lock_screen":
                dpm.lockNow();
                break;
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "reset_password":
                resetPassword((String) newValue);
                break;
            case "min_length":
//                changePasswordLength((int) newValue);
                break;
            case "min_letters":
//                changePasswordLetters((int) newValue);
                break;
            case "password_quality":
                changePasswordQuality(newValue.toString());
                break;
        }
        return false;
    }

    /**
     * To create a work profile on a device that already has a personal profile,
     * first find out whether the device can support a work profile. To do this,
     * check whether the device supports the FEATURE_MANAGED_USERS system feature:
     */
    private boolean checkSupportDeviceAdmin() {
        return pm.hasSystemFeature(PackageManager.FEATURE_MANAGED_USERS);
    }

    private boolean checkDeviceAdminEnabled() {
        return dpm.isAdminActive(mComponentName);
    }

    private void getDeviceAdmin() {
        if (checkDeviceAdminEnabled()) {
            ToastUtil.show("已激活");
            return;
        }
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                mComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                getString(R.string.device_admin_description));
        startActivity(intent);
    }

    private void removeAdmin() {
        dpm.removeActiveAdmin(mComponentName);
    }

    private boolean checkCameraState() {
        return dpm.getCameraDisabled(mComponentName);
    }


    private void disableCamera() {
        if (checkDeviceAdminEnabled()) {
            dpm.setCameraDisabled(mComponentName,
                    !checkCameraState());
            if (checkCameraState()) {
                ToastUtil.show("相机已禁用");
            } else {
                ToastUtil.show("相机已开启");
            }
        } else {
            ToastUtil.show("权限未获取");
        }
    }

    private void resetPassword(String password) {
        if (!checkDeviceAdminEnabled()) {
            ToastUtil.show("权限未获取");
            return;
        }
        if (password.length() < 5) {
            ToastUtil.show("密码长度不得小于5位");
            return;
        }
        Logger.d("password:" + password
                + "...quality:" + dpm.getPasswordQuality(mComponentName)
                + "...minimumLength:" + dpm.getPasswordMinimumLength(mComponentName));
        boolean succeeded = dpm.resetPassword(password, 0);
        if (succeeded) {
            ToastUtil.show("设置成功");
        } else {
            ToastUtil.show("设置失败");
        }

//        dpm.setPasswordExpirationTimeout(mComponentName,60*1000);
    }


    private void changePasswordQuality(String quality) {
        switch (quality) {
            case "1":
                dpm.setPasswordQuality(mComponentName, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
                break;
            case "2":
                /**
                 * 不起作用
                 * the user must have entered a password containing at least numeric characters.
                 * Note that quality constants are ordered so that higher values are more restrictive.
                 */
                dpm.setPasswordQuality(mComponentName, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC);
                break;
            case "3":
                /**
                 * 未测试
                 */
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    ToastUtil.show("此选项适用于Android5.0版本以上");
                    return;
                }
                dpm.setPasswordQuality(mComponentName, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC_COMPLEX);
                break;
            case "4":
                dpm.setPasswordQuality(mComponentName, DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
                break;
            case "5":
                dpm.setPasswordQuality(mComponentName, DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC);
                break;
            case "6":
                dpm.setPasswordQuality(mComponentName, DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
                break;
        }
        Logger.d("quality" + dpm.getPasswordQuality(mComponentName));
    }
//    private void changePasswordLength(int newValue) {
//        dpm.setPasswordMinimumLength(mComponentName,newValue);
//        Logger.d("最小长度;"+dpm.getPasswordMinimumLength(mComponentName));
//    }
//    private void changePasswordLetters(int newValue) {
//        dpm.setPasswordMinimumLetters(mComponentName,newValue);
//        Logger.d("最少");
//    }
}
