package terrails.stattinkerer.api.health;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public interface HealthManager {

    /**
     * @return the current amount of health the player has.
     * This might not be accurate if some other mod is
     * messing with the health system.
     */
    int getHealth();

    /**
     * @return the current health threshold the player achieved.
     * If value < 0, health is not going to be decreased on death
     * If value = 0, has not been set
     * If value > 0, health is not going to go below this value
     */
    int getThreshold();

    /**
     * @return is the health at the highest value possible
     */
    boolean isHighest();
    /**
     * @return is the health at the lowest value possible
     */
    boolean isLowest();

    /**
     * @return is the health possible to be decreased
     */
    boolean isHealthRemovable();

    /**
     * Changes the players health to the specified value
     * @param amount the amount of health
     * @return success
     */
    boolean setHealth(ServerPlayer playerEntity, int amount);

    /**
     * Increases/Decreases players health
     * @param amount the amount of health to increase (+) or decrease (-)
     * @return success
     */
    boolean addHealth(ServerPlayer playerEntity, int amount);

    /**
     * Increases/Decreases players health
     * @param amount the amount of health to increase (+) or decrease (-)
     * @param threshold should the method care about the threshold when decreasing
     * @return success
     */
    boolean addHealth(ServerPlayer playerEntity, int amount, boolean threshold);

    /**
     * Runs the default update method which checks if the current
     * health values are in range, checks the threshold and saves
     * data to the player file if it has been changed.
     */
    void update(ServerPlayer playerEntity);

    /**
     * Resets the whole manager to default values
     */
    void reset(ServerPlayer playerEntity);

    /**
     * Serializes the data to the given NBTTagCompound
     * @param compound the tag to which the data will be saved
     */
    void serialize(CompoundTag compound);

    /**
     * Reads the data from the given NBTTagCompound
     * @param compound the tag from which the data will be read
     */
    void deserialize(CompoundTag compound);
}
