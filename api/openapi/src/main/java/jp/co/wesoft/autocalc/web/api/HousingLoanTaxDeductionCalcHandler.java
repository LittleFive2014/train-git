package jp.co.wesoft.autocalc.web.api;

import jp.co.wesoft.autocalc.service.HousingLoanTaxDeductionCalcService;
import jp.co.wesoft.autocalc.web.vo.HousingLoanTaxDeductionCalcRequest;
import jp.co.wesoft.autocalc.web.vo.ResponseData;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class HousingLoanTaxDeductionCalcHandler {

    private final HousingLoanTaxDeductionCalcService housingLoanTaxDeductionCalcService;

    public HousingLoanTaxDeductionCalcHandler(HousingLoanTaxDeductionCalcService housingLoanTaxDeductionCalcService) {
        this.housingLoanTaxDeductionCalcService = housingLoanTaxDeductionCalcService;
    }


    public Mono<ServerResponse> housingLoanTaxDeductionCalc(ServerRequest request) {

        Mono<ServerResponse> serverResponseMono = request.bodyToMono(HousingLoanTaxDeductionCalcRequest.class).flatMap(e -> {

            Integer integer = housingLoanTaxDeductionCalcService.autoCalc(e.getData(), e.getAdjustmentYear());

            ResponseData<Integer> responseData = new ResponseData();
            responseData.setData(integer);
            responseData.setCode(200);

            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON).
            bodyValue(responseData);

        });
        return serverResponseMono;
    }
}
