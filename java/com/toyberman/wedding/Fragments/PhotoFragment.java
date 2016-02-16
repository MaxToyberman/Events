package com.toyberman.wedding.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.toyberman.wedding.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment {


    public interface OnPhotoTabFilledListener {
        void titleFilled(String title);

        void descriptionFilled(String description);

        void imageChosen(String encodedImage);
    }

    private static final int CHOOSER_CODE = 100;
    private OnPhotoTabFilledListener mCallback;
    private Bitmap bitmap;
    private String encodedImage;

    @Bind(R.id.et_title)
    EditText et_title;
    @Bind(R.id.et_description)
    EditText et_description;
    @Bind(R.id.iv_eventImage)
    ImageView iv_eventImage;

    public PhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //attaching to newEventFragment
        Fragment fragment = getParentFragment();
        onAttachFragment(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.iv_eventImage)
    public void chooseEventImage() {

        //get image from gallery intent
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        //chooser intent
        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
        getParentFragment().startActivityForResult(chooser, CHOOSER_CODE);

    }

    public void onAttachFragment(Fragment fragment) {
        try {
            mCallback = (OnPhotoTabFilledListener) fragment;

        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString() + " must implement OnPlayerSelectionSetListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CHOOSER_CODE && resultCode == Activity.RESULT_OK) {

            if (data.getData() != null) {
                final Uri image_uri = data.getData();
                final Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        iv_eventImage.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                iv_eventImage.setTag(target);

                Picasso.with(getActivity())
                        .load(image_uri)
                        .transform(transformation)
                        .into(target);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InputStream image_stream = null;
                        try {
                            image_stream = getActivity().getContentResolver().openInputStream(image_uri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        bitmap = BitmapFactory.decodeStream(image_stream);

                        //Convert bitmap to byte array
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        encodedImage = Base64.encodeToString(bitmapdata, Base64.DEFAULT);

                        mCallback.imageChosen(encodedImage);
                    }
                }).start();

            }

        }
    }

    Transformation transformation = new Transformation() {

        @Override
        public Bitmap transform(Bitmap source) {
            int targetWidth = iv_eventImage.getWidth();

            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (targetWidth * aspectRatio);
            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                // Same bitmap is returned if sizes are the same
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "transformation" + " desiredWidth";
        }
    };

    public void setTextError(String name, String s) {


        if (name.equals("title")) {
            et_title.setError(s);
        }
        if (name.equals("description")) {
            et_description.setError(s);
        }

    }

    @OnTextChanged(value = R.id.et_title, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onTitleChanged(CharSequence text) {
        mCallback.titleFilled(text.toString());
    }

    @OnTextChanged(value = R.id.et_description, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onDescriptionChanged(CharSequence text) {
        mCallback.descriptionFilled(text.toString());
    }


}
