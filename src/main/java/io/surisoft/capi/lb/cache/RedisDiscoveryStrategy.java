package io.surisoft.capi.lb.cache;

import com.hazelcast.cluster.Address;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import io.surisoft.capi.lb.schema.CapiNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class RedisDiscoveryStrategy extends AbstractDiscoveryStrategy {


    private RedisTemplate redisTemplate;

    private final DiscoveryNode thisNode;
    private final ILogger logger;

    private CapiNode capiNode = new CapiNode();


    public RedisDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties, RedisTemplate redisTemplate) {
        super(logger, properties);
        this.redisTemplate = redisTemplate;
        this.thisNode = discoveryNode;
        this.logger = logger;
    }

    @Override
    public void start() {
        try {
            capiNode.setAddress(thisNode.getPrivateAddress().getHost());
            capiNode.setPort(thisNode.getPrivateAddress().getPort());
            redisTemplate.opsForHash().put(CapiNode.CLIENT_KEY, capiNode.getAddress(), capiNode);
        } catch (Exception e) {
            throw new IllegalStateException("Error while calling Redis. ", e);
        }
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        Iterable<CapiNode> nodeList = redisTemplate.opsForHash().values(CapiNode.CLIENT_KEY);
        return mapToDiscoveryNodes(nodeList);
    }

    private Iterable<DiscoveryNode> mapToDiscoveryNodes(Iterable<CapiNode> nodeList) {
        Collection<DiscoveryNode> discoveredNodes = new ArrayList<>();
        for (CapiNode node : nodeList) {
            try {
                Address address = new Address(node.getAddress(), node.getPort());
                discoveredNodes.add(new SimpleDiscoveryNode(address));
            } catch (UnknownHostException e) {
                log.error(e.getMessage(), e);
            }
        }
        return discoveredNodes;
    }

    @Override
    public void destroy() {
        try {
            if (isMember()) {
                redisTemplate.opsForHash().delete(CapiNode.CLIENT_KEY, capiNode.getAddress(), capiNode);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error while calling to Redis", e);
        }
    }

    private boolean isMember() {
        return thisNode != null;
    }
}
