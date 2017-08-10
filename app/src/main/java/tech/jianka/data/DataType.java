package tech.jianka.data;

/**
 * Created by Richa on 2017/8/8.
 */

public class DataType {
    /**
     * Item 的常量
     */
    public static final int GROUP = 509;
    public static final int CARD = 753;

    private static final int CARD_PLAIN_TEXT = 871;

    /**
     * Task的常量
     */
    public static final int TASK_IMPORTANT_EMERGENT = 627;
    public static final int TASK_IMPORTANT_NOT_EMERGENT = 325;
    public static final int TASK_UNIMPORTANT_EMERGENT = 465;
    public static final int TASK_UNIMPORTANT_NOT_EMERGENT = 789;
    public static final int[] TASK_TYPE = {TASK_IMPORTANT_EMERGENT, TASK_IMPORTANT_NOT_EMERGENT,
            TASK_UNIMPORTANT_EMERGENT, TASK_UNIMPORTANT_NOT_EMERGENT};
}
