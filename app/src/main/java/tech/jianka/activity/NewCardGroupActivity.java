package tech.jianka.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import tech.jianka.data.Group;
import tech.jianka.data.GroupData;
import tech.jianka.fragment.FragmentManager;
import tech.jianka.fragment.GroupFragment;

import static tech.jianka.utils.ItemUtils.getSDCardPath;
import static tech.jianka.utils.ItemUtils.saveBitmapToSDCard;

public class NewCardGroupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_OK = 133;
    private static final int CUT_OK = 52;
    private EditText mEditGroupTitle;
    private Button mBtnSave;
    private ImageView mImageGroupCover;
    private Bitmap mCover;
    private boolean isRename = false;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card_group);

        setTitle(R.string.action_new_card_list);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mEditGroupTitle = (EditText) findViewById(R.id.new_card_group_title);
        mBtnSave = (Button) findViewById(R.id.new_card_group_save);
        mBtnSave.setOnClickListener(this);
        mImageGroupCover = (ImageView) findViewById(R.id.new_card_group_cover);
        mImageGroupCover.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent.hasExtra("RENAME_GROUP")) {
            isRename = true;
            index = intent.getIntExtra("RENAME_GROUP", 999);
            Group group = GroupData.getGroup().get(index);
            mEditGroupTitle.setText(group.getFileName());
            if (new File(group.getCoverPath()).exists()) {
                mImageGroupCover.setImageBitmap(BitmapFactory.decodeFile(group.getCoverPath()));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_card_group_save:
                String title = mEditGroupTitle.getText().toString().trim();
                if (!title.equals("")) {
                    if (isRename) {
                        FragmentManager.getGroupFragment().adapter.renameGroup(index, title);
                        NavUtils.navigateUpFromSameTask(this);
                    } else {
                        String path = getSDCardPath("jianka/data/" + title);
                        if (new File(path).exists()) {
                            Toast.makeText(this, "卡组已存在", Toast.LENGTH_LONG);
                            GroupFragment fragment =
                                    FragmentManager.getGroupFragment();
                            fragment.adapter.addItem(new Group(title, path));
                            NavUtils.navigateUpFromSameTask(this);
                        }
                    }
                    if (mCover != null) {
                        saveBitmapToSDCard(mCover, "jianka/images", title);
                    }
                }
                break;
            case R.id.new_card_group_cover:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PICK_OK);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_OK) {
            if (data != null) {
                clipPhoto(data.getData());
            }
        } else if (requestCode == CUT_OK) {
            setPicToView(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void clipPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop = true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CUT_OK);
    }

    /**
     * 保存裁剪之后的图片数据 将图片设置到imageView中
     *
     * @param picData
     */
    private void setPicToView(Intent picData) {
        Bundle extras = picData.getExtras();
        if (extras != null) {
            mCover = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(mCover);
            mImageGroupCover.setImageDrawable(drawable);
            TextView textView = (TextView) findViewById(R.id.new_card_group_click_to_choose_cover);
            textView.setText("");
        }
    }

}
