package znjt.nxld.com.util.imageUtil;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import znjt.nxld.com.R;

/**
 * Created by ThinkPad on 2018/9/14.
 */

public class GridListAdapter extends ArrayAdapter<FolderBean>{
    private LayoutInflater mInflater;
    private List<FolderBean> mData;

    public GridListAdapter(Context context, List<FolderBean> objects) {
        super(context, 0, objects);
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder= null;
        if (holder==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_view_list_item,parent,false);
            holder.mImage = convertView.findViewById(R.id.gridView_list_item_imageView_show);
            holder.mDirName = convertView.findViewById(R.id.gridView_list_item_name);
            holder.mDirCount = convertView.findViewById(R.id.gridView_list_item_count);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        FolderBean folderBean = getItem(position);
        holder.mImage.setImageResource(R.drawable.qraved_bg_default);
        GridViewImageLoader.getInstance().loadImage(folderBean.getFirstImaPath(),holder.mImage);//可以加载网络图片
        holder.mDirCount.setText(folderBean.getCount()+"张");
        holder.mDirName.setText(folderBean.getName());
        return convertView;
    }
    private class ViewHolder{
        ImageView mImage;
        TextView mDirName;
        TextView mDirCount;
    }
}
