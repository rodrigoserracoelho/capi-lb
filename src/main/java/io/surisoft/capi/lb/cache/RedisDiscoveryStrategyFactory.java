package io.surisoft.capi.lb.cache;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Map;

public class RedisDiscoveryStrategyFactory implements DiscoveryStrategyFactory {

    private RedisTemplate redisTemplate;

    public RedisDiscoveryStrategyFactory(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
        return RedisDiscoveryStrategy.class;
    }

    @Override
    public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger iLogger, Map<String, Comparable> map) {
        return new RedisDiscoveryStrategy(discoveryNode, iLogger, map, redisTemplate);
    }

    @Override
    public Collection<PropertyDefinition> getConfigurationProperties() {
        return null;
    }
}
