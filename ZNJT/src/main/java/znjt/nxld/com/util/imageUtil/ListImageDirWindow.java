package znjt.nxld.com.util.imageUtil;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.List;

import znjt.nxld.com.R;

/**
 * Created by ThinkPad on 2018/9/14.
 */

public class ListImageDirWindow extends PopupWindow{
    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private ListView mListView;
    private List<FolderBean> mData;
    public interface OnDirSelectListener{
        void onSelect(FolderBean folderBean);
    }

    public void setOnDirSelectListener(OnDirSelectListener mListener) {
        this.mListener = mListener;
    }

    private OnDirSelectListener mListener;

    public ListImageDirWindow(Context context,List<FolderBean> data){
        calWidthAndHeight(context);
        mConvertView = LayoutInflater.from(context).
                inflate(R.layout.grid_view_list_window,null);
        mData = data;
        setContentView(mConvertView);
        setWidth(mWidth);
        setHeight(mHeight);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE){
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        initView(context);
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mListener !=null){
                    mListener.onSelect(mData.get(i));
                }
            }
        });
    }

    private void initView(Context context) {
        mListView = mConvertView.findViewById(R.id.gridView_album_list);
        mListView.setAdapter(new GridListAdapter(context,mData));
    }

    /**
     * 计算PopupWindow的宽度和高度
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidth =outMetrics.widthPixels;
        mHeight = (int) (outMetrics.heightPixels*0.7);
    }
}
