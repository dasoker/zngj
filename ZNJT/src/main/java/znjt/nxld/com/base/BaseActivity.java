package znjt.nxld.com.base;

import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by zhengzhihua on 2018/9/14.10:11
 * Update 2018/9/14 10:11
 * Describe
 */

public class BaseActivity extends AppCompatActivity{
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
          //  finish();
            System.exit(0);
        }
    }
}
