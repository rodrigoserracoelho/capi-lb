package io.surisoft.capi.lb.cache;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import io.surisoft.capi.lb.configuration.SingleRouteProcessor;
import io.surisoft.capi.lb.schema.Api;
import io.surisoft.capi.lb.schema.RunningApi;
import io.surisoft.capi.lb.utils.RouteUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RunningApiListener implements EntryAddedListener<String, RunningApi>, EntryUpdatedListener<String, RunningApi> {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private RouteUtils routeUtils;

    @Autowired
    private RunningApiManager runningApiManager;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void entryUpdated(EntryEvent<String, RunningApi> event) {
        if(event.getValue() != null) {
            RunningApi runningApi = event.getValue();
            /*if(runningApi.isRemoved() && runningApi.isDisabled()) {
                log.info( "API detected for suspension: {}", runningApi.getRouteId());
                camelUtils.suspendRoute(runningApi);
                camelUtils.addSuspendedRoute(runningApi);
            } else {
                log.info( "API detected for reactivation: {}", runningApi.getRouteId());
                camelUtils.suspendRoute(runningApi);
                camelUtils.addActiveRoute(runningApi);
            }*/
        }
    }

    @Override
    public void entryAdded(EntryEvent<String, RunningApi> entryEvent) {
        log.info("NEW ENTRY DETECTED: {}", entryEvent.getMember().localMember());
        if(!entryEvent.getMember().localMember()) {
            try {
                Api api = (Api) redisTemplate.opsForHash().get(Api.CLIENT_KEY, entryEvent.getValue().getApiId());
                camelContext.addRoutes(new SingleRouteProcessor(camelContext, api, routeUtils, runningApiManager));
            } catch (Exception e) {
               log.error(e.getMessage(), e);
            }
        } else {
            log.trace("New entry was deployed by this CAPI instance, skipping Camel Route.");
        }
    }
}
