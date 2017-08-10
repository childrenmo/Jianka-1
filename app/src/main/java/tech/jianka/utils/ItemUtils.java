package tech.jianka.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tech.jianka.data.Card;
import tech.jianka.data.DataType;
import tech.jianka.data.Group;

/**
 * Created by Richa on 2017/7/31.
 */

public class ItemUtils {

    /**
     * 获取sdcard某文件夹绝对物理路径
     * 传入null获得根目录
     */
    public static String getSDCardPath(@Nullable String path) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (path == null) {
                return SDPath;
            } else {
                return SDPath + File.separator + path;
            }
        } else {
            return null;
        }
    }

    /**
     * @param groupDir
     * @return
     */
    @Nullable
    public static List<Card> getCardItems(String groupDir) {
        ArrayList<Card> cards = new ArrayList<>();
        File[] cardFiles = new File(groupDir).listFiles();
        for (File file : cardFiles) {
            Card card = inflateCardFromFile(file);
            if (card == null) break;
            cards.add(card);
        }
        return cards;
    }

    @Nullable
    public static List<File> getAllSubFiles(File file) {
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        List<File> cardFile = new ArrayList<>();
        for (File f : files) {
            if (f.isDirectory()) {
                List<File> temp = getAllSubFiles(f);
                if (temp != null) {
                    cardFile.addAll(temp);
                }
            } else {
                cardFile.add(f);
            }
        }
        return cardFile;
    }

    public static List<Card> getTaskItems(String taskDir) {
        ArrayList<Card> cards = new ArrayList<>();
        File[] taskFiles = new File(taskDir).listFiles();
        if (taskFiles != null) {
            for (File file : taskFiles) {
                Card card = inflateTaskFromFile(file);
                if (card == null) break;
                cards.add(card);
            }
        }
        return cards;
    }

    @Nullable
    public static List<Card> getAllSubCards(String dir) {
        List<File> files = getAllSubFiles(new File(dir));
        if (files == null) {
            return null;
        }
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                long diff = o1.lastModified() - o2.lastModified();
                if (diff > 0) {
                    return -1;
                } else if (diff < 0) {
                    return 1;
                } else return 0;
            }
        });
        List<Card> cards = new ArrayList<>();
        for (File f : files) {
            cards.add(inflateCardFromFile(f));
        }
        return cards;
    }

    @Nullable
    private static Card inflateTaskFromFile(File file) {
        if (file.isDirectory()) {
            return null;
        }
        Card card = new Card();
        try {
            FileInputStream fis;
            ObjectInputStream ois;
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            card = (Card) ois.readObject();
            card.setItemType(DataType.CARD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        card.setFileName(file.getName());
        card.setFilePath(file.getPath());
        card.setModifiedTime(file.lastModified());
        return card;
    }

    public static List<Group> getAllGroups(String dir) {
        List<File> files = getAllSubDirectories(new File(dir));
        if (files == null) {
            return null;
        }

        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                long diff = o1.lastModified() - o2.lastModified();
                if (diff > 0) {
                    return -1;
                } else if (diff < 0) {
                    return 1;
                } else return 0;
            }
        });
        List<Group> groups = new ArrayList<>();
        for (File f : files) {
            if (f.getName().equals("收信箱")) {
                groups.add(0, new Group(f.getName(), f.getPath(), f.lastModified()));
            } else {
                groups.add(new Group(f.getName(), f.getPath(), f.lastModified()));
            }
        }
        return groups;
    }

    private static List<File> getAllSubDirectories(File file) {
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        List<File> fileList = new ArrayList<>();
        for (File f : files) {
            if (f.isDirectory()) {
                fileList.add(f);
                List<File> temp = getAllSubDirectories(f);
                if (temp != null) {
                    fileList.addAll(temp);
                }
            }
        }
        return fileList;
    }

    public static List<Group> getSubGroups(String groupDir) {
        File group = new File(groupDir);
        ArrayList<Group> subGroups = new ArrayList<>();
        List<File> groups = Arrays.asList(group.listFiles());
        Collections.sort(groups, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (f1.lastModified() > f2.lastModified()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        if (groups.size() != 0) {
            for (File child : groups) {
                subGroups.add(new Group(child.getName(), child.getPath(), child.lastModified()));
            }
        }
        return subGroups;
    }

    @Nullable
    public static Card inflateCardFromFile(File file) {
        if (file.isDirectory()) {
            return null;
        }
        Card card = new Card();
        try {
            FileInputStream fis;
            ObjectInputStream ois;
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            card = (Card) ois.readObject();
            card.setItemType(DataType.CARD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        card.setFileName(file.getName());
        card.setFilePath(file.getPath());
        card.setModifiedTime(file.lastModified());
        return card;
    }


    /**
     * 将文件（byte[]） 保存进sdcard指定的路径下
     */
    public static boolean saveFileToSDCard(byte[] data, String dir,
                                           String filename) {
        BufferedOutputStream bos = null;
        if (isSDCardMounted()) {
            File file = new File(dir);
//            File file = new File(getSDCardPath(dir));
            if (!file.exists()) {
                boolean flags = file.mkdirs();
            }
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(
                        file, filename)));
                bos.write(data, 0, data.length);
                bos.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }


    /**
     * 将文件（byte[]） 保存进sdcard指定的路径下
     */

    public static byte[] Obj2Bytes(Object obj) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 保存bitmap到SD卡
     *
     * @param bitmap
     * @param fileName
     */
    @Nullable
    public static String saveBitmapToSDCard(Bitmap bitmap, String filePath, String fileName) {
        String path = getSDCardPath(filePath + File.separator + fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }

            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /****
     * 计算文件大小
     *
     * @param length
     * @return 文件大小
     */
    public static String getFileSzie(Long length) {
        if (length >= 1048576) {
            return (length / 1048576) + "MB";
        } else if (length >= 1024) {
            return (length / 1024) + "KB";
        } else if (length < 1024) {
            return length + "B";
        } else {
            return "0KB";
        }
    }

    /**
     * 字符串时间戳转时间格式
     *
     * @param timeStamp
     * @return
     */
    public static String getStrTime(String timeStamp) {
        String timeString = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DATE_FIELD, SimpleDateFormat.TIMEZONE_FIELD, Locale.CHINA);
        long l = Long.valueOf(timeStamp) * 1000;
        timeString = sdf.format(new Date(l));
        return timeString;
    }


    /**
     * 获取扩展内存的路径
     *
     * @param mContext
     * @return
     */
    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getStoragePath(Context mContext) {
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }


    // date类型转换为String类型
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    // long类型转换为String类型
    // currentTime要转换的long类型的时间
    // formatType要转换的string类型的时间格式
    public static String longToString(long currentTime, String formatType)
            throws java.text.ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws java.text.ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws java.text.ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    /**
     * @param strTime
     * @param formatType
     * @return
     * @throws java.text.ParseException string类型转换为long类型
     *                                  strTime要转换的String类型的时间
     *                                  formatType时间格式
     *                                  strTime的时间格式和formatType的时间格式必须相同
     */

    public static long stringToLong(String strTime, String formatType)
            throws java.text.ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    /**
     * 判断sdcard是否挂载
     */
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * 已知文件的路径，从sdcard中获取到该文件，返回byte[]
     */
    public static byte[] loadFileFromSDCard(String filepath) {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;
        if (isSDCardMounted()) {
            File file = new File(filepath);
            if (file.exists()) {
                try {
                    baos = new ByteArrayOutputStream();
                    bis = new BufferedInputStream(new FileInputStream(file));
                    byte[] buffer = new byte[1024 * 8];
                    int c = 0;
                    while ((c = bis.read(buffer)) != -1) {
                        baos.write(buffer, 0, c);
                        baos.flush();
                    }
                    return baos.toByteArray();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bis != null) {
                            bis.close();
                            baos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}
