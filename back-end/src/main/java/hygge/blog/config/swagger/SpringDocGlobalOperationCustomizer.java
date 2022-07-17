package hygge.blog.config.swagger;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

/**
 * @author Xavier
 * @date 2022/7/17
 */


@Component
public class SpringDocGlobalOperationCustomizer implements GlobalOperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Parameter uidInfo = new Parameter()
                .in(ParameterIn.HEADER.toString())
                .name("uid")
                .description("当前登录用户的编号").schema(new StringSchema()).required(false);
        operation.addParametersItem(uidInfo);

        Parameter tokenInfo = new Parameter()
                .in(ParameterIn.HEADER.toString())
                .name("token")
                .description("当前登录用户的用户令牌").schema(new StringSchema()).required(false);
        operation.addParametersItem(tokenInfo);

        Parameter scope = new Parameter()
                .in(ParameterIn.HEADER.toString())
                .name("scope")
                .description("当前登录用户的登录渠道").schema(new StringSchema()).required(false);
        operation.addParametersItem(scope);

        return operation;
    }
}