package terrails.stattinkerer.feature.health;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.config.Configuration;

public class HealthManagerImpl implements HealthManager {

    /** The amount of health the player has */
    private int amount = 0;
    /** Current health "threshold", can be a negative
     * number if the health is not being removed */
    private int threshold = 0;

    /** The amount of health the player will have in the beginning */
    private int start = 0;
    /** The highest amount of health a player can have */
    private int max = 0;
    /** The lowest amount of health a player can have */
    private int min = 0;

    @Override
    public void update(ServerPlayer playerEntity) {
        if (!playerEntity.isAlive() || !Configuration.HEALTH.systemEnabled.get()) {
            return;
        }

        if (hasConfigChanged()) {
            this.reset(playerEntity);
            return;
        }

        int prevThreshold = this.threshold;
        int prevHealthAmount = this.amount;

        if (!HealthHelper.hasModifier(playerEntity)) {
            this.amount = this.start;
        }

        Integer nearestThreshold = Configuration.HEALTH.thresholds.get().floor(this.amount);
        this.threshold = nearestThreshold != null ? (Math.abs(nearestThreshold) <= this.amount ? Math.abs(nearestThreshold) : nearestThreshold) : 0;

        if (this.start == this.max && this.min <= 0 && !Configuration.HEALTH.hardcoreMode.get()) {
            this.amount = this.max;
        } else {
            int min = Math.max(this.min, this.threshold);
            this.amount = Mth.clamp(this.amount, min, this.max);
        }

        if (prevHealthAmount != this.amount) {
            this.setHealth(playerEntity, this.amount);
        }

        if (prevThreshold != this.threshold && prevThreshold != 0 && this.threshold > 0) {
            HealthHelper.playerMessage(playerEntity, "health.stattinkerer.threshold", Math.abs(this.threshold));
        }
    }

    @Override
    public boolean setHealth(ServerPlayer playerEntity, int amount) {
        if (!playerEntity.isAlive() || !Configuration.HEALTH.systemEnabled.get()) {
            return false;
        }

        amount = Mth.clamp(amount, this.min, this.max);
        if (this.amount != amount) {
            HealthHelper.addModifier(playerEntity, amount);
            // Player health does not seem to decrease even when maxHealth is lower than health
            if (playerEntity.getHealth() > playerEntity.getMaxHealth()) {
                playerEntity.setHealth(playerEntity.getMaxHealth());
            } else playerEntity.setHealth(playerEntity.getHealth() + Math.max(amount - this.amount, 0));
            this.amount = amount;
            this.update(playerEntity);
            return true;
        }

        return false;
    }

    @Override
    public boolean addHealth(ServerPlayer playerEntity, int amount) {
        return this.addHealth(playerEntity, amount, true);
    }

    @Override
    public boolean addHealth(ServerPlayer playerEntity, int amount, boolean threshold) {
        if (!playerEntity.isAlive() || !Configuration.HEALTH.systemEnabled.get()) {
            return false;
        }

        int prevThreshold = this.threshold;
        int prevHealth = this.amount;
        amount = Mth.clamp(this.amount + amount, this.min, this.max);

        int min = Math.max(this.min, threshold ? this.threshold : 0);
        if (amount < min || amount > this.max) {
            return false;
        }

        boolean ret = this.setHealth(playerEntity, amount);

        // In case that the threshold value changed, do not overwrite the message it sent
        if (ret && prevThreshold == this.threshold) {
            String key = (this.amount - prevHealth) > 0 ? "health.stattinkerer.item_add" : "health.stattinkerer.item_lose";
            HealthHelper.playerMessage(playerEntity, key, Math.abs((this.amount - prevHealth)));
        }

        return ret;
    }

    @Override
    public void reset(ServerPlayer playerEntity) {
        this.threshold = 0;
        this.start = Configuration.HEALTH.startingHealth.get();
        this.max = Configuration.HEALTH.maxHealth.get();
        this.min = Configuration.HEALTH.minHealth.get();
        this.amount = (int) playerEntity.getMaxHealth();
        this.setHealth(playerEntity, this.start);
    }

    @Override
    public int getHealth() {
        return this.amount;
    }

    @Override
    public int getThreshold() {
        return this.threshold;
    }

    @Override
    public boolean isHighest() {
        return this.amount == this.max;
    }

    @Override
    public boolean isLowest() {
        return this.amount == this.min;
    }

    @Override
    public boolean isHealthRemovable() {
        int min = Math.max(this.min, Math.abs(this.threshold));
        return (this.min > 0 && this.amount > min) || Configuration.HEALTH.hardcoreMode.get();
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putInt(HealthHelper.TAG_ADDITIONAL_HEALTH, this.amount - 20);
        tag.putInt(HealthHelper.TAG_MAX_HEALTH, this.max);
        tag.putInt(HealthHelper.TAG_MIN_HEALTH, this.min);
        tag.putInt(HealthHelper.TAG_STARTING_HEALTH, this.start);
        tag.putInt(HealthHelper.TAG_HEALTH_THRESHOLD, this.threshold);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if (tag.contains(HealthHelper.TAG_STARTING_HEALTH)) {
            this.start = tag.getInt(HealthHelper.TAG_STARTING_HEALTH);
        }

        if (tag.contains(HealthHelper.TAG_ADDITIONAL_HEALTH)) {
            this.amount = tag.getInt(HealthHelper.TAG_ADDITIONAL_HEALTH) + 20;
        }

        if (tag.contains(HealthHelper.TAG_MAX_HEALTH)) {
            this.max = tag.getInt(HealthHelper.TAG_MAX_HEALTH);
        }

        if (tag.contains(HealthHelper.TAG_MIN_HEALTH)) {
            this.min = tag.getInt(HealthHelper.TAG_MIN_HEALTH);
        }

        if (tag.contains(HealthHelper.TAG_HEALTH_THRESHOLD)) {
            this.threshold = tag.getInt(HealthHelper.TAG_HEALTH_THRESHOLD);
        }
    }

    private boolean hasConfigChanged() {
        for (Configuration.OnChangeReset value : Configuration.HEALTH.onChangeReset.get()) {
            switch (value) {
                case MIN_HEALTH -> {
                    if (Configuration.HEALTH.minHealth.get() != this.min) {
                        return true;
                    }
                }
                case MAX_HEALTH -> {
                    if (Configuration.HEALTH.maxHealth.get() != this.max) {
                        return true;
                    }
                }
                case STARTING_HEALTH -> {
                    if (Configuration.HEALTH.startingHealth.get() != this.start) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
