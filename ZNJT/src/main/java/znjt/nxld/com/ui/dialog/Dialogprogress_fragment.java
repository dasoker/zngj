package znjt.nxld.com.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import znjt.nxld.com.R;


/**
 * Created by Lenovo on 2018/8/10.
 */

public class Dialogprogress_fragment extends DialogFragment {

    public static Dialogprogress_fragment newInstance(){
        Dialogprogress_fragment csdf = new Dialogprogress_fragment();
        Bundle bundle = new Bundle();
        csdf.setArguments(bundle);
        return csdf;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog=new Dialog(getActivity(), R.style.ProgressDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.f_dialogprogress_layout);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams l=window.getAttributes();
        l.gravity = Gravity.CENTER; // 紧贴底部
        window.setAttributes(l);
        return dialog;
    }
}
