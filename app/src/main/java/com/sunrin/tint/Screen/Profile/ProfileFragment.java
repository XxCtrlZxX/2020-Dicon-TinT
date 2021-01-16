package com.sunrin.tint.Screen.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.sunrin.tint.Firebase.DownLoad.FirebaseLoadUserLB;
import com.sunrin.tint.Firebase.DownLoad.FirebaseLoadUserPost;
import com.sunrin.tint.Models.LookBookModel;
import com.sunrin.tint.Models.PostModel;
import com.sunrin.tint.Models.UserModel;
import com.sunrin.tint.R;
import com.sunrin.tint.Screen.MainActivity;
import com.sunrin.tint.Screen.ShowPostActivity;
import com.sunrin.tint.Screen.SplashActivity;
import com.sunrin.tint.Util.CreateUtil;
import com.sunrin.tint.Util.ImagePickerUtil;
import com.sunrin.tint.Util.UserCache;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class ProfileFragment extends Fragment {

    Context mContext;

    TextView tv_username, tv_status;
    ImageButton btn_addLookBook, btn_addPost, btn_storage, btn_logout;
    Button btn_filterMenu;
    CircleImageView profile;
    ViewGroup emptyView1, emptyView2;

    SwipeRefreshLayout swipeRefreshLayout;
    ShimmerRecyclerView lookBook_Recycler, post_Recycler;
    ProfileLookBookAdapter lookBookAdapter;
    ProfilePostAdapter postAdapter, postAdapter1;

    private List<LookBookModel> lookBookModelList;
    private List<PostModel> postModelList;

    private UserModel userModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);

        userModel = UserCache.getUser(mContext);

        assert userModel != null;
        tv_username.setText(userModel.getName());
        tv_status.setText(userModel.getStatus());
        Glide.with(profile)
                .load(userModel.getProfile())
                .placeholder(R.drawable.profile_empty_feed) // 사진이 로딩 되기 전 미리보기 이미지
                .error(R.drawable.profile_empty_feed)       // 사진 불러오지 못했을 때
                .into(profile);

        profile.setOnClickListener(v -> changeProfile());
        btn_storage.setOnClickListener(v -> Toast.makeText(mContext, "Storage", Toast.LENGTH_SHORT).show());
        btn_logout.setOnClickListener(v -> logout());
        btn_addLookBook.setOnClickListener(v -> CreateUtil.CreateLookBook(mContext, getActivity()));
        btn_addPost.setOnClickListener(v -> CreateUtil.CreatePost(mContext, getActivity()));

        btn_filterMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getActivity(), v);//v는 클릭된 뷰를 의미

            popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case R.id.popup_all:
                        break;
                    case R.id.popup_makeup:
                        break;
                    case R.id.popup_hair:
                        break;
                    case R.id.popup_fashion:
                        break;
                    case R.id.popup_nail:
                        break;
                    case R.id.popup_diet:
                        break;
                    default:
                        break;
                }
                return true;
            });
            popup.show();//Popup Menu 보이기
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //***** RecyclerView *****//
        // LookBook
        lookBook_Recycler.showShimmerAdapter();
        //LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        lookBook_Recycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        lookBookAdapter = new ProfileLookBookAdapter(emptyView1);
        //post_Recycler.setAdapter(lookBookAdapter);
        //getLookBookData();
        // TODO: Fix adapter or recyclerview
        postAdapter1 = new ProfilePostAdapter(emptyView1);
        post_Recycler.setAdapter(postAdapter1); // 임시로 테스트로 어댑터 바꿈

        lookBookAdapter.setOnItemClickListener((v, position) -> {
            Toast.makeText(mContext, "asdf", Toast.LENGTH_SHORT).show();
        });

        // Post
        post_Recycler.showShimmerAdapter();
        post_Recycler.setLayoutManager(new GridLayoutManager(mContext, 3));
        postAdapter = new ProfilePostAdapter(emptyView2);
        post_Recycler.setAdapter(postAdapter);
        getPostData();

        postAdapter.setOnItemClickListener((v, cover, position) -> {
            Intent intent = new Intent(mContext, ShowPostActivity.class);
            intent.putExtra("item", postAdapter.getList().get(position));
            startActivity(intent);
        });

        //**** RefreshLayout ****//
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getLookBookData();
            getPostData();
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.pink_700);
    }

    private void getLookBookData() {
        lookBook_Recycler.showShimmerAdapter();

        FirebaseLoadUserLB
                .LoadUserLookBooks(userModel.getLookBookID(),
                        lookBookModels -> {
                            if (lookBookAdapter != null) {
                                lookBook_Recycler.hideShimmerAdapter();
                                lookBookModelList = lookBookModels;
                                lookBookAdapter.setList(lookBookModels);
                                lookBookAdapter.notifyDataSetChanged();

                                swipeRefreshLayout.setRefreshing(false);    // 로딩 종료
                                Toast.makeText(mContext, "룩북 불러옴", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "getLookBookData: 룩북 불러옴");
                            }
                        },
                        errorMsg -> Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show());
    }

    private void getPostData() {
        post_Recycler.showShimmerAdapter();

        FirebaseLoadUserPost
                .LoadUserPosts(userModel.getPostID(),
                        postModels -> {
                            if (postAdapter != null) {
                                post_Recycler.hideShimmerAdapter();
                                postModelList = postModels;
                                postAdapter.setList(postModels);
                                postAdapter.notifyDataSetChanged();
                            }
                            // 임시
                            if (postAdapter1 != null) {
                                post_Recycler.hideShimmerAdapter();
                                postModelList = postModels;
                                postAdapter1.setList(postModels);
                                postAdapter1.notifyDataSetChanged();
                            }
                        },
                        errorMsg -> Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show());
    }

    private void changeProfile() {
        ImagePickerUtil.PickImage(mContext, getActivity(), "Profile", image -> {
            Toast.makeText(mContext, "프로필 변경은 개발중에 있습니다 :)", Toast.LENGTH_SHORT).show();
        });
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("로그아웃").setMessage("정말 로그아웃 하시겠습니까?");
        builder.setPositiveButton("로그아웃", (dialog, which) -> {
            UserCache.logout(mContext);
            Toast.makeText(mContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(mContext, SplashActivity.class));
            ((MainActivity) mContext).finish();
        });
        builder.setNegativeButton("취소", (dialog, which) -> {});
        builder.setCancelable(true).show();
    }

    private void init(View view) {
        tv_username = view.findViewById(R.id.tv_username);
        tv_status = view.findViewById(R.id.tv_status);
        btn_filterMenu = view.findViewById(R.id.popupmenu_btn);
        btn_storage = view.findViewById(R.id.btn_storage);
        btn_logout = view.findViewById(R.id.btn_setting);
        btn_addLookBook = view.findViewById(R.id.add_lookbook_btn);
        btn_addPost = view.findViewById(R.id.add_post_btn);
        profile = view.findViewById(R.id.profile_imageview);
        lookBook_Recycler = view.findViewById(R.id.lookbook_recycler);
        post_Recycler = view.findViewById(R.id.post_recycler);
        emptyView1 = view.findViewById(R.id.empty_view1);
        emptyView2 = view.findViewById(R.id.empty_view2);
        swipeRefreshLayout = view.findViewById(R.id.profile_refresh);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
