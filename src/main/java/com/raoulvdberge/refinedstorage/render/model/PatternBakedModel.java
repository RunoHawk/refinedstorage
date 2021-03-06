package com.raoulvdberge.refinedstorage.render.model;

import com.google.common.collect.ImmutableList;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.item.PatternItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PatternBakedModel extends DelegateBakedModel {
    public PatternBakedModel(IBakedModel base) {
        super(base);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList() {
            @Nullable
            @Override
            public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
                if (entity != null) {
                    CraftingPattern pattern = PatternItem.fromCache(entity.world, stack);

                    if (canDisplayOutput(stack, pattern)) {
                        ItemStack outputToRender = pattern.getOutputs().get(0);

                        return Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(outputToRender, world, entity);
                    }
                }

                return super.getModelWithOverrides(model, stack, world, entity);
            }

            @Override
            public ImmutableList<ItemOverride> getOverrides() {
                return base.getOverrides().getOverrides();
            }
        };
    }

    public static boolean canDisplayOutput(ItemStack patternStack, CraftingPattern pattern) {
        if (pattern.isValid() && pattern.getOutputs().size() == 1) {
            for (ICraftingPatternRenderHandler renderHandler : API.instance().getPatternRenderHandlers()) {
                if (renderHandler.canRenderOutput(patternStack)) {
                    return true;
                }
            }
        }

        return false;
    }
}
