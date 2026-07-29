package terrails.stattinkerer.api.health;

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
     * @return the amount of health the player will have in the beginning
     * Stored in the player file so health can be reset in case of changes in config
     */
    int getStartingHealth();

    /**
     * @return the highest amount of health a player can have
     * Stored in the player file so health can be reset in case of changes in config
     */
    int getMaxHealth();

    /**
     * @return the lowest amount of health a player can have
     * Stored in the player file so health can be reset in case of changes in config
     */
    int getMinHealth();

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
     *
     * @param amount the amount of health
     * @return success
     */
    boolean setHealth(ServerPlayer playerEntity, int amount);

    /**
     * Increases/Decreases players health
     *
     * @param amount the amount of health to increase (+) or decrease (-)
     * @return success
     */
    boolean addHealth(ServerPlayer playerEntity, int amount);

    /**
     * Increases/Decreases players health
     *
     * @param amount    the amount of health to increase (+) or decrease (-)
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
     * Copies the data from a provided HealthManager instance
     *
     * @param other the other HealthManager instance to copy from
     */
    void copyFrom(HealthManager other);
}
