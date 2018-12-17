package com.zskj.utils;

import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemBase;
import net.sf.sevenzipjbinding.SevenZipException;

/**
 * @ProjectName: sevenziptest
 * @packageName: com.zskj.utils
 * @Description:
 * @author: huayang.bai
 * @date: 2018-12-14 17:30
 */
public abstract class AbstractIOutCreateCallback<T extends IOutItemBase> implements IOutCreateCallback<T> {

    @Override
    public void setOperationResult(boolean operationResultOk) throws SevenZipException {

    }

    @Override
    public void setTotal(long total) throws SevenZipException {

    }

    @Override
    public void setCompleted(long complete) throws SevenZipException {

    }
}
