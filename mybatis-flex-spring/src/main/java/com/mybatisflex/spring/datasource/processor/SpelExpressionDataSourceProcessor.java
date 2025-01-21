package com.mybatisflex.spring.datasource.processor;

import com.mybatisflex.core.datasource.processor.DataSourceProcessor;
import com.mybatisflex.processor.util.StrUtil;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * SpEL表达式支持处理器
 *
 * @author Alay
 * @since 2024-12-07 15:48
 */
public class SpelExpressionDataSourceProcessor implements DataSourceProcessor {
    /**
     * 动态表达式前缀
     */
    private static final String DYNAMIC_PREFIX = "#";
    /**
     * 参数发现器
     */
    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    /**
     * Express语法解析器
     */
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private BeanResolver beanResolver;
    /**
     * 解析上下文的模板 对于默认不设置的情况下,从参数中取值的方式 #param1
     * 设置指定模板 ParserContext. TEMPLATE_EXPRESSION{@link ParserContext#TEMPLATE_EXPRESSION} 后的取值方式: #{#param1}
     */
    private ParserContext parserContext = new ParserContext() {

        @Override
        public boolean isTemplate() {
            return false;
        }

        @Override
        public String getExpressionPrefix() {
            return null;
        }

        @Override
        public String getExpressionSuffix() {
            return null;
        }
    };


    @Override
    public String process(String dataSourceKey, Object mapper, Method method, Object[] arguments) {
        if (StrUtil.isBlank(dataSourceKey)) return null;
        if (!dataSourceKey.startsWith(DYNAMIC_PREFIX)) return null;
        if (arguments.length == 0) return null;

        RootObject rootObject = new RootObject(method, arguments, mapper);
        StandardEvaluationContext context = new MethodBasedEvaluationContext(rootObject, method, arguments, NAME_DISCOVERER);
        context.setBeanResolver(beanResolver);
        final Object value = PARSER.parseExpression(dataSourceKey, parserContext).getValue(context);
        return value == null ? null : value.toString();
    }


    public void setBeanResolver(BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
    }

    public void setParserContext(ParserContext parserContext) {
        this.parserContext = parserContext;
    }

    public static class RootObject {
        private final Method method;
        private final Object[] args;
        private final Object target;

        public RootObject(Method method, Object[] args, Object target) {
            this.method = method;
            this.args = args;
            this.target = target;
        }

        public Method getMethod() {
            return method;
        }

        public Object[] getArgs() {
            return args;
        }

        public Object getTarget() {
            return target;
        }
    }

}
