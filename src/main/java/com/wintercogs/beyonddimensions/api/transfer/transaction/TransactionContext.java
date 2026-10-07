package com.wintercogs.beyonddimensions.api.transfer.transaction;

/**
 * 移植垫片：NeoForge 26.x 的事务上下文。
 * 语义与上游一致：一个上下文对应一个「当前打开的事务」。
 */
public interface TransactionContext
{
    Transaction getOpenTransaction();

    void addCloseCallback(Object key, Transaction.CloseCallback callback);
}