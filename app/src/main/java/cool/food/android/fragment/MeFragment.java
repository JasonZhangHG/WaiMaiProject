package cool.food.android.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cool.food.android.R;
import cool.food.android.activity.ChangePwdActivity;
import cool.food.android.activity.MyOrderActivity;
import cool.food.android.bean.CurrentUser;
import cool.food.android.dialog.LogoutDialog;
import cool.food.android.utils.CurrentUserHelper;

//我的
public class MeFragment extends Fragment implements View.OnTouchListener {

    @BindView(R.id.tv_logout) TextView mLogout;
    @BindView(R.id.tv_change_pwd) TextView mChangePwd;
    @BindView(R.id.tv_title) TextView mUserName;
    @BindView(R.id.tv_my_order) TextView mMyWeiBo;

    private LogoutDialog mLogoutDialog;
    private CurrentUser mCurrentUser;
    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(this);

    }

    public void initInfo() {
        mCurrentUser = CurrentUserHelper.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            mUserName.setText("用户：" + mCurrentUser.getUsername());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initInfo();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.tv_change_pwd)
    public void changePwdClicked() {
        startActivity(new Intent(getActivity(), ChangePwdActivity.class));
    }

    @OnClick(R.id.tv_my_order)
    public void myOrderClicked() {
        startActivity(new Intent(getActivity(), MyOrderActivity.class));
    }

    @OnClick(R.id.tv_logout)
    public void logoutClicked() {
        if (mLogoutDialog == null) {
            mLogoutDialog = new LogoutDialog();
        }
        mLogoutDialog.tryShow(getChildFragmentManager());
    }
}
