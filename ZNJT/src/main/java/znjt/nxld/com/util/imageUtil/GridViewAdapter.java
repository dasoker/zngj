package znjt.nxld.com.util.imageUtil;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import znjt.nxld.com.R;

/**
*@Author:dingkuirong
*@date:2018/9/14 9:13
*@Description:适配器
*/

public class GridViewAdapter extends BaseAdapter {
    private String mDirPath;
    private List<String> mImagePath;
    private LayoutInflater mInflater;
    private String mSelectImage;
    private int mScreeWidth;
    public GridViewAdapter(Context context, List<String> mData,String dirPath){
        this.mDirPath = dirPath;
        this.mImagePath = mData;
        mInflater = LayoutInflater.from(context);
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreeWidth= outMetrics.widthPixels;
    }

    @Override
    public int getCount() {
        return mImagePath.size()+1;
    }

    @Override
    public Object getItem(int i) {
        return mImagePath.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder =null;
        if (view == null){
            view = mInflater.inflate(R.layout.grid_view_item,viewGroup,false);
            viewHolder =  new ViewHolder();
            viewHolder.imageView = view.findViewById(R.id.gridView_item_image);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageView.setImageResource(R.drawable.qraved_bg_default);
        viewHolder.imageView.setMaxWidth(mScreeWidth/4);
        if (i==0){
            viewHolder.imageView.setImageResource(R.drawable.cameraadd);
        }else {
            GridViewImageLoader.getInstance(3, GridViewImageLoader.Type.LIFO).
                    loadImage(mDirPath+"/"+mImagePath.get(i-1),viewHolder.imageView);
        }
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i>0){
                    mSelectImage = mDirPath+"/"+mImagePath.get(i-1);
                }else {
                    mSelectImage = "";
                }
                if (mListener !=null){
                    mListener.onSelect(mSelectImage);
                }
            }
        });
        return view;
    }
    private class ViewHolder{
        ImageView imageView;
    }
    public interface OnGridViewListItemSelectListener{
        void onSelect(String path);
    }

    public void setOnGridViewListItemSelectListener(OnGridViewListItemSelectListener mListener) {
        this.mListener = mListener;
    }

    private OnGridViewListItemSelectListener mListener;
}
