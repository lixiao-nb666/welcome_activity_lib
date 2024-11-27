package com.newbee.welcome_activity;//package com.newbee.build;

import static com.newbee.bulid_lib.mybase.ToastUtil.showToast;

import android.Manifest;

import android.view.View;
import android.widget.TextView;

import com.newbee.welcome_activity_lib.BaseWelcomeActivity;
import com.newbee.welcome_activity_lib.bean.WelcomeInfoBean;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends BaseWelcomeActivity {
    private TextView tv1,tv2,tv3,tv4;

    @Override
    public int getWelcomeLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initWelcomeView() {
        tv1=findViewById(R.id.tv_1);
        tv2=findViewById(R.id.tv_2);
        tv3=findViewById(R.id.tv_3);
        tv4=findViewById(R.id.tv_4);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivity(MainActivity.class);
            }
        });
    }

    @Override
    public void initWelcomeData() {

    }

    @Override
    public void initWelcomeControl() {

    }

    @Override
    public WelcomeInfoBean getWelcomeInfoBean() {
        WelcomeInfoBean welcomeInfoBean=new WelcomeInfoBean();
        List<String> permissions=new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        welcomeInfoBean.setPermissionList(permissions);
        welcomeInfoBean.setNeedFilePermission(true);
        return welcomeInfoBean;
    }

    @Override
    public void userNoPermission() {

    }

    @Override
    public void userGetAllPermission() {
        showToast("get all permission");


//        finish();
    }
}
