package com.newbee.welcome_activity_lib;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.newbee.bulid_lib.mybase.activity.BaseCompatActivity;
import com.newbee.welcome_activity_lib.bean.WelcomeInfoBean;
import com.newbee.welcome_activity_lib.dialog.WelcomeDialog;

import java.util.List;


public abstract class BaseWelcomeActivity extends BaseCompatActivity {

    public abstract int getWelcomeLayoutId();

    public abstract void initWelcomeView();

    public abstract void initWelcomeData();

    public abstract void initWelcomeControl();

    public abstract WelcomeInfoBean getWelcomeInfoBean();

    public abstract void userNoPermission();

    public abstract void userGetAllPermission();

    private boolean isCanSend = true;

    private void canSendUserGetAllPermission() {
        if (isCanSend) {
            isCanSend = false;
            userGetAllPermission();
        }
    }

    private WelcomeInfoBean welcomeInfoBean;

    @Override
    public int getViewLayoutRsId() {
        return getWelcomeLayoutId();
    }

    @Override
    public void initView() {
        initWelcomeView();
    }

    @Override
    public void initData() {
        welcomeInfoBean = getWelcomeInfoBean();
        initWelcomeData();
        if (null == welcomeInfoBean) {
            welcomeInfoBean = new WelcomeInfoBean();
        }
    }

    @Override
    public void initControl() {
        initWelcomeControl();
    }


    @Override
    public void closeActivity() {
    }

    @Override
    public void viewIsShow() {
        initPermissions();
    }

    @Override
    public void viewIsPause() {
    }

    @Override
    public void changeConfig() {
    }


    //权限处理
    // 要申请的权限
    private List<String> permissions;


    private void initPermissions() {
        if (welcomeInfoBean.isNeedFilePermission() && !fileManagerPermission()) {
            return;
        }
        initOtherPermissions();

    }

    private void initOtherPermissions() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initNeedPermissions();
            if (checkPermissions()) {
                canSendUserGetAllPermission();
            } else {
                showDialogTipUserRequestPermission();
            }
        } else {
            canSendUserGetAllPermission();
        }
    }


    private void initNeedPermissions() {
        permissions = welcomeInfoBean.getPermissionList();
    }

    //检测是否全部授权
    private boolean checkPermissions() {
        for (int i = 0; i < permissions.size(); i++) {
            if (!checkPermission(permissions.get(i))) {
//                LG.i(tag, "checkPermissions:false-" + permissions.get(i));
                return false;
            }
        }
        return true;
    }

    //检测单个权限是否授权
    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermission(String permission) {
        // 检查该权限是否已经获取
        int a = this.checkSelfPermission(permission);
//        LG.i(tag, "checkSelfPermission：" + a + "-" + permission);
        // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
        if (a == PackageManager.PERMISSION_GRANTED) return true;
        return false;
    }


    private WelcomeDialog welcomeDialog;
    private WelcomeDialog.Click welcomeDialogClick = new WelcomeDialog.Click() {


        @Override
        public void userCanToCheck() {
            startRequestPermission();
        }

        @Override
        public void userToSet() {
            goToAppSetting();
        }

        @Override
        public void userCancel() {
            userNoPermission();
        }
    };

    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {
        if (!isCanSend) {
            return;
        }

        if (null == welcomeDialog) {
            welcomeDialog = new WelcomeDialog(welcomeDialogClick);
        }
        welcomeDialog.show(this);
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        String[] strs = new String[permissions.size()];
        strs = permissions.toArray(strs);
        ActivityCompat.requestPermissions(this, strs, WelcomeActivityConfig.getPermissionsActivityType);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WelcomeActivityConfig.getPermissionsActivityType:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    boolean canStar = true;
                    for (int i = 0; i < grantResults.length; i++) {
                        canStar = (grantResults[i] == PackageManager.PERMISSION_GRANTED);
                    }
                    if (canStar) {
                        canSendUserGetAllPermission();
                    } else {
                        showDialogTipUserRequestPermission();
                    }
                }
                break;
        }


    }


    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, WelcomeActivityConfig.toSetType);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case WelcomeActivityConfig.toSetType:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 检查该权限是否已经获取
                    if (checkPermissions()) {
                        // 提示用户应该去应用设置界面手动开启权限
                        canSendUserGetAllPermission();
                    } else {
                        showDialogTipUserRequestPermission();
                    }
                }
                break;
            case WelcomeActivityConfig.togetAllFileManegerType:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // 判断有没有权限,没有就申请文件管理
                    if (!Environment.isExternalStorageManager()) {
                        showToast(getString(R.string.permission_no_file_exit));
                        finish();

                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 先判断有没有权限
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        showToast(getString(R.string.permission_no_file_exit));
                        finish();
                    }
                }
                break;
        }
    }

    public boolean fileManagerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 判断有没有权限,没有就申请文件管理
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, WelcomeActivityConfig.togetAllFileManegerType);
                showToast(getString(R.string.permission_no_file));
                return false;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, WelcomeActivityConfig.togetAllFileManegerType);
                showToast(getString(R.string.permission_no_file));
                return false;
            }
        }
        return true;
    }

}
