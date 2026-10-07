package com.wintercogs.beyonddimensions.registry;

import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/**
 * 模组自有的注册句柄（等价于上游的 DeferredHolder）。
 * <p>
 * 注册沿用「先实例化、后写注册表」两阶段：句柄在实例化阶段绑定值，在写表阶段被写入注册表。
 * 使用 Fabric/原版 {@code Registry.register}，无任何外来命名空间。
 */
public class BDHolder<T> implements Supplier<T>
{
    private final Identifier id;
    private T value;
    private boolean bound;

    public BDHolder(Identifier id)
    {
        this.id = id;
    }

    public void bind(T value)
    {
        this.value = value;
        this.bound = true;
    }

    @Override
    public T get()
    {
        if (!bound)
        {
            throw new IllegalStateException("BDHolder " + id + " 尚未完成注册（注册发生在模组初始化的注册阶段）");
        }
        return value;
    }

    public boolean isBound()
    {
        return bound;
    }

    public Identifier getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "BDHolder[" + id + "]";
    }
}