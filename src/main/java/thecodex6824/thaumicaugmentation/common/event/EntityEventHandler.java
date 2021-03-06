/**
 *  Thaumic Augmentation
 *  Copyright (c) 2019 TheCodex6824.
 *
 *  This file is part of Thaumic Augmentation.
 *
 *  Thaumic Augmentation is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Thaumic Augmentation is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Thaumic Augmentation.  If not, see <https://www.gnu.org/licenses/>.
 */

package thecodex6824.thaumicaugmentation.common.event;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thaumcraft.common.entities.EntityFluxRift;
import thecodex6824.thaumicaugmentation.api.TAConfig;
import thecodex6824.thaumicaugmentation.api.ThaumicAugmentationAPI;
import thecodex6824.thaumicaugmentation.api.entity.CapabilityPortalState;
import thecodex6824.thaumicaugmentation.api.entity.PortalStateManager;
import thecodex6824.thaumicaugmentation.api.world.TADimensions;

@EventBusSubscriber(modid = ThaumicAugmentationAPI.MODID)
public class EntityEventHandler {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!TAConfig.disableEmptiness.getValue() && event.getWorld().provider.getDimension() == TADimensions.EMPTINESS.getId() && event.getEntity().getClass() == EntityFluxRift.class)
            event.setCanceled(true);
        else if (event.getEntity().hasCapability(CapabilityPortalState.PORTAL_STATE, null) && 
                event.getEntity().getCapability(CapabilityPortalState.PORTAL_STATE, null).isInPortal()) {
            
            PortalStateManager.markEntityInPortal(event.getEntity());
        }
    }
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == Phase.END)
            PortalStateManager.tick();
    }
    
}
