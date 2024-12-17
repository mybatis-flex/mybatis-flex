package com.mybatisflex.solon.annotation;

import com.mybatisflex.annotation.UseDataSource;
import com.mybatisflex.core.datasource.DataSourceKey;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.aspect.MethodInterceptor;
import org.noear.solon.core.util.TmplUtil;

/**
 * @author noear 2024/12/17 created
 */
public class UseDataSourceInterceptor implements MethodInterceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        UseDataSource anno = inv.getMethodAnnotation(UseDataSource.class);

        if (anno == null) {
            anno = inv.getTargetAnnotation(UseDataSource.class);
        }

        if (anno == null) {
            return inv.invoke();
        } else {
            String dsName = TmplUtil.parse(anno.value(), inv);
            DataSourceKey.use(dsName);

            try {
                return inv.invoke();
            } finally {
                //还原
                DataSourceKey.clear();
            }
        }
    }
}
