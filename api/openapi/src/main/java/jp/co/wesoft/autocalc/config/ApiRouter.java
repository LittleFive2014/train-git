package jp.co.wesoft.autocalc.config;

import jp.co.wesoft.autocalc.web.api.HousingLoanTaxDeductionCalcHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration(proxyBeanMethods = false)
public class ApiRouter {
    @Bean
    public RouterFunction<ServerResponse> routes(HousingLoanTaxDeductionCalcHandler handler) {

        return route(POST("/housingLoanTaxDeductionCalc"), handler::housingLoanTaxDeductionCalc);
    }
}
