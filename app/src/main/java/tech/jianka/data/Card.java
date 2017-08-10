package tech.jianka.data;

import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by Richa on 2017/8/8.
 */

public class Card extends Item implements Serializable{
    private static final long serialVersionUID = -5536293718112336569L;
    private int cardType;
    private String cardTitle;
    // TODO: 2017/7/31 content的保存方式
    private String cardContent;

    public Card(String cardTitle,String filePath ,String cardContent) {
        super.setItemType(DataType.CARD);
        super.setFilePath(filePath);
        this.cardTitle = cardTitle;
        this.cardContent = cardContent;
    }

    public Card() {

    }
    public Card(String cardTitle, String filePath, @Nullable String cardContent, int cardType) {
        super.setFilePath(filePath);
        this.cardTitle = cardTitle;
        this.cardContent = cardContent;
        this.cardType = cardType;
    }

    /**
     * 为列表生成的构造函数
     * @param fileName
     * @param filePath
     * @param itemType
     * @param cardType
     */
    public Card(String fileName, String filePath, int itemType, int cardType) {
        super(fileName,filePath,itemType);
        this.cardType = cardType;
    }




    public String getCardContent() {
        return cardContent;
    }

    public void setCardContent(String cardContent) {
        this.cardContent = cardContent;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }


    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

}
