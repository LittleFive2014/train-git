package jp.co.wesoft.autocalc.web.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
@ToString
public class HousingLoanTaxDeductionCalcRequest {

  HousingLoanTaxDeduction data;

  // 年度
  private Integer adjustmentYear;

  @Getter
  @Setter
  @ToString
  public static class HousingLoanTaxDeduction {
    // 居住開始年月日(yyyy/MM/dd)
    private String residenceStartDate;
    // 控除の種類
    private Integer deductionClass;
    // 借入金等年末残高
    private Integer loansOutstanding;
    // 中古住宅 0対象外 1対象
    private Integer usedHousing;
    // 住宅取得等対価の額－消費税額
    private Integer propertyPurchaseCostNoTax;
    // 認定住宅等の区分
    private Integer certifiedHousingType;
    // 省エネ・バリアフリー改修工事の金額
    private Integer energyEfficiencyRenovationCost;



    /**
     * 非中古判断
     * 
     * @return true 非中古
     */
    public boolean notUsedHousing() {

      return Objects.isNull(usedHousing)
          || UsedHousingEnum.NON_TARGET.code == this.usedHousing;
    }

    public boolean isResidenceStartDateBetween(String start, String end) {
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd");
      LocalDate startDate = LocalDate.parse(start, fmt);
      LocalDate endDate = LocalDate.parse(end, fmt);
      LocalDate residenceStartDate = LocalDate.parse(this.residenceStartDate, fmt);
      return startDate.compareTo(residenceStartDate) <= 0
          && endDate.compareTo(residenceStartDate) >= 0;
    }

    public int residencyYear(int adjustmentYear) {
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd");
      LocalDate residenceStartDate = LocalDate.parse(this.residenceStartDate, fmt);
      LocalDate of = LocalDate.of(adjustmentYear, residenceStartDate.getMonth(),
          residenceStartDate.getDayOfMonth());
      return Period.between(residenceStartDate, of).getYears();
    }
  }


  @Getter
  @AllArgsConstructor
  public  enum UsedHousingEnum {
    // 0対象外
    NON_TARGET(0),
    // 1対象
    TARGET(1);

    private final int code;

    public  UsedHousingEnum from(int code) {
      for (UsedHousingEnum deductionClass : values()) {
        if (code == deductionClass.getCode()) {
          return deductionClass;
        }
      }
      throw new RuntimeException("unsupported code");
    }
  }



  @Getter
  @AllArgsConstructor
  public  enum CertifiedHousingTypeEnum {
    // 特定エネルギ住宅
    SPECIFIC_ENERGY_EFFICIENT_HOUSING(1),
    // エネルギ住宅
    ENERGY_EFFICIENT_HOUSING(2);

    private final int code;

    public static CertifiedHousingTypeEnum from(Integer code) {
      if (Objects.isNull(code)) {
        return null;
      }
      for (CertifiedHousingTypeEnum deductionClass : values()) {
        if (code == deductionClass.getCode()) {
          return deductionClass;
        }
      }
      throw new RuntimeException("unsupported code");
    }
  }



  @Getter
  @AllArgsConstructor
  public  enum HousingDeductionClassEnum {

    // 1 一般
    GENERAL(1)
    // 5 一般（特）
    , GENERAL_SPECIFIC(5)
    // 9 一般（特特）
    , GENERAL_SPECIFIC_SPECIAL(9)
    // 21 一般（特特特）
    , GENERAL_SPECIAL_SPECIAL_PARTICULAR_CASE(21)
    // 12 一般（特家）
    , GENERAL_PARTICULAR_CASE_FAMILY(12)
    // 2 認定
    , CERTIFIED(2)
    // 6 認定（特）
    , CERTIFIED_SPECIFIC(6)
    // 10 認定（特特）
    , CERTIFIED_SPECIFIC_SPECIAL(10)
    // 22 認定（特特特）
    , CERTIFIED_SPECIFIC_SPECIAL_PARTICULAR_CASE(22)
    // 13 認定（特家）
    , CERTIFIED_PARTICULAR_CASE_FAMILY(13)
    // 3 特定増改築等
    , RENOVATION(3)
    // 7 特定増改築等（特）
    , RENOVATION_SPECIFIC(7)
    // 4 震災再取得等
    , EARTHQUAKE_REPURCHASE(4)
    // 8 震災再取得等（特）
    , EARTHQUAKE_REPURCHASE_SPECIFIC(8)
    // 11 震災再取得等（特特）
    , EARTHQUAKE_REPURCHASE_SPECIFIC_SPECIAL(11)
    // 24 震災再取得等（特特特）
    , EARTHQUAKE_REPURCHASE_SPECIFIC_SPECIAL_PARTICULAR_CASE(24)
    // 14 震災再取得等（特家）
    , EARTHQUAKE_REPURCHASE_PARTICULAR_CASE_FAMILY(14);

    private final int code;

    public static HousingDeductionClassEnum from(Integer code) {
      if (Objects.isNull(code)) {
        return null;
      }
      for (HousingDeductionClassEnum deductionClass : values()) {
        if (code == deductionClass.getCode()) {
          return deductionClass;
        }
      }
      throw new RuntimeException("unsupported code");
    }
  }
}
