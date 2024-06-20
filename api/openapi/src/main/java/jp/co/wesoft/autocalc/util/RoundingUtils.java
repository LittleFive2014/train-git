package jp.co.wesoft.autocalc.util;

import java.math.BigDecimal;

public class RoundingUtils {
  // 100円未満の端数を切り捨てる関数
  public static int roundDownUnder100Yen(double amount) {
    BigDecimal bd = new BigDecimal(amount);
    bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
    return bd.intValue() / 100 * 100;
  }
}
