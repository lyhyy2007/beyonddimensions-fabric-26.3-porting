package com.wintercogs.beyonddimensions.api.capability;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import com.wintercogs.beyonddimensions.api.fluid.FluidStack;
import com.wintercogs.beyonddimensions.api.transfer.ResourceHandler;
import com.wintercogs.beyonddimensions.api.transfer.fluid.FluidResource;
import com.wintercogs.beyonddimensions.api.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/** 移植垫片内部实现：把 Fabric 的 {@code Storage<FluidVariant>} 适配成 {@link ResourceHandler}。 */
final class FluidStorageAdapter implements ResourceHandler<FluidResource>
{
    private final Storage<FluidVariant> storage;
    private final SlottedStorage<FluidVariant> slotted;
    private final TransactionBridge bridge = new TransactionBridge();

    FluidStorageAdapter(Storage<FluidVariant> storage)
    {
        this.storage = storage;
        this.slotted = (storage instanceof SlottedStorage<FluidVariant> s) ? s : null;
    }

    @Nullable
    private StorageView<FluidVariant> view(int index)
    {
        if (slotted != null)
        {
            return (index >= 0 && index < slotted.getSlotCount()) ? slotted.getSlot(index) : null;
        }
        if (index != 0)
        {
            return null;
        }
        Iterator<StorageView<FluidVariant>> it = storage.iterator();
        return it.hasNext() ? it.next() : null;
    }

    @Override
    public int size()
    {
        return slotted != null ? slotted.getSlotCount() : 1;
    }

    @Override
    public @NotNull FluidResource getResource(int index)
    {
        StorageView<FluidVariant> v = view(index);
        if (v == null || v.isResourceBlank() || v.getResource().isBlank())
        {
            return FluidResource.EMPTY;
        }
        FluidVariant variant = v.getResource();
        return FluidResource.of(new FluidStack(variant.getFluid(), 1, variant.getComponentsPatch()));
    }

    @Override
    public long getAmountAsLong(int index)
    {
        StorageView<FluidVariant> v = view(index);
        return v == null ? 0L : v.getAmount();
    }

    @Override
    public long getCapacityAsLong(int index, @NotNull FluidResource resource)
    {
        StorageView<FluidVariant> v = view(index);
        if (v == null)
        {
            return 0L;
        }
        if (v.isResourceBlank())
        {
            return v.getCapacity();
        }
        return matches(v.getResource(), resource) ? v.getCapacity() : 0L;
    }

    @Override
    public boolean isValid(int index, @NotNull FluidResource resource)
    {
        StorageView<FluidVariant> v = view(index);
        if (v == null)
        {
            return false;
        }
        return v.isResourceBlank() || matches(v.getResource(), resource);
    }

    private static boolean matches(FluidVariant variant, FluidResource resource)
    {
        return variant.getFluid() == resource.getFluid()
                && variant.getComponentsPatch().equals(resource.toStack().getComponentsPatch());
    }

    @Override
    public int insert(int index, @NotNull FluidResource resource, int amount, @NotNull TransactionContext transaction)
    {
        if (amount <= 0 || resource.isEmpty())
        {
            return 0;
        }
        FluidVariant variant = FluidVariant.of(resource.getFluid(), resource.toStack().getComponentsPatch());
        long inserted;
        if (slotted != null)
        {
            StorageView<FluidVariant> v = view(index);
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
    public int extract(int index, @NotNull FluidResource resource, int amount, @NotNull TransactionContext transaction)
    {
        if (amount <= 0 || resource.isEmpty())
        {
            return 0;
        }
        StorageView<FluidVariant> v = view(index);
        if (v == null || v.isResourceBlank() || !matches(v.getResource(), resource))
        {
            return 0;
        }
        long extracted = v.extract(v.getResource(), amount, bridge.toFabric(transaction));
        return (int) Math.max(0L, Math.min(extracted, amount));
    }
}