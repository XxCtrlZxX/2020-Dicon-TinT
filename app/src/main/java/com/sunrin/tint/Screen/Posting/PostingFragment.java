package com.sunrin.tint.Screen.Posting;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.okdroid.checkablechipview.CheckableChipView;
import com.sunrin.tint.Filter;
import com.sunrin.tint.Model.PostModel;
import com.sunrin.tint.R;
import com.sunrin.tint.Util.CreateUtil;
import com.sunrin.tint.Util.FirebaseUploadPost;
import com.sunrin.tint.Util.ImagePickerUtil;
import com.sunrin.tint.Util.UserCache;
import com.sunrin.tint.View.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

public class PostingFragment extends Fragment {

    Context mContext;

    Button postBtn;
    EditText titleText, subtitleText, contentText;
    ImageView imgBtn;
    List<CheckableChipView> chipViews = new ArrayList<>();

    private List<Uri> selectedImages;
    private boolean isImageSelected;

    private ViewGroup selectedImageContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posting, container, false);

        init(view);

        postBtn.setOnClickListener(v -> UploadPost());
        imgBtn.setOnClickListener(v -> GetImages());
        titleText.addTextChangedListener(textWatcher);
        subtitleText.addTextChangedListener(textWatcher);
        contentText.addTextChangedListener(textWatcher);

        return view;
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkCanUpload();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void init(View view) {
        isImageSelected = false;

        postBtn = view.findViewById(R.id.postBtn);
        titleText = view.findViewById(R.id.titleText);
        subtitleText = view.findViewById(R.id.subtitleText);
        contentText = view.findViewById(R.id.contentText);
        imgBtn = view.findViewById(R.id.imgBtn);
        selectedImageContainer = view.findViewById(R.id.selected_image_container);
        chipViews.add(view.findViewById(R.id.chip1));
        chipViews.add(view.findViewById(R.id.chip2));
        chipViews.add(view.findViewById(R.id.chip3));
        chipViews.add(view.findViewById(R.id.chip4));
        chipViews.add(view.findViewById(R.id.chip5));
        for (CheckableChipView chip : chipViews)
            chip.setOnCheckedChangeListener((chipView, aBoolean) -> {
                chip.setCheckedColor(ContextCompat.getColor(mContext,
                        aBoolean ? R.color.pink_700 : R.color.gray));
                return null;
            });
    }

    private void UploadPost() {
        String title = titleText.getText().toString();
        String subTitle = subtitleText.getText().toString();
        String content = contentText.getText().toString();

        List<Filter> filters = getFilters();
        List<String> imageToString = new ArrayList<String>() {
            {
                for (Uri uri : selectedImages)
                    add(uri.toString());
            }
        };

        LoadingDialog dialog = new LoadingDialog(mContext);
        dialog.setMessage("포스트 업로드 중...").show();

        FirebaseUploadPost
                .Upload(mContext, new PostModel(filters, imageToString, title, subTitle, content),
                        (documentID) -> {
                            dialog.setMessage("업로드 완료")
                                    .setFinishListener(() -> PostDone(documentID))
                                    .finish(true);
                        },
                        errorMsg -> dialog.setMessage(errorMsg).finish(false));
    }

    private void GetImages() {
        CreateUtil.CreatePost(mContext, getActivity());
        /*ImagePickerUtil.PickImages(mContext, getActivity(), images -> {
            selectedImages = images;
            isImageSelected = !images.isEmpty();
            showUriList(images);
            checkCanUpload();
        });*/
    }

    private void showUriList(List<Uri> uriList) {
        // Remove all views before
        // adding the new ones.
        selectedImageContainer.removeAllViews();

        selectedImageContainer.setVisibility(View.VISIBLE);

        int widthPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
        int heightPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());


        for (Uri uri : uriList) {

            View imageHolder = LayoutInflater.from(mContext).inflate(R.layout.post_image_item, null);
            ImageView thumbnail = imageHolder.findViewById(R.id.media_image);

            Glide.with(thumbnail)
                    .load(uri.toString())
                    .apply(new RequestOptions().fitCenter())
                    .into(thumbnail);

            selectedImageContainer.addView(imageHolder);

            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(widthPixel, heightPixel));

        }
    }

    private void checkCanUpload() {
        boolean t = titleText.getText().toString().isEmpty();
        boolean s = subtitleText.getText().toString().isEmpty();
        postBtn.setEnabled(isImageSelected && !t && !s);
    }

    private List<Filter> getFilters() {
        return new ArrayList<Filter>() {
            {
                for (int i = 0; i < chipViews.size(); i++)
                    if (chipViews.get(i).isChecked())
                        add(Filter.values()[i]);
            }
        };
    }

    private void PostDone(String docId) {
        // 포스팅 한 후 유저 정보 업데이트
        UserCache.updateUser(mContext, docId, null, UserCache.UPDATE_POST,
                aVoid -> {},
                errMsg -> Toast.makeText(mContext, errMsg, Toast.LENGTH_SHORT).show());
        postBtn.setEnabled(false);
        titleText.setText("");
        subtitleText.setText("");
        contentText.setText("");
        showUriList(new ArrayList<>()); // 사진 초기화
        isImageSelected = false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
