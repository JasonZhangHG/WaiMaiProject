package cool.food.android.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cool.food.android.R;
import cool.food.android.adapter.CommentAdapter;
import cool.food.android.base.BaseFragmentDialog;
import cool.food.android.bean.CommentBean;
import cool.food.android.bean.CurrentUser;
import cool.food.android.bean.RestaurantBean;
import cool.food.android.utils.CurrentUserHelper;
import cool.food.android.utils.ToastHelper;


public class CommentDialog extends BaseFragmentDialog {

    @BindView(R.id.rv_comment) RecyclerView mRecyclerView;
    @BindView(R.id.edt_chat_activity) EditText edtChatActivity;
    @BindView(R.id.rl_comment_dialog) RelativeLayout mCommentDialog;
    @BindView(R.id.btn_send_comment) Button mSendComment;
    private List<CommentBean> mCommentBeanList = new ArrayList<>();
    private RestaurantBean mRestaurantBean;
    private CommentAdapter mCommentAdapter;
    Unbinder unbinder;

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_comment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCommentList();
    }

    public void setData(RestaurantBean restaurantBean) {
        if (restaurantBean == null) {tryHide();}
        this.mRestaurantBean = restaurantBean;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.rl_comment_dialog)
    public void onHidleClicked(View view) {
        tryHide();
    }

    @OnClick(R.id.btn_send_comment)
    public void onSendCommentClicked(View view) {
        String commentValue = edtChatActivity.getText().toString();
        if (TextUtils.isEmpty(commentValue)) {
            ToastHelper.showShortMessage("评论不能为空");
        } else {
            CommentBean commentBean = new CommentBean();
            commentBean.setValue(commentValue);
            commentBean.setRestaurantId(mRestaurantBean.getObjectId());
            CurrentUser currentUser = CurrentUserHelper.getInstance().getCurrentUser();
            if (currentUser != null) {
                commentBean.setSenderUserName(currentUser.getUsername());
            }
            mCommentBeanList.add(commentBean);
            initRecyclerView(mCommentBeanList);
            commentBean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        ToastHelper.showShortMessage("评论成功");
                    } else {
                        ToastHelper.showShortMessage("评论失败");
                    }
                }
            });
        }
    }

    public void getCommentList() {
        BmobQuery<CommentBean> query = new BmobQuery<>();
        query.addWhereEqualTo("restaurantId", mRestaurantBean.getObjectId());
        query.setLimit(50).order("createdAt")
                .findObjects(new FindListener<CommentBean>() {
                    @Override
                    public void done(List<CommentBean> commentBeanList, BmobException e) {
                        if (e == null) {
                            initRecyclerView(commentBeanList);
                            mCommentBeanList = commentBeanList;
                        } else {
                            LogUtils.d("OrderFoodFragment BmobQuery failed : " + e);
                        }
                    }
                });
    }

    public void initRecyclerView(List<CommentBean> commentBeanList) {
        if (mCommentAdapter == null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mCommentAdapter = new CommentAdapter();
            mCommentAdapter.setDataSilently(commentBeanList);
            mRecyclerView.setAdapter(mCommentAdapter);
        } else {
            mCommentAdapter.setData(mCommentBeanList);
        }
    }
}
