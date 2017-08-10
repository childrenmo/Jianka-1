package tech.jianka.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static tech.jianka.utils.ItemUtils.Obj2Bytes;
import static tech.jianka.utils.ItemUtils.getAllSubCards;
import static tech.jianka.utils.ItemUtils.getSDCardPath;
import static tech.jianka.utils.ItemUtils.saveFileToSDCard;

/**
 * Created by Richa on 2017/8/3.
 */

public class RecentData {
    // TODO: 2017/8/3 读取最近修改的卡片数据
    private static List<Card> data = new ArrayList<>();

    public RecentData() {
        data = getAllSubCards(getSDCardPath("jianka/data"));
    }

    public List<Card> getData() {
        return data;
    }

    public static boolean removeCard(int index) {
        File file = new File(data.get(index).getFilePath());
        if(file.delete()) {
            data.remove(index);
            return true;
        }else return false;
    }

    public static void addCard(Card card) {
        saveFileToSDCard(Obj2Bytes(card), card.getFilePath(),card.getCardTitle()+".card");
        card.setFilePath(card.getFilePath()+ File.separator+card.getCardTitle()+".card");
        data.add(0, card);
    }

    public static void modifiedCard(int index,Card card) {
        removeCard(index);
        addCard(card);
    }

    public static Card getCard(int cardIndex) {
        return data.get(cardIndex);
    }
}
