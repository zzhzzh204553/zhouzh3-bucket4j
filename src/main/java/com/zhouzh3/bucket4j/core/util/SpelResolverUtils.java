package com.zhouzh3.bucket4j.core.util;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author haig
 */
public class SpelResolverUtils {

    public static String resolveExpression(String rawExpression, Method method, Object[] args) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();

        // 解析参数名
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName();
            context.setVariable(paramName, args[i]);
        }

        return parser.parseExpression(rawExpression).getValue(context, String.class);
    }
}
