package floatingmuseum.devicemanagersample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.List;

import floatingmuseum.devicemanagersample.util.RootUtil;
import floatingmuseum.devicemanagersample.util.ToastUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yan on 2016/6/6.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DeviceOwnerFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private DevicePolicyManager dpm;
    private PackageManager pm;
    private ComponentName mComponentName;
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_deviceowner);
        initListener();
        activity = DeviceOwnerFragment.this.getActivity();
        if (dpm == null) {
            dpm = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        }

        if (pm == null) {
            pm = activity.getPackageManager();
        }
        mComponentName = MyDeviceAdminReceiver.getComponentName(activity);
    }

    private void initListener() {
        findPreference("deviceowner_enabled").setOnPreferenceClickListener(this);
        findPreference("remove_deviceowner").setOnPreferenceClickListener(this);
        findPreference("check_root").setOnPreferenceClickListener(this);
        findPreference("enabled_deviceowner_rooted").setOnPreferenceClickListener(this);
        findPreference("hide_app").setOnPreferenceClickListener(this);
        findPreference("set_app_restrictions").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "deviceowner_enabled":
                if (checkDeviceOwnerEnabled()) {
                    ToastUtil.show("此应用已激活为DeviceOwner");
                } else {
                    ToastUtil.show("未激活");
                }
                break;
            case "remove_deviceowner":
                removeDeviceOwner();
                break;
            case "check_root":
                if(RootUtil.isRooted()){
                    ToastUtil.show("已root");
                }else{
                    ToastUtil.show("未root");
                }
                break;
            case "enabled_deviceowner_rooted":
                enabledDeviceOwnerOnRooted();
                break;
            case "hide_app":
                selectApp(APP_HIDE);
                break;
            case "set_app_restrictions":
                selectApp(APP_RESTRICTIONS);
                break;
        }
        return true;
    }

    private boolean checkDeviceOwnerEnabled() {
        return dpm.isDeviceOwnerApp(activity.getPackageName());
    }

    private void removeDeviceOwner() {
        if (dpm.isDeviceOwnerApp(activity.getPackageName())) {
            dpm.clearDeviceOwnerApp(activity.getPackageName());
            if (dpm.isDeviceOwnerApp(activity.getPackageName())) {
                ToastUtil.show("移除失败");
            } else {
                ToastUtil.show("移除成功");
            }
        } else {
            ToastUtil.show("此应用不是DeviceOwner");
        }
    }

    private void enabledDeviceOwnerOnRooted() {
        if (!RootUtil.isRooted()) {
            ToastUtil.show("系统未root");
            return;
        }


        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> observer) {
                enabledDeviceOwner();
                boolean enabled = checkDeviceOwnerEnabled();
                observer.onNext(enabled);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean item) {
                        Logger.d("Next: " + item);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Logger.d("error: ");
                    }

                    @Override
                    public void onCompleted() {
                        Logger.d("onCompleted: ");
                    }
                });
    }

    private void enabledDeviceOwner() {
        try {
            Runtime.getRuntime().exec("dpm set-device-owner floatingmuseum.devicemanagersample/floatingmuseum.devicemanagersample.MyDeviceAdminReceiver");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final int APP_HIDE = 0;
    private static final int APP_RESTRICTIONS = 1;

    private void selectApp(final int flag) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> resolveInfos = pm.queryIntentActivities(
                mainIntent, 0);
        String[] names = new String[resolveInfos.size()];
        for (int i = 0; i < resolveInfos.size(); i++) {
            names[i] = resolveInfos.get(i).loadLabel(pm).toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (flag) {
                    case APP_HIDE:
                        hideApp(resolveInfos.get(which).activityInfo.packageName);
                        break;
                    case APP_RESTRICTIONS:
                        setAppRestrictions(resolveInfos.get(which).activityInfo.packageName);
                        break;
                }
            }
        }).create().show();
    }

    private void hideApp(String packageName) {
        // TODO: 2016/6/17 未测试
        Logger.d("应用包名" + packageName);
//        boolean isHidden = dpm.isApplicationHidden(mComponentName,packageName);
//        dpm.setApplicationHidden(mComponentName,packageName,!isHidden);
//        if(isHidden){
//            ToastUtil.show("已隐藏");
//        }else{
//            ToastUtil.show("已显示");
//        }
    }

    private void setAppRestrictions(String packageName) {
        // TODO: 2016/6/17 未测试
//        dpm.setApplicationRestrictions(mComponentName,packageName,);
    }

    private void setDeviceGlobalSetting() {
        // TODO: 2016/6/17 未测试
//        dpm.setGlobalSetting(mComponentName, Settings.Global.DATA_ROAMING,"");
    }

    private void setDeviceKeyGuardDisabled() {
        // TODO: 2016/6/17 未测试
//        dpm.setKeyguardDisabled(mComponentName,true);
    }

    private void setDeviceKeyGuardDisabledFeatures() {
        // TODO: 2016/6/17 未测试
//        dpm.setKeyguardDisabledFeatures(mComponentName, DevicePolicyManager.KEYGUARD_DISABLE_WIDGETS_ALL);
    }

    /**
     * 锁定屏幕
     */
    private void setLockTask() {
        // TODO: 2016/6/17 未测试
//        dpm.setLockTaskPackages(mComponentName,);
    }

    /**
     * 静音
     */
    private void setVolumeMuted() {
        // TODO: 2016/6/17 未测试
//        dpm.setMasterVolumeMuted(mComponentName,true);
    }

    /**
     * 应用权限
     */
    private void setPackagePermission(String packageName) {
        // TODO: 2016/6/17 未测试
//        dpm.setPermissionGrantState(mComponentName,packageName, Manifest.permission.INTERNET,DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
    }

    /**
     * 动态权限
     */
    private void setDevicePermissionPolicy() {
        // TODO: 2016/6/17 未测试
//        dpm.setPermissionPolicy(mComponentName,DevicePolicyManager.PERMISSION_POLICY_PROMPT);
    }

    /**
     * 辅助功能
     */
    private void setDevicePermittedAccessibilityService() {
        // TODO: 2016/6/17 未测试
//        dpm.setPermittedAccessibilityServices(mComponentName,);
    }

    /**
     * 输入法
     */
    private void setDevicePermittedInputMethod() {
        // TODO: 2016/6/17 未测试
//        dpm.setPermittedInputMethods(mComponentName,)
    }

    private void setDeviceScrennCaptureDisabled() {
        // TODO: 2016/6/17 未测试
//        dpm.setScreenCaptureDisabled(mComponentName,true);
    }

    private void setDeviceSecureSetting(){
        // TODO: 2016/6/17 未测试
//        dpm.setSecureSetting(mComponentName, Settings.Secure.INSTALL_NON_MARKET_APPS,);
    }

    private void disableStatusBar(){
        // TODO: 2016/6/17 未测试
//        dpm.setStatusBarDisabled(mComponentName,true);
    }

    private void blockedUninstall(String packageName){
        // TODO: 2016/6/17 未测试
//        dpm.setUninstallBlocked(mComponentName,packageName,true);
    }

    private void setDeviceUserIcon(){
        // TODO: 2016/6/17 未测试
//        dpm.setUserIcon(mComponentName,);
    }

    private void setSwitchUser(){
        // TODO: 2016/6/17 未测试
//        dpm.switchUser(mComponentName,);
    }
}
