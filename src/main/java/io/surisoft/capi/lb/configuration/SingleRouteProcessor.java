/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *     contributor license agreements.  See the NOTICE file distributed with
 *     this work for additional information regarding copyright ownership.
 *     The ASF licenses this file to You under the Apache License, Version 2.0
 *     (the "License"); you may not use this file except in compliance with
 *     the License.  You may obtain a copy of the License at
 *          http://www.apache.org/licenses/LICENSE-2.0
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package io.surisoft.capi.lb.configuration;

import io.surisoft.capi.lb.cache.RunningApiManager;
import io.surisoft.capi.lb.schema.Api;
import io.surisoft.capi.lb.utils.RouteUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class SingleRouteProcessor extends RouteBuilder {

    private RouteUtils routeUtils;

    private Api api;

    private RunningApiManager runningApiManager;

    public SingleRouteProcessor(CamelContext camelContext, Api api, RouteUtils routeUtils, RunningApiManager runningApiManager) {
        super(camelContext);
        this.api = api;
        this.routeUtils = routeUtils;
        this.runningApiManager = runningApiManager;
    }

    @Override
    public void configure() {
        List<RouteDefinition> routeDefinitionList = routeDefinition(api);
        for(RouteDefinition routeDefinition : routeDefinitionList) {
            String routeId = api.getName() + "||" + UUID.randomUUID().toString();
            routeUtils.buildOnExceptionDefinition(routeDefinition, false, false, false, routeId);
             if(api.isFailoverEnabled()) {
                routeDefinition
                        .loadBalance()
                        .failover(api.getMaximumFailoverAttempts(), false, api.isRoundRobinEnabled(), false)
                        .to(routeUtils.buildEndpoints(api))
                        .end()
                        .routeId(routeId);
            } else {
                 routeDefinition
                         .loadBalance()
                         .roundRobin()
                         .to(routeUtils.buildEndpoints(api))
                         .end()
                         .routeId(routeId);
             }
            runningApiManager.runApi(routeId, api, routeUtils.getMethodFromRoute(routeDefinition));
        }
    }

    private List<RouteDefinition> routeDefinition(Api api) {
        List<RouteDefinition> routeDefinitionList = new ArrayList<>();
        switch (api.getHttpMethod()) {
            case ALL:
                routeDefinitionList.add(rest().get(routeUtils.buildFrom(api) + "?matchOnUriPrefix=" + api.isMatchOnUriPrefix()).route());
                routeDefinitionList.add(rest().post(routeUtils.buildFrom(api) + "?matchOnUriPrefix=" + api.isMatchOnUriPrefix()).route());
                routeDefinitionList.add(rest().put(routeUtils.buildFrom(api) + "?matchOnUriPrefix=" + api.isMatchOnUriPrefix()).route());
                routeDefinitionList.add(rest().delete(routeUtils.buildFrom(api) + "?matchOnUriPrefix=" + api.isMatchOnUriPrefix()).route());
                break;
            case GET:
                routeDefinitionList.add(rest().get(routeUtils.buildFrom(api) + "?matchOnUriPrefix=" + api.isMatchOnUriPrefix()).route());
                break;
            case POST:
                routeDefinitionList.add(rest().post(routeUtils.buildFrom(api) + "?matchOnUriPrefix=" + api.isMatchOnUriPrefix()).route());
                break;
            case PUT:
                routeDefinitionList.add(rest().put(routeUtils.buildFrom(api) + "?matchOnUriPrefix=" + api.isMatchOnUriPrefix()).route());
                break;
            case DELETE:
                routeDefinitionList.add(rest().delete(routeUtils.buildFrom(api) + "?matchOnUriPrefix=" + api.isMatchOnUriPrefix()).route());
                break;
        }
        return routeDefinitionList;
    }
}
