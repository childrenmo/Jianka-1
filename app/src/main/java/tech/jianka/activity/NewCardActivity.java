package tech.jianka.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import tech.jianka.adapter.MyAdapter;
import tech.jianka.data.Card;
import tech.jianka.data.DataType;
import tech.jianka.data.GroupData;
import tech.jianka.data.RecentData;
import tech.jianka.data.TaskData;
import tech.jianka.fragment.FragmentManager;

import static tech.jianka.utils.ItemUtils.getSDCardPath;

public class NewCardActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    private EditText mEditTitle;
    private Spinner mGroupSelector;
    private EditText mEditContent;
    private TextView mIndicator;
    private ImageView iv_image;
    private String[] mIndicatorText;
    private int cardType = DataType.CARD;
    private boolean isDetail = false;
    private int cardIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card);

        this.iv_image = (ImageView) findViewById(R.id.iv_image);

        setTitle(getString(R.string.new_card_activity));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mIndicatorText = getResources().getStringArray(R.array.task);

        mIndicator = (TextView) findViewById(R.id.new_card_task_indicator);
        mEditTitle = (EditText) findViewById(R.id.new_card_title);
        mEditContent = (EditText) findViewById(R.id.new_card_content);
        RadioGroup mTaskSelector = (RadioGroup) findViewById(R.id.new_card_task_selector);
        mGroupSelector = (Spinner) findViewById(R.id.new_card_group_selector);
        mGroupSelector.setOnItemSelectedListener(this);

        mEditContent = (EditText) findViewById(R.id.new_card_content);
        mTaskSelector.setOnCheckedChangeListener(this);


        ArrayList<String> groups = (ArrayList<String>) GroupData.getGroupTitles();
        BaseAdapter myAdapter = new MyAdapter<String>(groups, R.layout.spinner_item) {
            @Override
            public void bindView(ViewHolder holder, String obj) {
                holder.setText(R.id.spinner_item_text_view, obj);
            }
        };
        mGroupSelector.setAdapter(myAdapter);

        Intent intent = getIntent();
        if (intent.hasExtra("CARD_DETAILS")){
            isDetail = true;
            cardIndex = intent.getIntExtra("CARD_DETAILS",999);
            Card card = RecentData.getCard(cardIndex);
            loadCard(card);
        } else if (intent.hasExtra("TASK_DETAILS")) {
            isDetail = true;
            cardIndex = intent.getIntExtra("TASK_DETAILS", 999);
            Card card = TaskData.getTask(cardIndex);
            loadTask(card);
        }
    }

    private void loadTask(Card card) {
        mEditTitle.setText(card.getCardTitle());
        mEditContent.setText(card.getCardContent());
    }

    private void loadCard(Card card) {
        mEditTitle.setText(card.getCardTitle());
        mEditContent.setText(card.getCardContent());
        // TODO: 2017/8/9 分组问题还没解决
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            // 得到图片的全路径
            Uri uri = data.getData();
            // 通过路径加载图片
            //这里省去了图片缩放操作，如果图片过大，可能会导致内存泄漏
            //图片缩放的实现，请看：http://blog.csdn.net/reality_jie_blog/article/details/16891095
            this.iv_image.setImageURI(uri);
            // 获取图片的缩略图，可能为空！
            // Bitmap bitmap = data.getParcelableExtra("data");
            // this.iv_image.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //2017/8/4

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_save:
                saveCard();
                finish();
                return true;
            case R.id.action_share:
                if (isEmpty()) {
                    shareDialog();
                }
                return true;
            case R.id.action_insert_image:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //li2
    private void shareDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_menu_share)
                .setTitle("发表成功，立即分享？")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                        intent.putExtra(Intent.EXTRA_TEXT, mEditTitle.getText() + "\n" + mEditContent.getText());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, getTitle()));

                        mEditContent.setText("");
                    }
                }).setNegativeButton("以后", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mEditContent.setText("");
            }
        }).show();
    }

    private boolean isEmpty() {
        if (mEditContent.getText() == null
                || mEditContent.getText().toString().trim().equals("")) {
            Toast toast = Toast.makeText(NewCardActivity.this,
                    this.getText(R.string.content_null), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
        return true;
    }

    private void saveCard() {
        String title = mEditTitle.getText().toString();
        String content = mEditContent.getText().toString();
        String filePath;
        if (cardType == DataType.CARD) {
            filePath = getSDCardPath("jianka/data/" + mGroupSelector.getSelectedItem().toString() + File.separator);
            Card card = new Card(title, filePath, content);
            FragmentManager.getRecentFragment().adapter.addItem(card);
        } else {
            filePath = getSDCardPath("jianka/card/" + mIndicator.getText().toString());
            Card card = new Card(title, filePath, content, cardType);
            FragmentManager.getTaskFragment().mAdapter.addItem(card);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (group.getId() == R.id.new_card_task_selector) {
            switch (checkedId) {
                case R.id.task_regular:
                    mIndicator.setText(mIndicatorText[0]);
                    cardType = DataType.CARD;
                    break;
                case R.id.task_important_emergent:
                    // TODO: 2017/8/6 bug 两个选择不能联动
                    mIndicator.setText(mIndicatorText[1]);
                    cardType = DataType.TASK_IMPORTANT_EMERGENT;
                    break;
                case R.id.task_important_not_emergent:
                    mIndicator.setText(mIndicatorText[2]);
                    cardType = DataType.TASK_IMPORTANT_NOT_EMERGENT;
                    break;
                case R.id.task_unimportant_emergent:
                    mIndicator.setText(mIndicatorText[3]);
                    cardType = DataType.TASK_UNIMPORTANT_EMERGENT;
                    break;
                case R.id.task_unimportant_not_emergent:
                    mIndicator.setText(mIndicatorText[4]);
                    cardType = DataType.TASK_UNIMPORTANT_NOT_EMERGENT;
                    break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.new_card_group_selector) {
            if (parent.getSelectedItemPosition() == 1) {
                if (cardType == DataType.CARD) {
                    cardType = DataType.TASK_IMPORTANT_EMERGENT;
                    RadioButton button = (RadioButton) findViewById(R.id.task_important_emergent);
                    button.setChecked(true);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
//    StringBuffer status = new StringBuffer();
//    //①获取系统的Configuration对象
//    Configuration cfg = getResources().getConfiguration();
//    RecyclerViewBanner recyclerViewBanner1 = (RecyclerViewBanner) findViewById(R.id.rv_banner_1);
//
//    //②想查什么查什么
//        status.append("densityDpi:" + cfg.densityDpi + "\n");
//        status.append("fontScale:" + cfg.fontScale + "\n");
//        status.append("hardKeyboardHidden:" + cfg.hardKeyboardHidden + "\n");
//        status.append("keyboard:" + cfg.keyboard + "\n");
//        status.append("keyboardHidden:" + cfg.keyboardHidden + "\n");
//        status.append("locale:" + cfg.locale + "\n");
//        status.append("mcc:" + cfg.mcc + "\n");
//        status.append("mnc:" + cfg.mnc + "\n");
//        status.append("navigation:" + cfg.navigation + "\n");
//        status.append("navigationHidden:" + cfg.navigationHidden + "\n");
//        status.append("orientation:" + cfg.orientation + "\n");
//        status.append("screenHeightDp:" + cfg.screenHeightDp + "\n");
//        status.append("screenWidthDp:" + cfg.screenWidthDp + "\n");
//        status.append("screenLayout:" + cfg.screenLayout + "\n");
//        status.append("smallestScreenWidthDp:" + cfg.densityDpi + "\n");
//        status.append("touchscreen:" + cfg.densityDpi + "\n");
//        status.append("uiMode:" + cfg.densityDpi + "\n");
//        txtResult.setText(status.toString());

}