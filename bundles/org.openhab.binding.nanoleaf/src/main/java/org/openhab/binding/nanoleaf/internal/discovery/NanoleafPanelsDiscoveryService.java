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
package org.openhab.binding.nanoleaf.internal.discovery;

import static org.openhab.binding.nanoleaf.internal.NanoleafBindingConstants.CONFIG_PANEL_ID;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.nanoleaf.internal.NanoleafBindingConstants;
import org.openhab.binding.nanoleaf.internal.NanoleafControllerListener;
import org.openhab.binding.nanoleaf.internal.NanoleafHandlerFactory;
import org.openhab.binding.nanoleaf.internal.handler.NanoleafControllerHandler;
import org.openhab.binding.nanoleaf.internal.model.ControllerInfo;
import org.openhab.binding.nanoleaf.internal.model.Layout;
import org.openhab.binding.nanoleaf.internal.model.PanelLayout;
import org.openhab.binding.nanoleaf.internal.model.PositionDatum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NanoleafPanelsDiscoveryService} is responsible for discovering the individual
 * panels connected to the controller.
 *
 * @author Martin Raepple - Initial contribution
 */
@NonNullByDefault
public class NanoleafPanelsDiscoveryService extends AbstractDiscoveryService implements NanoleafControllerListener {

    private static final int SEARCH_TIMEOUT_SECONDS = 60;

    private final Logger logger = LoggerFactory.getLogger(NanoleafPanelsDiscoveryService.class);
    private final NanoleafControllerHandler bridgeHandler;

    /**
     * Constructs a new {@link NanoleafPanelsDiscoveryService} attached to the given bridge handler.
     *
     * @param nanoleafControllerHandler The bridge handler this discovery service is attached to
     */
    public NanoleafPanelsDiscoveryService(NanoleafControllerHandler nanoleafControllerHandler) {
        super(NanoleafHandlerFactory.SUPPORTED_THING_TYPES_UIDS, SEARCH_TIMEOUT_SECONDS, false);
        this.bridgeHandler = nanoleafControllerHandler;
    }

    @Override
    protected void startScan() {
        logger.debug("Starting Nanoleaf panel discovery");
        bridgeHandler.registerControllerListener(this);
    }

    @Override
    protected synchronized void stopScan() {
        logger.debug("Stopping Nanoleaf panel discovery");
        super.stopScan();
        bridgeHandler.unregisterControllerListener(this);
    }

    /**
     * Called by the controller handler with bridge and panel data
     *
     * @param bridge The controller
     * @param controllerInfo Panel data (and more)
     */
    @Override
    public void onControllerInfoFetched(ThingUID bridge, ControllerInfo controllerInfo) {
        logger.debug("Discover panels connected to controller with id {}", bridge.getAsString());
        final PanelLayout panelLayout = controllerInfo.getPanelLayout();
        @Nullable
        Layout layout = panelLayout.getLayout();

        if (layout != null && layout.getNumPanels() > 0) {
            @Nullable
            final List<PositionDatum> positionData = layout.getPositionData();
            if (positionData != null) {
                Iterator<PositionDatum> iterator = positionData.iterator();
                while (iterator.hasNext()) {
                    @Nullable
                    PositionDatum panel = iterator.next();
                    ThingUID newPanelThingUID = new ThingUID(NanoleafBindingConstants.THING_TYPE_LIGHT_PANEL, bridge,
                            Integer.toString(panel.getPanelId()));

                    final Map<String, Object> properties = new HashMap<>(1);
                    properties.put(CONFIG_PANEL_ID, panel.getPanelId());

                    DiscoveryResult newPanel = DiscoveryResultBuilder.create(newPanelThingUID).withBridge(bridge)
                            .withProperties(properties).withLabel("Light Panel " + panel.getPanelId())
                            .withRepresentationProperty(CONFIG_PANEL_ID).build();

                    logger.debug("Adding panel with id {} to inbox", panel.getPanelId());
                    thingDiscovered(newPanel);
                }
            } else {
                logger.debug("Couldn't add panels to inbox as layout position data was null");
            }

        } else {
            logger.info("No panels found or connected to controller");
        }
    }
}
