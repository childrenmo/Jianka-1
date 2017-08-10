package tech.jianka.data;

import java.io.File;

/**
 * Created by Richard on 2017/7/30.
 */

public class Item {

    private int itemType;
    private String fileName;
    private String filePath;
    private long modifiedTime;

    public Item() {

    }

    public Item(String fileName, String filePath, long lastModified) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.modifiedTime = lastModified;
    }

    public Item(String fileName, String filePath, int itemType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.itemType = itemType;
    }

    public Item(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getItemType() {
        return itemType;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public boolean remove(Item item) {
        return new File(item.getFilePath()).delete();
    }

    public boolean remove() {
        return new File(filePath).delete();
    }
}

