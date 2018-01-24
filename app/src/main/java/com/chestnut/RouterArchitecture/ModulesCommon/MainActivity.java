package com.chestnut.RouterArchitecture.ModulesCommon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chestnut.RouterArchitecture.ModulesCommon.retrofit.AppListBean;
import com.chestnut.RouterArchitecture.ModulesCommon.retrofit.GetAppList;
import com.chestnut.common.ui.XToast;
import com.chestnut.common.utils.AppUtils;
import com.chestnut.common.utils.LogUtils;
import com.chestnut.common.utils.NetworkUtils;
import com.chestnut.common.utils.XFontUtils;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.subjects.PublishSubject;


public class MainActivity extends RxAppCompatActivity {

    private String TAG = "MainActivity";
    private boolean OpenLog = true;
    private XToast toast;
    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;
    TextView txtLog;
    SeekBar seekBar1;
    SeekBar seekBar2;
    List<String> logs;
    private PublishSubject<Integer> publishSubject;

    int btnIds[] = {
            R.id.btn_1,
            R.id.btn_2,
            R.id.btn_3,
            R.id.btn_4,
            R.id.btn_5,
            R.id.btn_6,
            R.id.btn_7,
            R.id.btn_8,
            R.id.btn_9,
            R.id.btn_10,
            R.id.btn_11,
            R.id.btn_12,
    };

    String toastAndBtnName[] = {
            "1_"+"PayTest",
            "2_"+"蓝牙",
            "3_"+"重启程序",
            "4_"+"OemHWLActivity",
            "5_"+"DemoInstallUninstallActivity",
            "6_"+"",
            "7_"+"",
            "8_"+"",
            "9_"+"",
            "10_"+"",
            "11_"+"",
            "12_"+"",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XFontUtils.getInstance().activitySetFont(this,"fonts/Test.TTF");
        logs = new ArrayList<>();

        img1 = (ImageView) findViewById(R.id.img_1);
        img2 = (ImageView) findViewById(R.id.img_2);
        img3 = (ImageView) findViewById(R.id.img_3);
        img4 = (ImageView) findViewById(R.id.img_4);

        seekBar1 = (SeekBar) findViewById(R.id.seekBar_1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar_2);
        toast = new XToast(this, Toast.LENGTH_LONG);
        toast.setTextTypeface(XFontUtils.getInstance().get("fonts/caonima.ttf"));
        toast.setTextSize(20);

        TextView textView = null;
        txtLog = (TextView) findViewById(R.id.txt_log);
        for (int i = 0; i < btnIds.length; i++) {
            textView = (TextView) findViewById(btnIds[i]);
            textView.setText(toastAndBtnName[i]);
            textView.setOnClickListener(onClickListener);
            textView.setTag(i);
        }

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                toast.setText("1:"+seekBar.getProgress()).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                toast.setText("1:"+seekBar.getProgress()).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                toast.setText("2:"+seekBar.getProgress()).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                toast.setText("2:"+seekBar.getProgress()).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        publishSubject = PublishSubject.create();
        publishSubject.throttleFirst(5, TimeUnit.SECONDS)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(integer -> {
                    LogUtils.i(OpenLog,TAG,String.valueOf(System.currentTimeMillis()));
                });
    }

    private void viewLog(String TAG,String msg) {
        if (logs.size()>=6)
            logs.remove(0);
        logs.add(TAG+"\t\t"+msg);
        StringBuilder result = new StringBuilder();
        for (String s :
                logs) {
            result.append(s).append("\n");
        }
        txtLog.setText(result.toString());
    }

    private String url = "http://windowserl.honeybot.cn:8080/Market/getAppList?pageIndex=0&pageSize=8";

    private View.OnClickListener onClickListener = view -> {
        toast.setText(toastAndBtnName[(int) view.getTag()]).show();
        LogUtils.i(OpenLog,TAG,"btn-info:"+toastAndBtnName[(int) view.getTag()]);
        viewLog(TAG,toastAndBtnName[(int) view.getTag()]);
        switch (view.getId()) {
            case R.id.btn_1:
                startActivity(new Intent(this,PayActivity.class));
                break;
            case R.id.btn_2:
                startActivity(new Intent(this,RecordPlayActivity.class));
                break;
            case R.id.btn_3:
                break;
            case R.id.btn_4:
                startActivity(new Intent(this,OemHWLActivity.class));
                break;
            case R.id.btn_5:
                startActivity(new Intent(this,DemoInstallUninstallActivity.class));
                break;
            case R.id.btn_6:
                break;
            case R.id.btn_7:
                break;
            case R.id.btn_8:
                break;
            case R.id.btn_9:
                break;
            case R.id.btn_10:
                break;
            case R.id.btn_11:
                break;
            case R.id.btn_12:
                //声明缓存地址和大小
                Cache cache = new Cache(this.getCacheDir(),10*1024*1024);
                //构建 Client
                //参考：http://blog.csdn.net/changsimeng/article/details/54668884
                OkHttpClient client = new OkHttpClient.Builder()
//                        .retryOnConnectionFailure(true)
                        //addInterceptor()添加的是应用拦截器，他只会在response被调用一次。
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
                                Log.i(TAG,"0");
                                if (!NetworkUtils.isConnected(MainActivity.this)) {
                                    Log.i(TAG,"1");
                                    /**
                                     * 离线缓存控制  总的缓存时间=在线缓存时间+设置离线缓存时间
                                     */
                                    int maxStale = 50; // 离线时缓存保存4周,单位:秒
                                    CacheControl tempCacheControl = new CacheControl.Builder()
                                            .onlyIfCached()
                                            .maxStale(maxStale, TimeUnit.SECONDS)
                                            .build();
                                    request = request.newBuilder()
                                            .cacheControl(tempCacheControl)
                                            .build();
                                }
                                return chain.proceed(request);
                            }
                        })
                        .addNetworkInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Log.i(TAG,"2");
                                Request request = chain.request();
                                Response originalResponse = chain.proceed(request);
                                int maxAge = 20;    // 在线缓存,单位:秒
                                return originalResponse.newBuilder()
                                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                                        .removeHeader("Cache-Control")
                                        .header("Cache-Control", "public, max-age=" + maxAge)
                                        .build();
                            }
                        })
                        //addNetworkInterceptor()添加的是网络拦截器，它会在request和response时分别被调用一次
                        .cache(cache)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .build();
                //构建 Retrofit
                Retrofit retrofit1 = new Retrofit.Builder()
                        .baseUrl("http://windowserl.honeybot.cn:8080/Market/") //设置网络请求的Url地址
                        .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                        .client(client)
                        .build();
                // 创建 网络请求接口 的实例
                GetAppList getAppList = retrofit1.create(GetAppList.class);
                Call<AppListBean> appListBeanCall1 = getAppList.get(1,8);
                appListBeanCall1.enqueue(new Callback<AppListBean>() {
                    @Override
                    public void onResponse(Call<AppListBean> call, retrofit2.Response<AppListBean> response) {
                        if (response!=null && response.isSuccessful()) {
                            if (response.body()!=null && response.body().data!=null)
                                for (AppListBean.DataBean d :
                                        response.body().data) {
                                    LogUtils.i(TAG,d.toString());
                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<AppListBean> call, Throwable t) {
                        LogUtils.i(TAG,t.getMessage());
                    }
                });
                break;
        }
    };

    @Override
    public void onBackPressed() {
        AppUtils.pressTwiceExitApp(this,false,2000, new AppUtils.ExitAppCallBack() {
            @Override
            public void firstAsk() {
                LogUtils.i(OpenLog,TAG,"firstAsk");
                toast.setText("再按一次就退出啦~").show();
            }

            @Override
            public void beginExit() {
                LogUtils.i(OpenLog,TAG,"beginExit");
            }
        });
    }
}
