package znjt.nxld.com.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by zhengzhihua on 2018/9/14.13:43
 * Update 2018/9/14 13:43
 * Describe
 */

public class EditChangedListener implements TextWatcher {

    private EditText et;

    public EditChangedListener(EditText et){
        this.et = et;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //当输入为小写字母时，自动转换为大写字母

        et.removeTextChangedListener(this);//解除文字改变事件
        et.setText(s.toString().toUpperCase());//转换
        et.setSelection(s.toString().length());//重新设置光标位置
        et.addTextChangedListener(this);//重新绑
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

};
