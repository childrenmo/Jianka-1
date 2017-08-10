package tech.jianka.data;

import static tech.jianka.utils.ItemUtils.getSDCardPath;

/**
 * Created by Richa on 2017/8/8.
 */

public class Group extends Item {
    private String coverPath;
    public Group(String fileName, String filePath,long lastModified) {
        super(fileName, filePath, lastModified);
        super.setItemType(DataType.GROUP);
        this.coverPath = getSDCardPath("jianka/images/") + fileName;
    }
    public Group(String fileName, String filePath) {
        super(fileName, filePath);
        super.setItemType(DataType.GROUP);
        this.coverPath = getSDCardPath("jianka/images/"+fileName);
    }

    public String getCoverPath() {
        return coverPath;
    }
    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }
}
