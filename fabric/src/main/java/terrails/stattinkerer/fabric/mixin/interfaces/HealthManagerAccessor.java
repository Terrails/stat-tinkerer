package terrails.stattinkerer.fabric.mixin.interfaces;

import terrails.stattinkerer.api.health.HealthManager;

import java.util.Optional;

public interface HealthManagerAccessor {

    Optional<HealthManager> stattinkerer$getHealthManager();
}
