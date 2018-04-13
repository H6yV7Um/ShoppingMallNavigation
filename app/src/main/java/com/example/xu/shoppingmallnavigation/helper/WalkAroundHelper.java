package com.example.xu.shoppingmallnavigation.helper;

import com.example.xu.shoppingmallnavigation.utils.MapSearchUtils;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.marker.FMModel;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Xu on 2018/4/13.
 *
 * @author Xu
 */
public class WalkAroundHelper {

    public static String[] getData(FMMap mFMMap, FMSearchAnalyser mSearchAnalyser) {
        int[] groupIds = new int[]{1, 2, 3, 4, 5, 6};
        ArrayList<FMModel> list = MapSearchUtils.queryModelByKeyword(mFMMap,
                groupIds, mSearchAnalyser, "");
        ArrayList<FMModel> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals("步行梯") || list.get(i).getName().equals("手扶电梯")
                    || list.get(i).getName().equals("直升电梯")
                    || list.get(i).getName().equals("出入口")
                    || list.get(i).getName().equals("洗手间")
                    || list.get(i).getName().equals("咨询台")
                    || list.get(i).getName().equals("ATM")
                    || list.get(i).getName().equals("")) {
                continue;
            } else {
                result.add(list.get(i));
            }
        }
        String[] data = new String[4];
//        for (int i = 0; i < data.length; i++) {
//            StringBuilder sb = new StringBuilder();
//            sb.append("推荐路线" + getChineseNumber(i) + ":\n\n");
//            int[] randomNumbers = getRandomNumbers(result.size());
//            for (int j = 0; j < randomNumbers.length; j++) {
//                if (j == randomNumbers.length - 1) {
//                    sb.append(result.get(randomNumbers[j]).getName() + "\n\n");
//                } else {
//                    sb.append(result.get(randomNumbers[j]).getName() + "----");
//                }
//            }
//            sb.append("特点: 无");
//            data[i] = sb.toString();
//        }
        data[0] = "推荐路线一:\n\n" + "茜丽杯子蛋糕----" + "满记甜品----" + "汉堡王----"
                + "STARBUCKS COFFEE----" + "晓本烘焙\n\n" + "特点: 吃货专属路线！\n";
        data[1] = "推荐路线二:\n\n" + "三星----" + "APPLE----" + "酷乐潮玩\n\n" + "特点: 科技宅！\n";
        data[2] = "推荐路线三:\n\n" + "安踏儿童----" + "早晨童鞋----" + "迪士尼婴幼----"
                + "乐友孕婴童\n\n" + "特点: 孩子也爱美！\n";
        data[3] = "推荐路线四:\n\n" + "THE SHOES BAR----" + "SKECHERS----" + "FILA----"
                + "UNIQLO\n\n" + "特点: 逛逛逛最佳路线！\n";
        return data;
    }

    private static String getChineseNumber(int index) {
        String[] num = {"一","二","三","四","五","六","七","八","九"};
        return num[index];
    }

    private static int[] getRandomNumbers(int n) {
        Random random = new Random();
        int[] numbers = new int[5];
        for (int i = 0; i < 5; i++) {
            int r = random.nextInt(n);
            numbers[i] = r;
        }
//        Log.i("test-getRandomNumbers", "randomNumbers: " + numbers);
        return numbers;
    }

}
