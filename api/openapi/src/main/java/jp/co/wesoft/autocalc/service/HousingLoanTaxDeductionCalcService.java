package jp.co.wesoft.autocalc.service;

import jp.co.wesoft.autocalc.web.vo.HousingLoanTaxDeductionShowItemRequest;
import jp.co.wesoft.autocalc.web.vo.HousingLoanTaxDeductionCalcRequest.HousingLoanTaxDeduction;




public interface HousingLoanTaxDeductionCalcService {
    Integer autoCalc(HousingLoanTaxDeduction housingLoanTaxDeduction, int adjustmentYear);
   HousingLoanTaxDeductionShowItemRequest.HousingLoanTaxShowItem showItem(HousingLoanTaxDeduction housingLoanTaxDeduction, int adjustmentYear);
}
