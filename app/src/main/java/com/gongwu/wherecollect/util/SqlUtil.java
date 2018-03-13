package com.gongwu.wherecollect.util;
import org.litepal.crud.DataSupport;

import java.util.List;
/**
 * Function:
 * Date: 2017/9/12
 *
 * @author zhaojin
 * @since JDK 1.7
 */
public class SqlUtil {
    public static <T> T find(Class<T> modelClass, String... conditions) {
        List<T> list = DataSupport.where(conditions).find(modelClass);
        if (StringUtils.isEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }
    }
}
