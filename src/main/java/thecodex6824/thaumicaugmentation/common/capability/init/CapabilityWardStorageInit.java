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

package thecodex6824.thaumicaugmentation.common.capability.init;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import thecodex6824.thaumicaugmentation.api.warded.IWardStorage;
import thecodex6824.thaumicaugmentation.api.warded.IWardStorageServer;

public final class CapabilityWardStorageInit {

    private CapabilityWardStorageInit() {}
    
    public static void init() {
        CapabilityManager.INSTANCE.register(IWardStorage.class, new Capability.IStorage<IWardStorage>() {
            
            @Override
            public void readNBT(Capability<IWardStorage> capability, IWardStorage instance, EnumFacing side, NBTBase nbt) {
                if (instance instanceof IWardStorageServer)
                    ((IWardStorageServer) instance).deserializeNBT((NBTTagCompound) nbt);
            }
            
            @Override
            public NBTBase writeNBT(Capability<IWardStorage> capability, IWardStorage instance, EnumFacing side) {
                if (instance instanceof IWardStorageServer)
                    return ((IWardStorageServer) instance).serializeNBT();
                else
                    return new NBTTagCompound();
            }
            
        }, () -> {
            throw new UnsupportedOperationException("Cannot create a default ward storage impl (create one for client or server side instead)");
        });
    }
    
}
