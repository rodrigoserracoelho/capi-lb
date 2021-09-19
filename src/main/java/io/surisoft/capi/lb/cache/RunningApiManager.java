package io.surisoft.capi.lb.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.surisoft.capi.lb.schema.Api;
import io.surisoft.capi.lb.schema.HttpMethod;
import io.surisoft.capi.lb.schema.RunningApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Component
@Slf4j
public class RunningApiManager {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private RunningApiListener runningApiListener;

    @PostConstruct
    public void addListener() {
        getCachedApi().addEntryListener(runningApiListener, true );
    }

    private IMap<String, RunningApi> getCachedApi() {
        return hazelcastInstance.getMap(CacheConstants.RUNNING_API_IMAP_NAME);
    }

    public void runApi(String routeId, Api api, HttpMethod method) {
        if(getRunningApiByRouteId(routeId) == null) {
            log.info("Adding API definition to run time cache.");
            RunningApi runningApi = new RunningApi();
            runningApi.setApiId(api.getId());
            runningApi.setContext(api.getContext());
            runningApi.setName(api.getName());
            runningApi.setRouteId(routeId);
            runningApi.setHttpMethod(method);
            getCachedApi().put(routeId, runningApi);
        } else {
            log.info("API Definition already Cached for run time.");
        }

    }

    public RunningApi getRunningApiByRouteId(String routeId) {
        if(getCachedApi().containsKey(routeId)) {
            return getCachedApi().get(routeId);
        } else {
            return null;
        }
    }

    public Collection<RunningApi> getRunningApi() {
        return getCachedApi().values();
    }

    public String getMemberPublicAddress() {
        return hazelcastInstance.getConfig().getNetworkConfig().getPublicAddress();
    }
}
