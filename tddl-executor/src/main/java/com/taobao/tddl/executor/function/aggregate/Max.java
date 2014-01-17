package com.taobao.tddl.executor.function.aggregate;

import java.util.HashMap;
import java.util.Map;

import com.taobao.tddl.common.exception.NotSupportException;
import com.taobao.tddl.executor.function.AggregateFunction;
import com.taobao.tddl.optimizer.core.datatype.DataType;
import com.taobao.tddl.optimizer.core.expression.IColumn;
import com.taobao.tddl.optimizer.core.expression.IFunction;
import com.taobao.tddl.optimizer.exceptions.FunctionException;

/**
 * @since 5.1.0
 */
public class Max extends AggregateFunction {

    private Object max = null;

    public Max(){
    }

    @Override
    public void serverMap(Object[] args) throws FunctionException {
        doMax(args);
    }

    @Override
    public void serverReduce(Object[] args) throws FunctionException {
        doMax(args);
    }

    private void doMax(Object[] args) {
        Object o = args[0];
        if (o != null) {
            if (max == null) {
                max = o;
            }
            if (((Comparable) o).compareTo(max) > 0) {
                max = o;
            }

        }
    }

    public int getArgSize() {
        return 1;
    }

    @Override
    public Map<String, Object> getResult() {
        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put(function.getColumnName(), max);
        return resMap;
    }

    @Override
    public void clear() {
        max = null;
    }

    @Override
    public DataType getReturnType() {
        return this.getMapReturnType();
    }

    @Override
    public DataType getMapReturnType() {
        Object[] args = function.getArgs().toArray();

        if (args[0] instanceof IColumn) {
            return ((IColumn) args[0]).getDataType();
        }

        if (args[0] instanceof IFunction) {
            return ((IFunction) args[0]).getDataType();
        }

        throw new NotSupportException();
    }

}
