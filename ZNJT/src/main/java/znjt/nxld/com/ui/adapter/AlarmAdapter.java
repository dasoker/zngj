package znjt.nxld.com.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import znjt.nxld.com.R;
import znjt.nxld.com.bean.AlarmItem;

/**
*@Author:dingkuirong
*@date:2018/9/7 16:40
*@Description:警报类型的适配器
*/

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private List<AlarmItem> mAlarmList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView type;
        TextView startTime;
        TextView carNum;
        TextView speed;
        View alarmView;
        public ViewHolder(View itemView) {
            super(itemView);
            alarmView=itemView;
            type = alarmView.findViewById(R.id.alarm_item_Type);
            startTime = alarmView.findViewById(R.id.alarm_item_start_time);
            carNum = alarmView.findViewById(R.id.alarm_item_carNum);
            speed = alarmView.findViewById(R.id.alarm_item_speed);
        }
    }
    private OnItemClickListener mOnItemClickListener = null;
    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public AlarmAdapter(List<AlarmItem> alarmItemList){
        mAlarmList= alarmItemList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.alarmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    //注意这里使用getTag方法获取position
                    mOnItemClickListener.onItemClick(view,(int)view.getTag());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        AlarmItem alarmItem =mAlarmList.get(position);
        holder.type.setText(alarmItem.getType());
        holder.startTime.setText(alarmItem.getStartTime());
        holder.carNum.setText(alarmItem.getCarNum());
        holder.speed.setText(alarmItem.getSpeed());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mAlarmList.size();
    }
}
