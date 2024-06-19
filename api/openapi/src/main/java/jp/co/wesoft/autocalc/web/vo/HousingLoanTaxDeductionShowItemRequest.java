package jp.co.wesoft.autocalc.web.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jp.co.wesoft.autocalc.web.vo.HousingLoanTaxDeductionCalcRequest.HousingLoanTaxDeduction;

@Getter
@Setter
@ToString
public class HousingLoanTaxDeductionShowItemRequest {

  HousingLoanTaxDeduction data;

  // 年度
  private Integer adjustmentYear;

  @Getter
  @Setter
  @ToString
  public static class HousingLoanTaxShowItem {
    // 中古住宅 0対象外 1対象
    private boolean usedHousingShow;
    // 住宅取得等対価の額－消費税額
    private boolean propertyPurchaseCostNoTaxShow;
    // 認定住宅等の区分
    private boolean certifiedHousingTypeShow;
    // 省エネ・バリアフリー改修工事の金額
    private boolean energyEfficiencyRenovationCostShow;
  }

}
