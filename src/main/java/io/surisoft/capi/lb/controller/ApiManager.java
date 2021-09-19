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

package io.surisoft.capi.lb.controller;

import io.surisoft.capi.lb.cache.RunningApiManager;
import io.surisoft.capi.lb.configuration.SingleRouteProcessor;
import io.surisoft.capi.lb.schema.Api;
import io.surisoft.capi.lb.schema.RunningApi;
import io.surisoft.capi.lb.utils.RouteUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/manager/api")
@Slf4j
public class ApiManager {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RouteUtils routeUtils;

    @Autowired
    private RunningApiManager runningApiManager;

    @GetMapping(path = "/configured")
    public ResponseEntity<Iterable<Api>> getAllClients() {
        return new ResponseEntity<>(redisTemplate.opsForHash().values(Api.CLIENT_KEY), HttpStatus.OK);
    }

    @GetMapping(path = "/running")
    public ResponseEntity<Iterable<RunningApi>> getAllRunningApi() {
        return new ResponseEntity<>(runningApiManager.getRunningApi(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Api> createClient(@RequestBody Api api) {
        if(api.getId() != null) {
            log.info("Requesting to edit API");
        }
       if(redisTemplate.opsForHash().get(Api.CLIENT_KEY, api.getName()) != null) {
           return new ResponseEntity<>(HttpStatus.CONFLICT);
       }

       api.setId(UUID.randomUUID().toString());
       redisTemplate.opsForHash().put(Api.CLIENT_KEY, api.getName(), api);
       try {
           camelContext.addRoutes(new SingleRouteProcessor(camelContext, api, routeUtils, runningApiManager));
       } catch (Exception e) {
           log.error(e.getMessage(), e);
           redisTemplate.opsForHash().delete(Api.CLIENT_KEY, api);
           return new ResponseEntity<>(api, HttpStatus.PRECONDITION_FAILED);
       }
       return new ResponseEntity<>(api, HttpStatus.CREATED);
    }

    @PostMapping(path="/refresh/mapping")
    public ResponseEntity<Api> refreshMapping(@RequestBody Api api) {
        if(redisTemplate.opsForHash().get(Api.CLIENT_KEY, api.getName()) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        redisTemplate.opsForHash().put(Api.CLIENT_KEY, api.getName(), api);
        return new ResponseEntity<>(api, HttpStatus.CREATED);
    }

    @DeleteMapping(path="/{clientName}")
    public ResponseEntity<String> deleteClient(@PathVariable String clientName) {
        //camelCon
        //camelContext.getRoutes().remove()
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
