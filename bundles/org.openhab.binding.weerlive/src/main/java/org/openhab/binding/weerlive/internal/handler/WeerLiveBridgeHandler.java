/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.weerlive.internal.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

import static org.openhab.binding.weerlive.internal.WeerLiveBindingConstants.THING_TYPE_WEER_LIVE_API;

/**
 * The {@link WeerLiveBridgeHandler} is responsible for accessing the Weerlive.nl API.
 *
 * @author Jeroen Stiekema - Initial contribution
 */
@NonNullByDefault
public class WeerLiveBridgeHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(WeerLiveBridgeHandler.class);

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.singleton(THING_TYPE_WEER_LIVE_API);


    public WeerLiveBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (logger.isDebugEnabled()) {
            logger.debug("WeerLiveBridge handle command: {}", command);
        }
    }
}
