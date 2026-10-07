package com.wintercogs.beyonddimensions.registry;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 模组自有注册入口（Fabric 原生版）。
 * <p>
 * 原先是 NeoForge 的 {@code DeferredRegister}；现在直接封装原版 {@code Registry.register}。
 * 之所以仍保留「延迟」，是因为上游存在循环依赖：<b>流体必须先于方块实例化</b>
 * （{@code LiquidBlock} 的构造器要拿 {@link net.minecraft.world.level.material.Fluid} 实例），
 * 而方块物品又要拿方块实例。因此 {@link #flush()} 分两轮执行：
 * <ol>
 *     <li>按 {@link #PHASE_FLUID} → {@code PHASE_BLOCK} → {@code PHASE_DATA_COMPONENT} → {@code PHASE_ITEM}
 *         → {@code PHASE_BLOCK_ENTITY} 的顺序<b>实例化</b>；</li>
 *     <li>再按同样顺序<b>写入注册表</b>。</li>
 * </ol>
 */
public final class BDRegistry
{
    private BDRegistry() {}

    public static final int PHASE_FLUID = 0;
    public static final int PHASE_BLOCK = 1;
    public static final int PHASE_DATA_COMPONENT = 2;
    public static final int PHASE_ITEM = 3;
    public static final int PHASE_BLOCK_ENTITY = 4;
    public static final int PHASE_TAB = 5;

    private record Pending(int phase, int seq, Runnable instantiate, Runnable register) {}

    private static final List<Pending> PENDING = new ArrayList<>();
    private static int sequence = 0;
    private static boolean flushed = false;

    public static Identifier id(String path)
    {
        return Identifier.fromNamespaceAndPath(BDConstants.MODID, path);
    }

    /** 菜单界面：无跨依赖，直接注册。 */
    @FunctionalInterface
    public interface MenuFactory<T extends AbstractContainerMenu>
    {
        T create(int windowId, Inventory inventory, FriendlyByteBuf data);
    }

    private static final StreamCodec<RegistryFriendlyByteBuf, byte[]> BYTE_ARRAY_CODEC = StreamCodec.of(
            (buf, value) -> buf.writeByteArray(value == null ? new byte[0] : value),
            buf -> buf.readByteArray());

    /** 注册菜单类型：载荷为「一段额外字节」，供菜单构造器读取 BlockPos / 手别等参数。 */
    public static <T extends AbstractContainerMenu> MenuType<T> registerMenu(String name, MenuFactory<T> factory)
    {
        MenuType<T> type = new ExtendedMenuType<T, byte[]>((syncId, inventory, data) ->
                factory.create(syncId, inventory, new FriendlyByteBuf(
                        Unpooled.wrappedBuffer(data == null ? new byte[0] : data))), BYTE_ARRAY_CODEC);
        return Registry.register(BuiltInRegistries.MENU, id(name), type);
    }

    // ---------------------------------------------------------------- 延迟注册

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> BDHolder<T> defer(int phase, Registry<?> registry, String name, Supplier<? extends T> factory)
    {
        Identifier identifier = id(name);
        BDHolder<T> holder = new BDHolder<>(identifier);
        PENDING.add(new Pending(phase, sequence++,
                () -> holder.bind(factory.get()),
                () -> Registry.register((Registry) registry, identifier, holder.get())));
        return holder;
    }

    public static <T extends Item> BDItemHolder<T> deferItem(String name, Function<Item.Properties, T> factory)
    {
        Identifier identifier = id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, identifier);
        BDItemHolder<T> holder = new BDItemHolder<>(identifier);
        PENDING.add(new Pending(PHASE_ITEM, sequence++,
                () -> holder.bind(factory.apply(new Item.Properties().setId(key))),
                () -> Registry.register(BuiltInRegistries.ITEM, identifier, holder.get())));
        return holder;
    }

    public static BDItemHolder<BlockItem> deferBlockItem(String name, Supplier<? extends Block> block)
    {
        Identifier identifier = id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, identifier);
        BDItemHolder<BlockItem> holder = new BDItemHolder<>(identifier);
        PENDING.add(new Pending(PHASE_ITEM, sequence++,
                () -> holder.bind(new BlockItem(block.get(), new Item.Properties().setId(key))),
                () -> Registry.register(BuiltInRegistries.ITEM, identifier, holder.get())));
        return holder;
    }

    public static <T extends Block> BDBlockHolder<T> deferBlock(String name,
                                                                Function<BlockBehaviour.Properties, T> factory,
                                                                Supplier<BlockBehaviour.Properties> properties)
    {
        Identifier identifier = id(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, identifier);
        BDBlockHolder<T> holder = new BDBlockHolder<>(identifier);
        PENDING.add(new Pending(PHASE_BLOCK, sequence++,
                () -> {
                    BlockBehaviour.Properties props = properties.get();
                    props.setId(key);
                    holder.bind(factory.apply(props));
                },
                () -> Registry.register(BuiltInRegistries.BLOCK, identifier, holder.get())));
        return holder;
    }

    /** 立即注册（无依赖项，例如 FluidType 这类不进入原版注册表的对象由调用方自行持有）。 */
    public static <T> T registerNow(Registry<T> registry, String name, T value)
    {
        return Registry.register(registry, id(name), value);
    }

    /** 供数据组件等「注册表类型为通配、值为具体泛型」的场景使用。 */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> BDHolder<T> deferIn(int phase, Registry<?> registry, String name, Supplier<? extends T> factory)
    {
        return defer(phase, registry, name, factory);
    }

    public static boolean isFlushed()
    {
        return flushed;
    }

    /** 由 {@code BeyondDimensions#init()} 在排队完成后调用一次。 */
    public static void flush()
    {
        if (flushed)
        {
            return;
        }
        flushed = true;

        List<Pending> ordered = new ArrayList<>(PENDING);
        ordered.sort(Comparator.comparingInt(Pending::phase).thenComparingInt(Pending::seq));

        for (Pending pending : ordered)
        {
            pending.instantiate().run();
        }
        for (Pending pending : ordered)
        {
            pending.register().run();
        }
        PENDING.clear();
    }
}
