package io.surisoft.capi.lb.cache;

import com.hazelcast.config.*;
import com.hazelcast.spi.properties.ClusterProperty;
import io.surisoft.capi.lb.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public class CapiCacheConfig extends Config {

    public CapiCacheConfig(boolean redisDiscoveryEnabled, RedisTemplate redisTemplate) {
        super();
        if(redisDiscoveryEnabled) {
            getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
            setProperty(ClusterProperty.DISCOVERY_SPI_ENABLED.getName(), "true");
            setProperty("connection-timeout-seconds", "30");

            DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new RedisDiscoveryStrategyFactory(redisTemplate));
            getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);
        }

        MapConfig mapConfig = new MapConfig()
                .setName("running-api-configuration")
                .setTimeToLiveSeconds(-1);
        mapConfig.getEvictionConfig()
                .setMaxSizePolicy(MaxSizePolicy.FREE_HEAP_SIZE)
                .setSize(20000)
                .setEvictionPolicy(EvictionPolicy.LRU);
        setInstanceName("running-api-instance")
                .setClusterName(Constants.APPLICATION_NAME)
                .addMapConfig(mapConfig);
    }
}