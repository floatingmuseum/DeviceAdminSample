package floatingmuseum.devicemanagersample.util;

import android.widget.Toast;

import floatingmuseum.devicemanagersample.App;

/**
 * 
 */
public class ToastUtil {
    private static Toast mToast = null;

    private ToastUtil(){}

    /**
     * 传字符串
     * @param content
     */
    public static void show(String content) {
    	if(mToast==null){
    		mToast.makeText(App.context,content, Toast.LENGTH_SHORT).show();
    	}else{
    		mToast.setText(content);
    		mToast.setDuration(Toast.LENGTH_LONG);
    		mToast.show();
    	}
    }

    /**
     * 传String资源id
     * @param resId
     */
    public static void show(int resId){
        show(App.context.getText(resId).toString());
    }
}
