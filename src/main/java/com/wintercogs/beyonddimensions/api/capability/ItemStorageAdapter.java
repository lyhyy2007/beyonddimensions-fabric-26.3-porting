package com.wintercogs.beyonddimensions.api.capability;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import com.wintercogs.beyonddimensions.api.transfer.ResourceHandler;
import com.wintercogs.beyonddimensions.api.transfer.item.ItemResource;
import com.wintercogs.beyonddimensions.api.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/** 移植垫片内部实现：把 Fabric 的 {@code Storage<ItemVariant>} 适配成 {@link ResourceHandler}。 */
final class ItemStorageAdapter implements ResourceHandler<ItemResource>
{
    private final Storage<ItemVariant> storage;
    private final SlottedStorage<ItemVariant> slotted;
    private final TransactionBridge bridge = new TransactionBridge();

    ItemStorageAdapter(Storage<ItemVariant> storage)
    {
        this.storage = storage;
        this.slotted = (storage instanceof SlottedStorage<ItemVariant> s) ? s : null;
    }

    @Nullable
    private StorageView<ItemVariant> view(int index)
    {
        if (slotted != null)
        {
            return (index >= 0 && index < slotted.getSlotCount()) ? slotted.getSlot(index) : null;
        }
        if (index != 0)
        {
            return null;
        }
        Iterator<StorageView<ItemVariant>> it = storage.iterator();
        return it.hasNext() ? it.next() : null;
    }

    @Override
    public int size()
    {
        return slotted != null ? slotted.getSlotCount() : 1;
    }

    @Override
    public @NotNull ItemResource getResource(int index)
    {
        StorageView<ItemVariant> v = view(index);
        if (v == null || v.isResourceBlank() || v.getResource().isBlank())
        {
            return ItemResource.EMPTY;
        }
        return ItemResource.of(v.getResource().toStack(1));
    }

    @Override
    public long getAmountAsLong(int index)
    {
        StorageView<ItemVariant> v = view(index);
        return v == null ? 0L : v.getAmount();
    }

    @Override
    public long getCapacityAsLong(int index, @NotNull ItemResource resource)
    {
        StorageView<ItemVariant> v = view(index);
        if (v == null)
        {
            return 0L;
        }
        if (v.isResourceBlank())
        {
            return v.getCapacity();
        }
        return v.getResource().matches(resource.toStack()) ? v.getCapacity() : 0L;
    }

    @Override
    public boolean isValid(int index, @NotNull ItemResource resource)
    {
        StorageView<ItemVariant> v = view(index);
        if (v == null)
        {
            return false;
        }
        return v.isResourceBlank() || v.getResource().matches(resource.toStack());
    }

    @Override
    public int insert(int index, @NotNull ItemResource resource, int amount, @NotNull TransactionContext transaction)
    {
        if (amount <= 0 || resource.isEmpty())
        {
            return 0;
        }
        ItemVariant variant = ItemVariant.of(resource.toStack());
        long inserted;
        if (slotted != null)
        {
            StorageView<ItemVariant> v = view(index);
            if (v == null)
            {
                return 0;
            }
            inserted = slotted.getSlot(index).insert(variant, amount, bridge.toFabric(transaction));
        }
        else
        {
            inserted = storage.insert(variant, amount, bridge.toFabric(transaction));
        }
        return (int) Math.max(0L, Math.min(inserted, amount));
    }

    @Override
    public int extract(int index, @NotNull ItemResource resource, int amount, @NotNull TransactionContext transaction)
    {
        if (amount <= 0 || resource.isEmpty())
        {
            return 0;
        }
        StorageView<ItemVariant> v = view(index);
        if (v == null || v.isResourceBlank() || !v.getResource().matches(resource.toStack()))
        {
            return 0;
        }
        long extracted = v.extract(v.getResource(), amount, bridge.toFabric(transaction));
        return (int) Math.max(0L, Math.min(extracted, amount));
    }
}