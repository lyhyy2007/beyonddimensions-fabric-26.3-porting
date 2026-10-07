package com.wintercogs.beyonddimensions.mixin;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.world.level.material.Fluid;
import com.wintercogs.beyonddimensions.client.init.BDFluidModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

/**
 * 26.3 原版 {@code FluidStateModelSet.bake} 只烘培水与岩浆（NeoForge 用注册事件扩展它）。
 * 这里在返回值上补进本模组登记的自定义流体模型。
 */
@Mixin(FluidStateModelSet.class)
public class FluidStateModelSetMixin
{
    @Inject(method = "bake", at = @At("RETURN"), cancellable = true)
    private static void beyonddimensions$addCustomFluidModels(MaterialBaker baker,
                                                              CallbackInfoReturnable<Map<Fluid, FluidModel>> cir)
    {
        var entries = BDFluidModels.entries();
        if (entries.isEmpty())
        {
            return;
        }

        ModelDebugName debugName = () -> BDConstants.MODID + ":fluid";

        Map<Fluid, FluidModel> models = new HashMap<>(cir.getReturnValue());
        for (BDFluidModels.Entry entry : entries)
        {
            try
            {
                models.put(entry.fluid(), entry.model().bake(baker, debugName));
            }
            catch (Throwable ignored)
            {
                // 单个流体烘培失败不应影响整体模型加载
            }
        }
        cir.setReturnValue(models);
    }
}
