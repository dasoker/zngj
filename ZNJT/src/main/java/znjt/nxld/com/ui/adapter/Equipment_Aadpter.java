package znjt.nxld.com.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import znjt.nxld.com.R;
import znjt.nxld.com.bean.BindCar;
import znjt.nxld.com.ui.activity.EquipmentDetail_Activity;

/**
 * Created by zhengzhihua on 2018/9/12.23:34
 * Update 2018/9/12 23:34
 * Describe
 */

public class Equipment_Aadpter extends RecyclerView.Adapter<Equipment_Aadpter.ViewHolder> {
    private List<BindCar> list;
    private Context context;
   public Equipment_Aadpter(Context context,List<BindCar> list){
       this.context=context;
       this.list=list;                  //传入适配器器的数据
   }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.cardequipment_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }
/**
*  @Description 将数据与适配器中的控件绑定
*  @param:
*  @return
*/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        for(int i=0;i<list.size();i++){
            holder.carid.setText(list.get(position).getCarNo());
            holder.equipid.setText(list.get(position).getDeviceId());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,EquipmentDetail_Activity.class);
                    intent.putExtra("bindcar",list.get(position));
                    context.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
         private TextView carid;
         private TextView equipid;
         private CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            carid=itemView.findViewById(R.id.carids_text);
            equipid=itemView.findViewById(R.id.equips_text);
            cardView=itemView.findViewById(R.id.card_text);
        }
    }

}
