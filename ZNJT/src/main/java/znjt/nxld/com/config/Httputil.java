package znjt.nxld.com.config;


import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import znjt.nxld.com.listener.BackHander;
import znjt.nxld.com.ui.activity.IdcaidPhoto_Activity;

/**
 * Created by zhengzhihua on 2018/8/31.11:35
 * Update 2018/8/31 11:35
 * Describe
 */

public class Httputil {
    private static final String TAG="Httputil";
//    private static  String url="";

    /**
     * 通过get请求接口
     * @param urls
     * @param prammap
     * @param hander
     */
    public static void Okhttpget(String urls, Map prammap, final BackHander hander){
        OkHttpClient client = new OkHttpClient();
        if(prammap != null){
            urls = urls+"?";
            for (Object key: prammap.keySet()){
                urls = urls + key+"="+prammap.get(key)+"&";
            }
            urls = urls.substring(0,urls.length()-1);
        }
        Request request = new Request.Builder()
                .url(urls)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hander.onexception(e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
                    String body=response.body().string();
                    Log.i(TAG, "onResponse: "+body);
                    hander.onsuccess(body);
                }else{
                    hander.onFail(response.body().string());
                }
            }
        });

    }

    public static void Okhttppost(String urls, String  strjson, final BackHander hander){

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        RequestBody body = RequestBody.create(JSON, strjson);
        Request request = new Request.Builder()
                .url(urls)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                hander.onexception(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
             if(response.isSuccessful()){
                 String body=response.body().string();
                 Log.i(TAG, "onResponse: "+body);
                 hander.onsuccess(body);
             }else{
                 hander.onFail(response.body().string());
             }
            }
        });//此处省略回调方法。
    }


}
