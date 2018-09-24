package znjt.nxld.com.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import znjt.nxld.com.R;

/**
 * Created by zhengzhihua on 2018/9/11.17:46
 * Update 2018/9/11 17:46
 * Describe
 */

public class ThirdFragment extends Fragment {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private float latitude;
    private float longitude;
    private boolean isOk=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        if(bundle.getString("latitude")==null){
            isOk=true;
        }else{
            latitude= Float.parseFloat(getArguments().getString("latitude").trim());
            longitude=Float.parseFloat(getArguments().getString("longitude").trim());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3, null);
        LinearLayout linearLayout=view.findViewById(R.id.linearlayout_view);
        mMapView =view.findViewById(R.id.mmaps);
        if(isOk){
            linearLayout.setVisibility(View.VISIBLE);
            mMapView.setVisibility(View.GONE);
        }else{
            linearLayout.setVisibility(View.GONE);
            mMapView.setVisibility(View.VISIBLE);
            setMap();
        }

        return view;
    }

    protected void setMap(){
        mBaiduMap =mMapView.getMap();
        MapStatus mMapStatus = new MapStatus.Builder().zoom(15).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mBaiduMap.setMyLocationEnabled(true);

        MyLocationData locData = new MyLocationData.Builder()
                .latitude(latitude)//
                .longitude(longitude)//
                .build();
        // 设置定位数据

        mBaiduMap.setMyLocationData(locData);//给地图设置定位数据，这样地图就显示位置了
        LatLng ll=new LatLng(latitude,longitude);
        MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(update);
    }

}
