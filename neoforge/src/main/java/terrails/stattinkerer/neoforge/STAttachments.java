package terrails.stattinkerer.neoforge;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.feature.health.HealthManagerImpl;

import java.util.function.Supplier;

public class STAttachments {

    static final DeferredRegister<AttachmentType<?>> DATA_ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CStatTinkerer.MOD_ID);

    public static final Supplier<AttachmentType<HealthManager>> HEALTH_DATA = DATA_ATTACHMENTS.register(
            "health", () -> AttachmentType
                    .builder(() -> (HealthManager) (new HealthManagerImpl()))
                    .serialize(new IAttachmentSerializer<CompoundTag, HealthManager>() {
                        @Override
                        public HealthManager read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                            HealthManager manager = new HealthManagerImpl();
                            manager.deserialize(tag);
                            return manager;
                        }

                        @Override
                        public @Nullable CompoundTag write(HealthManager manager, HolderLookup.Provider provider) {
                            CompoundTag tag = new CompoundTag();
                            manager.serialize(tag);
                            return tag;
                        }
                    }).copyOnDeath().build()
    );
}
