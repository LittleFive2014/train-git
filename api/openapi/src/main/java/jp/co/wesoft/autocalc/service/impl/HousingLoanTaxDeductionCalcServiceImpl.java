package jp.co.wesoft.autocalc.service.impl;
import jp.co.wesoft.autocalc.web.vo.HousingLoanTaxDeductionCalcRequest;
import jp.co.wesoft.autocalc.web.vo.HousingLoanTaxDeductionCalcRequest.*;
import jp.co.wesoft.autocalc.web.vo.HousingLoanTaxDeductionShowItemRequest;
import jp.co.wesoft.autocalc.service.HousingLoanTaxDeductionCalcService;
import jp.co.wesoft.autocalc.util.RoundingUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 住宅ローン控除自動計算
 * 
 * @author pei
 *
 */
@Service
public class HousingLoanTaxDeductionCalcServiceImpl implements HousingLoanTaxDeductionCalcService {


  /**
   * APPログ.
   */
  protected Logger appLog = LoggerFactory.getLogger(HousingLoanTaxDeductionCalcServiceImpl.class);


  @Override
  public Integer autoCalc(HousingLoanTaxDeduction housingLoanTaxDeduction, int adjustmentYear) {

    List<HousingLoanTaxDeductionFormula> formulas = new ArrayList<>();

    // 一般
    formulas.add(generalLess10Year());
    formulas.add(generalSpecialLess10Year());
    formulas.add(generalSpecificSpecialLess10Year());
    formulas.add(generalSpecificSpecialBetween11To13Year());
    formulas.add(generalSpecialSpecialParticularcaseLess10Year());
    formulas.add(generalSpecialSpecialParticularcaseBetween11To13Year());
    formulas.add(general20220101To20231231());
    formulas.add(general20240101To20251231());
    formulas.add(generalFamily20240101To20251231());

    // 認定住宅取得
    formulas.add(certifiedLess10Year());
    formulas.add(certifiedSpecificLess10Year());
    formulas.add(certifiedSpecificSpecialLess10Year());
    formulas.add(certifiedSpecificSpecialBetween11To13Year());
    formulas.add(certifiedSpecificSpecialParticularcaseLess10Year());
    formulas.add(certifiedSpecificSpecialParticularcaseBetween11To13Year());
    formulas.add(certified20220101To2023012031());
    formulas.add(certified20240101To20251231());

    // 中古
    formulas.add(certifiedUsedHousing20220101To20251231());
    formulas.add(generalGeneralUsedHousing20220101To20251231());


    // 特定増改築等
    formulas.add(renovation());
    formulas.add(renovationSpecific());


    // 震災再取得
    formulas.add(earthquakeRepurchase20110101To20121231());
    formulas.add(earthquakeRepurchase20130101To20150331());
    formulas.add(earthquakeRepurchase20150401To20211231());
    formulas.add(earthquakeRepurchaseSpecificSpecialLess10Year());
    formulas.add(earthquakeRepurchaseSpecificSpecial11To13Year());
    formulas.add(earthquakeRepurchasel20220101To20221231());
    formulas.add(earthquakerepurchaseSpecificSpeciall20220101To202212311To10Year());
    formulas.add(earthquakerepurchaseSpecificSpeciall20220101To2022123111To13Year());
    formulas.add(earthquakeRepurchase20230101To20231231());
    formulas.add(earthquakeRepurchase20240101To20251231());



    Optional<HousingLoanTaxDeductionFormula> formula = formulas.stream()
        .filter(e -> e.getCondition().condition(housingLoanTaxDeduction, adjustmentYear))
        .findFirst();
    Optional<Integer> result = formula.map(e -> e.getCalc().calc(housingLoanTaxDeduction));
    return result.orElse(0);
  }

  @Override
  public HousingLoanTaxDeductionShowItemRequest.HousingLoanTaxShowItem showItem(HousingLoanTaxDeductionCalcRequest.HousingLoanTaxDeduction housingLoanTaxDeduction, int adjustmentYear) {
    HousingLoanTaxDeductionShowItemRequest.HousingLoanTaxShowItem item = new HousingLoanTaxDeductionShowItemRequest.HousingLoanTaxShowItem();

    if (housingLoanTaxDeduction == null
            || !StringUtils.hasText(housingLoanTaxDeduction.getResidenceStartDate())
            || housingLoanTaxDeduction.getDeductionClass() == null
            || housingLoanTaxDeduction.getLoansOutstanding() == null) {
      return item;
    }


    if (housingLoanTaxDeduction.isResidenceStartDateBetween("2019/10/01", "2020/12/31")) {
      if (housingLoanTaxDeduction
              .getDeductionClass() == HousingLoanTaxDeductionCalcRequest.HousingDeductionClassEnum.GENERAL_SPECIFIC_SPECIAL.getCode()
              && housingLoanTaxDeduction.residencyYear(adjustmentYear) >= 10
              && housingLoanTaxDeduction.residencyYear(adjustmentYear) <= 13) {
        item.setPropertyPurchaseCostNoTaxShow(true);
      }
    }

    if (housingLoanTaxDeduction.isResidenceStartDateBetween("2021/01/01", "2022/12/31")) {
      if ((housingLoanTaxDeduction
              .getDeductionClass() == HousingLoanTaxDeductionCalcRequest.HousingDeductionClassEnum.GENERAL_SPECIFIC_SPECIAL.getCode()
              || housingLoanTaxDeduction
              .getDeductionClass() == HousingLoanTaxDeductionCalcRequest.HousingDeductionClassEnum.GENERAL_SPECIAL_SPECIAL_PARTICULAR_CASE
              .getCode())
              && housingLoanTaxDeduction.residencyYear(adjustmentYear) >= 10
              && housingLoanTaxDeduction.residencyYear(adjustmentYear) <= 13) {
        item.setPropertyPurchaseCostNoTaxShow(true);
      }
    }

    if (housingLoanTaxDeduction.isResidenceStartDateBetween("2019/10/01", "2020/12/31")) {
      if ((housingLoanTaxDeduction
              .getDeductionClass() == HousingLoanTaxDeductionCalcRequest.HousingDeductionClassEnum.CERTIFIED_SPECIFIC_SPECIAL.getCode())
              && housingLoanTaxDeduction.residencyYear(adjustmentYear) >= 10
              && housingLoanTaxDeduction.residencyYear(adjustmentYear) <= 13) {
        item.setPropertyPurchaseCostNoTaxShow(true);
      }
    }

    if (housingLoanTaxDeduction.isResidenceStartDateBetween("2021/01/01", "2022/12/31")) {
      if ((housingLoanTaxDeduction
              .getDeductionClass() == HousingDeductionClassEnum.CERTIFIED_SPECIFIC_SPECIAL.getCode()
              || housingLoanTaxDeduction
              .getDeductionClass() == HousingDeductionClassEnum.CERTIFIED_SPECIFIC_SPECIAL_PARTICULAR_CASE
              .getCode())
              && housingLoanTaxDeduction.residencyYear(adjustmentYear) >= 10
              && housingLoanTaxDeduction.residencyYear(adjustmentYear) <= 13) {
        item.setPropertyPurchaseCostNoTaxShow(true);
      }
    }

    if (housingLoanTaxDeduction.isResidenceStartDateBetween("2022/01/01", "2023/12/31")
            || housingLoanTaxDeduction.isResidenceStartDateBetween("2024/01/01", "2025/12/31")) {

      if (housingLoanTaxDeduction.getDeductionClass() == HousingDeductionClassEnum.CERTIFIED
              .getCode()) {
        item.setCertifiedHousingTypeShow(true);
      }
    }



    if (housingLoanTaxDeduction.isResidenceStartDateBetween("2022/01/01", "2025/12/31")) {
      if (housingLoanTaxDeduction.getDeductionClass() == HousingDeductionClassEnum.CERTIFIED
              .getCode()
              || housingLoanTaxDeduction.getDeductionClass() == HousingDeductionClassEnum.GENERAL
              .getCode()) {
        item.setUsedHousingShow(true);
      }

    }

    if (housingLoanTaxDeduction.getDeductionClass() == HousingDeductionClassEnum.RENOVATION
            .getCode()
            || housingLoanTaxDeduction
            .getDeductionClass() == HousingDeductionClassEnum.RENOVATION_SPECIFIC.getCode()) {
      item.setEnergyEfficiencyRenovationCostShow(true);
    }


    if (housingLoanTaxDeduction.isResidenceStartDateBetween("2019/10/01", "2021/12/31")
            || housingLoanTaxDeduction.isResidenceStartDateBetween("2022/01/01", "2022/12/31")) {
      if (housingLoanTaxDeduction
              .getDeductionClass() == HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_SPECIFIC_SPECIAL
              .getCode()
              || housingLoanTaxDeduction
              .getDeductionClass() == HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_SPECIFIC_SPECIAL_PARTICULAR_CASE
              .getCode()) {
        item.setPropertyPurchaseCostNoTaxShow(true);
      }

    }

    return item;
  }

  /**
   * 2014.1.1~2019.9.30 一般 年末残高×1％(20万円) 一般(特) 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula generalLess10Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {
      // 2014.1.1~2019.9.30
      // 2019.10.1~2020.12.31
      // 2021.01.1~2021.12.31
      if ((e.isResidenceStartDateBetween("2014/01/01", "2019/09/30")
          || e.isResidenceStartDateBetween("2019/10/01", "2020/12/31")
          || e.isResidenceStartDateBetween("2021/01/01", "2021/12/31"))
          // 一般
          && HousingDeductionClassEnum.GENERAL.getCode() == e.getDeductionClass()

      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10) {

        appLog.debug("generalLess10Year  year:" + y + "data:" + e);
        return true;
      }

      return false;
      // 年末残高×1％(20万円)
    }, e -> Math.min(RoundingUtils.roundDownUnder100Yen(e.getLoansOutstanding() / 100.0), 200000));
  }


  /**
   * 2014.1.1~2019.9.30 2019.10.1~2020.12.31 2021.01.1~2021.12.31 一般(特) 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula generalSpecialLess10Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {
      // 2014.1.1~2019.9.30
      // 2019.10.1~2020.12.31
      // 2021.01.1~2021.12.31
      if ((e.isResidenceStartDateBetween("2014/01/01", "2019/09/30")
          || e.isResidenceStartDateBetween("2019/10/01", "2020/12/31")
          || e.isResidenceStartDateBetween("2021/01/01", "2021/12/31"))
          // 一般（特）
          && HousingDeductionClassEnum.GENERAL_SPECIFIC.getCode() == e.getDeductionClass()

      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10) {

        appLog.debug("generalSpecialLess10Year  year:" + y + "data:" + e);
        return true;
      }

      return false;
      // 年末残高×1％(40万円)
    }, e -> Math.min(RoundingUtils.roundDownUnder100Yen(e.getLoansOutstanding() / 100.0), 400000));
  }

  /**
   * 2019.10.1~2020.12.31 2021.1.1~2022.12.31 一般(特特) 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula generalSpecificSpecialLess10Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {
      // 2019.10.1~2020.12.31
      // 2021.1.1~2022.12.31
      if ((e.isResidenceStartDateBetween("2019/10/01", "2020/12/31")
          || e.isResidenceStartDateBetween("2021/01/01", "2022/12/31")) &&
      // 一般(特特)
          HousingDeductionClassEnum.GENERAL_SPECIFIC_SPECIAL.getCode() == e.getDeductionClass()

      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10) {

        appLog.debug("generalSpecificSpecialLess10Year  year:" + y + "data:" + e);
        return true;
      }

      return false;
      // 年末残高×1％(40万円)
    }, e -> Math.min(RoundingUtils.roundDownUnder100Yen(e.getLoansOutstanding() / 100.0), 400000));
  }


  /**
   * 2019.10.1~2020.12.31 2021.1.1~2022.12.31 一般(特特) 11 to 13
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula generalSpecificSpecialBetween11To13Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2019.10.1~2020.12.31
      // 2021.1.1~2022.12.31
      if ((e.isResidenceStartDateBetween("2019/10/01", "2020/12/31")
          || e.isResidenceStartDateBetween("2021/01/01", "2022/12/31"))
          // 一般(特特)
          && HousingDeductionClassEnum.GENERAL_SPECIFIC_SPECIAL.getCode() == e.getDeductionClass()

      // 11~13年目
          && e.residencyYear(y) >= 11 && e.residencyYear(y) <= 13) {
        appLog.debug("generalSpecificSpecialBetween11To13Year  year:" + y + "data:" + e);
        return true;
      }

      return false;

    }, e -> {

      // ①年末残高等〔上限4,000万円〕×１％
      // ②（住宅取得等対価の額－消費税額）〔上限4,000万円〕×２％÷３
      // （注）この場合の「住宅取得等対価の額」は、補助金および住宅取得等資金の贈与の額を控除しないで計算した金額をいいます。
      double i = Math.min(e.getLoansOutstanding(), 40000000.0) / 100;
      if (e.getPropertyPurchaseCostNoTax() != null) {
        return RoundingUtils.roundDownUnder100Yen(
            Math.min(Math.min(e.getPropertyPurchaseCostNoTax(), 40000000.0) / 100 * 2 / 3, i));
      }
      return RoundingUtils.roundDownUnder100Yen(i);
    });
  }


  /**
   * 2021.1.1~2022.12.31 一般(特特特) 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula generalSpecialSpecialParticularcaseLess10Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2021.1.1~2022.12.31
      if (e.isResidenceStartDateBetween("2021/01/01", "2022/12/31")
          // 一般(特特特)
          && HousingDeductionClassEnum.GENERAL_SPECIAL_SPECIAL_PARTICULAR_CASE.getCode() == e
              .getDeductionClass()
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10) {

        appLog.debug(
            "generalSpecialSpecialParticularcaseLess10Year  year:" + y + "data:" + e);
        return true;
      }

      return false;

    }, e -> {

      // 年末残高×1％(40万円)
      return Math.min(RoundingUtils.roundDownUnder100Yen(e.getLoansOutstanding() / 100.0), 400000);
    });
  }


  /**
   * 2021.1.1~2022.12.31 一般(特特特) 11 to 13
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula generalSpecialSpecialParticularcaseBetween11To13Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2021.1.1~2022.12.31
      if (e.isResidenceStartDateBetween("2021/01/01", "2022/12/31")
          // 一般(特特特)
          && HousingDeductionClassEnum.GENERAL_SPECIAL_SPECIAL_PARTICULAR_CASE.getCode() == e
              .getDeductionClass()

      // 11~13年目
          && e.residencyYear(y) >= 11 && e.residencyYear(y) <= 13) {
        appLog.debug("generalSpecialSpecialParticularcaseBetween11To13Year  year:" + y + "data:"
            + e);
        return true;
      }

      return false;

    }, e -> {

      // ①年末残高等〔上限4,000万円〕×１％
      // ②（住宅取得等対価の額－消費税額）〔上限4,000万円〕×２％÷３
      // （注）この場合の「住宅取得等対価の額」は、補助金および住宅取得等資金の贈与の額を控除しないで計算した金額をいいます。
      int i = Math.min(e.getLoansOutstanding(), 40000000) / 100;
      if (e.getPropertyPurchaseCostNoTax() != null) {
        return Math.min(Math.min(e.getPropertyPurchaseCostNoTax(), 40000000) / 100 * 2 / 3, i);
      }
      return i;
    });
  }


  /**
   * 2022.1.1~2023.12.31 一般 1 to 13
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula general20220101To20231231() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2022.1.1~2023.12.31
      if (e.isResidenceStartDateBetween("2022/01/01", "2023/12/31")
          // 一般
          && HousingDeductionClassEnum.GENERAL.getCode() == e.getDeductionClass()
      // 1~13年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 13
      // 非中古判断
          && e.notUsedHousing()) {
        appLog.debug("general20220101To20231231  year:" + y + "data:"
            + e);
        return true;
      }

      return false;

    }, e -> {

      // 年末残高等〔上限3,000万円〕×0.7％
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(e.getLoansOutstanding(), 30000000.0) / 100 * 0.7);
    });
  }



  /**
   * 2024.1.1~2025.12.31 一般 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula general20240101To20251231() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 22024.1.1~2025.12.31
      if (e.isResidenceStartDateBetween("2024/01/01", "2025/12/31")
          // 一般
          && (HousingDeductionClassEnum.GENERAL.getCode() == e.getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10
      // 非中古判断
          && e.notUsedHousing()) {
        appLog.debug("general20240101To20251231  year:" + y + "data:" + e);
        return true;
      }

      return false;

    }, e -> {

      // 年末残高等〔上限2,000万円〕×0.7％
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(e.getLoansOutstanding(), 20000000.0) / 100 * 0.7);
    });
  }


  /**
   * 2024.1.1~2025.12.31 一般(特家) 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula generalFamily20240101To20251231() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 22024.1.1~2025.12.31
      if (e.isResidenceStartDateBetween("2024/01/01", "2025/12/31")
          // 一般(特家)
          && (HousingDeductionClassEnum.GENERAL_PARTICULAR_CASE_FAMILY.getCode() == e
              .getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10
      // 非中古判断
          && e.notUsedHousing()) {
        appLog.debug("generalFamily20240101To20251231  year:" + y + "data:" + e);
        return true;
      }

      return false;

    }, e -> {

      // 年末残高等〔上限2,000万円〕×0.7％
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(e.getLoansOutstanding(), 20000000.0) / 100 * 0.7);
    });
  }



  /**
   * 2014.1.1~2019.9.30 2019.10.1~2020.12.31 2021.1.1~2021.12.31 認定 認定（特家） 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula certifiedLess10Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2014.1.1~2019.9.30
      // 2019.10.1~2020.12.31
      // 2021.1.1~2021.12.31
      if ((e.isResidenceStartDateBetween("2014/01/01", "2019/09/30")
          || e.isResidenceStartDateBetween("2019/10/01", "2020/12/31")
          || e.isResidenceStartDateBetween("2021/01/01", "2021/12/31"))
          // 認定
          && (HousingDeductionClassEnum.CERTIFIED.getCode() == e.getDeductionClass()
              // 認定（特家）
              || HousingDeductionClassEnum.CERTIFIED_PARTICULAR_CASE_FAMILY.getCode() == e
                  .getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10

      // 特定エネルギ選択されない
          && Objects.isNull(e.getCertifiedHousingType())

      ) {
        appLog.debug("certifiedLess10Year  year:" + y + "data:" + e);
        return true;
      }

      return false;

    }, e -> {
      // 年末残高×1％(30万円)
      return RoundingUtils.roundDownUnder100Yen(Math.min(e.getLoansOutstanding() / 100, 300000));
    });
  }


  /**
   * 2014.1.1~2019.9.30 2019.10.1~2020.12.31 2021.1.1~2021.12.31 認定（特） 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula certifiedSpecificLess10Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2014.1.1~2019.9.30
      // 2019.10.1~2020.12.31
      // 2021.1.1~2021.12.31
      if ((e.isResidenceStartDateBetween("2014/01/01", "2019/09/30")
          || e.isResidenceStartDateBetween("2019/10/01", "2020/12/31")
          || e.isResidenceStartDateBetween("2021/01/01", "2021/12/31"))
          // 認定(特)
          && (HousingDeductionClassEnum.CERTIFIED_SPECIFIC.getCode() == e.getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10
      // 特定エネルギ選択されない
          && Objects.isNull(e.getCertifiedHousingType())) {
        appLog.debug("certifiedSpecificLess10Year  year:" + y + "data:" + e);
        return true;
      }

      return false;

    }, e -> {
      // 年末残高×1％(50万円)
      return RoundingUtils.roundDownUnder100Yen(Math.min(e.getLoansOutstanding() / 100, 500000));
    });
  }


  /**
   * 2019.10.1~2020.12.31 2021.1.1~2022.12.31 認定(特特) 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula certifiedSpecificSpecialLess10Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2019.10.1~2020.12.31
      // 2021.1.1~2022.12.31
      if ((e.isResidenceStartDateBetween("2019/01/01", "2020/12/31")
          || e.isResidenceStartDateBetween("2021/01/01", "2022/12/31"))
          // 認定(特特)
          && (HousingDeductionClassEnum.CERTIFIED_SPECIFIC_SPECIAL.getCode() == e
              .getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10
      // 特定エネルギ選択されない
          && Objects.isNull(e.getCertifiedHousingType())) {
        appLog.debug("certifiedSpecificSpecialLess10Year  year:" + y + "data:" + e);
        return true;
      }

      return false;

    }, e -> {
      // 年末残高×1％(50万円)
      return RoundingUtils.roundDownUnder100Yen(Math.min(e.getLoansOutstanding() / 100, 500000));
    });
  }


  /**
   * 2019.10.1~2020.12.31 2021.1.1~2022.12.31 認定(特特) 11 to 13
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula certifiedSpecificSpecialBetween11To13Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2019.10.1~2020.12.31
      // 2021.1.1~2022.12.31
      if ((e.isResidenceStartDateBetween("2019/01/01", "2020/12/31")
          || e.isResidenceStartDateBetween("2021/01/01", "2022/12/31"))
          // 認定(特特)
          && (HousingDeductionClassEnum.CERTIFIED_SPECIFIC_SPECIAL.getCode() == e
              .getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 11 && e.residencyYear(y) <= 13
      // 特定エネルギ選択されない
          && Objects.isNull(e.getCertifiedHousingType())) {
        appLog
            .debug("certifiedSpecificSpecialBetween11To13Year  year:" + y + "data:" + e);
        return true;
      }

      return false;
    }, e -> {

      // 次のいずれか少ない額が控除限度額
      // ①年末残高等〔上限5,000万円〕×１％
      // ②（住宅取得等対価の額－消費税額）〔上限5,000万円〕×２％÷３
      // （注）この場合の「住宅取得等対価の額」は、補助金および住宅取得等資金の贈与の額を控除しないで計算した金額をいいます。
      double i = Math.min(e.getLoansOutstanding(), 50000000.0) / 100;
      if (e.getPropertyPurchaseCostNoTax() != null) {
        i = Math.min(Math.min(e.getPropertyPurchaseCostNoTax(), 50000000.0) / 100 * 2 / 3, i);
      }
      return RoundingUtils.roundDownUnder100Yen(i);
    });
  }


  /**
   * 2021.1.1~2022.12.31 認定(特特特) 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula certifiedSpecificSpecialParticularcaseLess10Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2021.1.1~2022.12.31
      if ((e.isResidenceStartDateBetween("2021/01/01", "2022/12/31"))
          // 認定(特特特)
          && (HousingDeductionClassEnum.CERTIFIED_SPECIFIC_SPECIAL_PARTICULAR_CASE.getCode() == e
              .getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10
      // 特定エネルギ選択されない
          && Objects.isNull(e.getCertifiedHousingType())) {
        appLog.debug(
            "certifiedSpecificSpecialParticularcaseLess10Year  year:" + y + "data:" + e);
        return true;
      }

      return false;

    }, e -> {
      // 年末残高×1％(50万円)
      return RoundingUtils.roundDownUnder100Yen(Math.min(e.getLoansOutstanding() / 100, 500000));
    });
  }


  /**
   * 2021.1.1~2022.12.31 認定(特特特) 11 to 13
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula certifiedSpecificSpecialParticularcaseBetween11To13Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2021.1.1~2022.12.31
      if ((e.isResidenceStartDateBetween("2021/01/01", "2022/12/31"))
          // 認定(特特特)
          && (HousingDeductionClassEnum.CERTIFIED_SPECIFIC_SPECIAL_PARTICULAR_CASE.getCode() == e
              .getDeductionClass())
      // 11~13年目
          && e.residencyYear(y) >= 11 && e.residencyYear(y) <= 13
      // 特定エネルギ選択されない
          && Objects.isNull(e.getCertifiedHousingType())) {
        appLog.debug("certifiedSpecificSpecialParticularcaseBetween11To13Year  year:" + y + "data:"
            + e);
        return true;
      }

      return false;

    }, e -> {

      // 次のいずれか少ない額が控除限度額
      // ①年末残高等〔上限5,000万円〕×１％
      // ②（住宅取得等対価の額－消費税額）〔上限5,000万円〕×２％÷３
      // （注）この場合の「住宅取得等対価の額」は、補助金および住宅取得等資金の贈与の額を控除しないで計算した金額をいいます。
      double i = Math.min(e.getLoansOutstanding(), 50000000.0) / 100;
      if (e.getPropertyPurchaseCostNoTax() != null) {
        i = Math.min(Math.min(e.getPropertyPurchaseCostNoTax(), 50000000.0) / 100 * 2 / 3, i);
      }
      return RoundingUtils.roundDownUnder100Yen(i);
    });
  }


  /**
   * 2022.1.1~2023.13.31 認定 認定（特家） 特定エネルギ エネルギ 1 to 13
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula certified20220101To2023012031() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2022.1.1~2023.12.31
      if ((e.isResidenceStartDateBetween("2022/01/01", "2023/12/31"))
          // 認定
          && (HousingDeductionClassEnum.CERTIFIED.getCode() == e.getDeductionClass()
              // 認定（特家）
              || HousingDeductionClassEnum.CERTIFIED_PARTICULAR_CASE_FAMILY.getCode() == e
                  .getDeductionClass())
      // 1~13年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 13
      // 非中古判断
          && e.notUsedHousing()) {
        appLog.debug("certified20220101To2023012031  year:" + y + "data:" + e);
        return true;
      }

      return false;

    }, e -> {

      // 特定エネルギ選択されない
      if (Objects.isNull(e.getCertifiedHousingType())) {
        // ［認定住宅に該当する場合］
        // 年末残高等〔上限5,000万円〕×0.7％
        return RoundingUtils
            .roundDownUnder100Yen(Math.min(e.getLoansOutstanding(), 50000000.0) / 100 * 0.7);
      }

      CertifiedHousingTypeEnum certifiedHousingTypeEnum =
          CertifiedHousingTypeEnum.from(e.getCertifiedHousingType());

      return switch (Objects.requireNonNull(certifiedHousingTypeEnum)) {
        // ［特定エネルギー消費性能向上住宅に該当する場合］
        // 年末残高等〔上限4,500万円〕×0.7％
        case SPECIFIC_ENERGY_EFFICIENT_HOUSING -> RoundingUtils
                .roundDownUnder100Yen(Math.min(e.getLoansOutstanding(), 45000000.0) / 100 * 0.7);
        // ［エネルギー消費性能向上住宅に該当する場合］
        // 年末残高等〔上限4,000万円〕×0.7％
        case ENERGY_EFFICIENT_HOUSING -> RoundingUtils
                .roundDownUnder100Yen(Math.min(e.getLoansOutstanding(), 40000000.0) / 100 * 0.7);
      };
    });
  }


  /**
   * 2024.1.1~2025.12.31 認定 認定（特家） 特定エネルギ エネルギ 1 to 13
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula certified20240101To20251231() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2024.1.1~2025.12.31
      if ((e.isResidenceStartDateBetween("2024/01/01", "2025/12/31"))
          // 認定
          && (HousingDeductionClassEnum.CERTIFIED.getCode() == e.getDeductionClass()
              // 認定（特家）
              || HousingDeductionClassEnum.CERTIFIED_PARTICULAR_CASE_FAMILY.getCode() == e
                  .getDeductionClass())
      // 1~13年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 13
      // 非中古判断
          && e.notUsedHousing()) {
        appLog.debug("certified20240101To20251231  year:" + y + "data:" + e);
        return true;
      }

      return false;

    }, e -> {

      // 特定エネルギ選択されない
      if (Objects.isNull(e.getCertifiedHousingType())) {
        // ［認定住宅に該当する場合］
        // 年末残高等〔上限4,500万円〕×0.7％
        return RoundingUtils
            .roundDownUnder100Yen(Math.min(e.getLoansOutstanding(), 45000000.0) / 100 * 0.7);
      }

      CertifiedHousingTypeEnum certifiedHousingTypeEnum =
          CertifiedHousingTypeEnum.from(e.getCertifiedHousingType());

      return switch (Objects.requireNonNull(certifiedHousingTypeEnum)) {
        // ［特定エネルギー消費性能向上住宅に該当する場合］
        // 年末残高等〔上限3,500万円〕×0.7％
        case SPECIFIC_ENERGY_EFFICIENT_HOUSING -> RoundingUtils
                .roundDownUnder100Yen(Math.min(e.getLoansOutstanding(), 35000000.0) / 100 * 0.7);
        // ［エネルギー消費性能向上住宅に該当する場合］
        // 年末残高等〔上限3,000万円〕×0.7％
        case ENERGY_EFFICIENT_HOUSING -> RoundingUtils
                .roundDownUnder100Yen(Math.min(e.getLoansOutstanding(), 30000000.0) / 100 * 0.7);
      };
    });
  }

  /**
   * 2022.1.1~2025.12.31 認定(中古) 認定（特家） 特定エネルギ(中古) エネルギ(中古) 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula certifiedUsedHousing20220101To20251231() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2022.1.1~2025.12.31
      if ((e.isResidenceStartDateBetween("2022/01/01", "2025/12/31"))
          // 認定
          && (HousingDeductionClassEnum.CERTIFIED.getCode() == e.getDeductionClass()
              // 認定（特家）
              || HousingDeductionClassEnum.CERTIFIED_PARTICULAR_CASE_FAMILY.getCode() == e
                  .getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10
      // 中古
          && !e.notUsedHousing()) {
        appLog.debug("certifiedUsedHousing20220101To20251231  year:" + y + "data:" + e);
        return true;
      }

      return false;

    }, e -> {
      // 年末残高等〔上限3,000万円〕×0.7％
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(e.getLoansOutstanding(), 30000000.0) / 100 * 0.7);
    });
  }


  /**
   * 2022.1.1~2025.12.31 一般(中古) 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula generalGeneralUsedHousing20220101To20251231() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2022.1.1~2025.12.31
      if ((e.isResidenceStartDateBetween("2022/01/01", "2025/12/31"))
          // 一般
          && (HousingDeductionClassEnum.GENERAL.getCode() == e.getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10
      // 中古
          && !e.notUsedHousing()) {
        appLog.debug(
            "generalGeneralUsedHousing20220101To20251231  year:" + y + "data:" + e);
        return true;
      }

      return false;
    }, e -> {
      // 年末残高等〔上限3,000万円〕×0.7％
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(e.getLoansOutstanding(), 20000000.0) / 100 * 0.7);
    });
  }

  /**
   * 2014.4.1～2021.12.31 特定増改築等 1 to 5
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula renovation() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2014.4.1～2021.12.31
      if ((e.isResidenceStartDateBetween("2014/04/01", "2021/12/31"))
          // 特定増改築等
          && HousingDeductionClassEnum.RENOVATION.getCode() == e.getDeductionClass()
      // 1~5年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 5) {
        appLog.debug("renovation  year:" + y + "data:" + e);
        return true;
      }

      return false;
    }, e -> {

      int energyEfficiencyRenovationCost =
          e.getEnergyEfficiencyRenovationCost() == null ? 0 : e.getEnergyEfficiencyRenovationCost();
      // A×2％（4万円）＋（B - A）×1％（8万円）
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(energyEfficiencyRenovationCost / 100.0 * 2, 40000.0) + Math
              .min((e.getLoansOutstanding() - energyEfficiencyRenovationCost) / 100.0, 80000.0));
    });
  }



  /**
   * 2014.4.1～2021.12.31 特定増改築等(特) 1 to 5
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula renovationSpecific() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2014.4.1～2021.12.31
      if ((e.isResidenceStartDateBetween("2014/04/01", "2021/12/31"))
          // 特定増改築等(特)
          && HousingDeductionClassEnum.RENOVATION_SPECIFIC.getCode() == e.getDeductionClass()
      // 1~5年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 5) {
        appLog.debug("renovationSpecific  year:" + y + "data:" + e);
        return true;
      }

      return false;
    }, e -> {
      // A×2％（5万円）＋（B - A）×1％（7.5万円）
      return RoundingUtils.roundDownUnder100Yen(
          Math.min(e.getEnergyEfficiencyRenovationCost() / 100.0 * 2, 50000.0) + Math.min(
              (e.getLoansOutstanding() - e.getEnergyEfficiencyRenovationCost()) / 100.0, 75000.0));
    });
  }


  /**
   * 2011.1~2012.12 震災再取得 震災再取得等（特家） 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula earthquakeRepurchase20110101To20121231() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2011.1~2012.12
      if ((e.isResidenceStartDateBetween("2011/01/01", "2012/12/31"))
          // 震災再取得等
          && (HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE.getCode() == e.getDeductionClass()
              // 震災再取得等（特家）
              || HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_PARTICULAR_CASE_FAMILY.getCode() == e
                  .getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10) {
        appLog.debug("earthquakeRepurchase20110101To20121231  year:" + y + "data:" + e);
        return true;
      }

      return false;
    }, e -> {
      // 年末残高×1.2％(48万円)
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(e.getLoansOutstanding() / 100.0 * 1.2, 480000.0));
    });
  }


  /**
   * 2013.1~2015.03 震災再取得 震災再取得等（特家） 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula earthquakeRepurchase20130101To20150331() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2013.1~2015.03
      if ((e.isResidenceStartDateBetween("2013/01/01", "2014/03/31"))
          // 震災再取得等
          && (HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE.getCode() == e.getDeductionClass()
              // 震災再取得等（特家）
              || HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_PARTICULAR_CASE_FAMILY.getCode() == e
                  .getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10) {
        appLog.debug("earthquakeRepurchase20130101To20150331  year:" + y + "data:" + e);
        return true;

      }

      return false;
    }, e -> {
      // 年末残高×1.2％(36万円)
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(e.getLoansOutstanding() / 100.0 * 1.2, 360000.0));
    });
  }


  /**
   * 2015.4~2019.9 2019.10~2021.12 震災再取得 震災再取得等（特家） 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula earthquakeRepurchase20150401To20211231() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2015.4~2019.9
      // 2019.10~2021.12
      if ((e.isResidenceStartDateBetween("2014/04/01", "2019/09/30")
          || e.isResidenceStartDateBetween("2019/10/01", "2021/12/31"))
          // 震災再取得等
          && (HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE.getCode() == e.getDeductionClass()
              // 震災再取得等（特家）
              || HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_PARTICULAR_CASE_FAMILY.getCode() == e
                  .getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10) {
        appLog.debug("earthquakeRepurchase20150401To20211231  year:" + y + "data:" + e);
        return true;
      }

      return false;
    }, e -> {
      // 年末残高×1.2％(60万円)
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(e.getLoansOutstanding() / 100.0 * 1.2, 600000.0));
    });
  }


  /**
   * 2019.10~2021.12 震災再取得(特特) 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula earthquakeRepurchaseSpecificSpecialLess10Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2019.10~2021.12
      if (e.isResidenceStartDateBetween("2019/10/01", "2021/12/31")
          // 震災再取得(特特)
          && HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_SPECIFIC_SPECIAL.getCode() == e
              .getDeductionClass()
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10) {
        appLog.debug(
            "earthquakeRepurchaseSpecificSpecialLess10Year  year:" + y + "data:" + e);
        return true;
      }

      return false;
    }, e -> {
      // 年末残高×1.2％(60万円)
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(e.getLoansOutstanding() / 100.0 * 1.2, 600000.0));
    });
  }


  /**
   * 2019.10~2021.12 震災再取得(特特) 11 to 13
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula earthquakeRepurchaseSpecificSpecial11To13Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2019.10~2021.12
      if (e.isResidenceStartDateBetween("2019/10/01", "2021/12/31")
          // 震災再取得(特特)
          && HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_SPECIFIC_SPECIAL.getCode() == e
              .getDeductionClass()
      // 11~13年目
          && e.residencyYear(y) >= 11 && e.residencyYear(y) <= 13) {
        appLog.debug(
            "earthquakeRepurchaseSpecificSpecial11To13Year  year:" + y + "data:" + e);
        return true;
      }

      return false;
    }, e -> {
      // 次のいずれか少ない額が控除限度額（上限33.33万円）
      // ①年末残高等〔上限5,000万円〕×1.2％
      // ②（住宅取得等対価の額－消費税額）〔上限5,000万円〕×２％÷３
      // （注）この場合の「住宅取得等対価の額」は、補助金および住宅取得等資金の贈与の額を控除しないで計算した金額をいいます。
      double result = Math.min(e.getLoansOutstanding(), 50000000.0) / 100 * 1.2;
      if (e.getPropertyPurchaseCostNoTax() != null) {
        result =
            Math.min(result, Math.min(e.getPropertyPurchaseCostNoTax(), 50000000.0) / 100 * 2 / 3);
      }
      return RoundingUtils.roundDownUnder100Yen(Math.min(result, 333300));
    });
  }


  /**
   * 2022.1~2022.12 震災再取得 年末残高×0.9％(45万円) 1 to 13
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula earthquakeRepurchasel20220101To20221231() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2022.1~2022.12
      if (e.isResidenceStartDateBetween("2022/01/01", "2022/12/31")
          // 震災再取得 震災再取得等（特家）
          && (HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE.getCode() == e.getDeductionClass()
              || HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_PARTICULAR_CASE_FAMILY.getCode() == e
                  .getDeductionClass())
      // 11~13年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 13) {

        appLog.debug("earthquakeRepurchasel20220101To20221231  year:" + y + "data:" + e);
        return true;
      }

      return false;
    }, e -> {
      // 年末残高×0.9％(45万円)
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(e.getLoansOutstanding() / 100.0 * 0.9, 450000.0));
    });
  }


  /**
   * 2022.1~2022.12 震災再取得(特特) 震災再取得(特特特 年末残高×1.2％(60万円) 1 to 10
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula earthquakerepurchaseSpecificSpeciall20220101To202212311To10Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2022.1~2022.12
      if (e.isResidenceStartDateBetween("2022/01/01", "2022/12/31")
          // // 震災再取得(特特)
          && (HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_SPECIFIC_SPECIAL.getCode() == e
              .getDeductionClass()
              // 震災再取得(特特特)
              ||HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_SPECIFIC_SPECIAL_PARTICULAR_CASE
                  .getCode() == e.getDeductionClass())
      // 1~10年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 10) {
        appLog.debug("earthquakerepurchaseSpecificSpeciall20220101To202212311To10Year  year:" + y
            + "data:" + e);
        return true;
      }

      return false;
    }, e -> {
      // 年末残高×1.2％(60万円)
      return RoundingUtils
          .roundDownUnder100Yen(Math.min(e.getLoansOutstanding() / 100.0 * 1.2, 600000.0));
    });
  }

  /**
   * 2022.1~2022.12 11 to 13年 "震災再取得(特特) 震災再取得(特特特)" 次のいずれか少ない額が控除限度額（上限33.33万円）
   * ①年末残高等〔上限5,000万円〕×1.2％ ②（住宅取得等対価の額－消費税額）〔上限5,000万円〕×２％÷３
   * （注）この場合の「住宅取得等対価の額」は、補助金および住宅取得等資金の贈与の額を控除しないで計算した金額をいいます。
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula earthquakerepurchaseSpecificSpeciall20220101To2022123111To13Year() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2022.1~2022.12
      if (e.isResidenceStartDateBetween("2022/01/01", "2022/12/31")
          // // 震災再取得(特特)
          && (HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_SPECIFIC_SPECIAL.getCode() == e
              .getDeductionClass()
              // 震災再取得(特特特)
              || HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_SPECIFIC_SPECIAL_PARTICULAR_CASE
                  .getCode() == e.getDeductionClass())
      // 11~13年目
          && e.residencyYear(y) >= 11 && e.residencyYear(y) <= 13) {
        appLog.debug("earthquakerepurchaseSpecificSpeciall20220101To2022123111To13Year  year:" + y
            + "data:" + e);
        return true;
      }

      return false;
    }, e -> {
      // 次のいずれか少ない額が控除限度額（上限33.33万円）
      // ①年末残高等〔上限5,000万円〕×1.2％
      // ②（住宅取得等対価の額－消費税額）〔上限5,000万円〕×２％÷３
      // （注）この場合の「住宅取得等対価の額」は、補助金および住宅取得等資金の贈与の額を控除しないで計算した金額をいいます。
      double result = Math.min(e.getLoansOutstanding(), 50000000.0) / 100 * 1.2;
      if (e.getPropertyPurchaseCostNoTax() != null) {
        result =
            Math.min(result, Math.min(e.getPropertyPurchaseCostNoTax(), 50000000.0) / 100 * 2 / 3);
      }
      return RoundingUtils
          .roundDownUnder100Yen(RoundingUtils.roundDownUnder100Yen(Math.min(result, 333300)));
    });
  }

  /**
   * 2023.1~2023.12 震災再取得 震災再取得等（特家） 年末残高×0.9％(45万円)
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula earthquakeRepurchase20230101To20231231() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2023.1~2023.12
      if (e.isResidenceStartDateBetween("2023/01/01", "2023/12/31")
          // // 震災再取得
          && (HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE.getCode() == e.getDeductionClass()
              // 震災再取得等（特家）
              || HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_PARTICULAR_CASE_FAMILY.getCode() == e
                  .getDeductionClass())
      // 1~13年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 13) {
        appLog.debug("earthquakeRepurchase20230101To20231231  year:" + y + "data:" + e);
        return true;
      }
      return false;
    }, e -> {
      // 年末残高×0.9％(45万円)
      double result = e.getLoansOutstanding() / 100.0 * 0.9;
      return RoundingUtils
          .roundDownUnder100Yen(RoundingUtils.roundDownUnder100Yen(Math.min(result, 450000.0)));
    });
  }

  /**
   * 2024.1~2025.12 "震災再取得 震災再取得等（特家）" "震災再取得 " 年末残高×0.9％(40.5万円)
   * 
   * @return 計算結果
   */
  private HousingLoanTaxDeductionFormula earthquakeRepurchase20240101To20251231() {
    return new HousingLoanTaxDeductionFormula((e, y) -> {

      // 2024.1~2025.12
      if (e.isResidenceStartDateBetween("2024/01/01", "2025/12/31")
          // // 震災再取得
          && (HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE.getCode() == e.getDeductionClass()
              // 震災再取得等（特家）
              || HousingDeductionClassEnum.EARTHQUAKE_REPURCHASE_PARTICULAR_CASE_FAMILY.getCode() == e
                  .getDeductionClass())

      // 11~13年目
          && e.residencyYear(y) >= 1 && e.residencyYear(y) <= 13) {

        appLog.debug("earthquakeRepurchase20240101To20251231  year:" + y + "data:" + e);
        return true;
      }
      return false;
    }, e -> {
      // 年末残高×0.9％(40.5万円)
      double result = e.getLoansOutstanding() / 100.0 * 0.9;
      return RoundingUtils
          .roundDownUnder100Yen(RoundingUtils.roundDownUnder100Yen(Math.min(result, 405000.0)));
    });
  }

  @FunctionalInterface
  public  interface HousingLoanTaxDeductionCondition {
     boolean condition(HousingLoanTaxDeduction housingLoanTaxDeduction,
                       Integer adjustmentYear);
  }


  @FunctionalInterface
  public  interface HousingLoanTaxDeductionCalc {
     Integer calc(HousingLoanTaxDeduction housingLoanTaxDeduction);
  }


  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class HousingLoanTaxDeductionFormula {
    private HousingLoanTaxDeductionCondition condition;
    private HousingLoanTaxDeductionCalc calc;
  }
}
