package terrails.stattinkerer.neoforge.feature.health;

import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import terrails.stattinkerer.feature.health.HealthManagerImpl;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;

public class HealthManagerNeoForgeImpl extends HealthManagerImpl implements INBTSerializable<CompoundTag> {

    @Override
    public CompoundTag serializeNBT(@NotNull Provider provider) {
        var tag = new CompoundTag();
        super.serialize(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull Provider provider, @NotNull CompoundTag tag) {
        super.deserialize(tag);
    }
}
