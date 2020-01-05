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

package thecodex6824.thaumicaugmentation.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thecodex6824.thaumicaugmentation.api.block.property.IDirectionalBlock;
import thecodex6824.thaumicaugmentation.api.tile.IImpetusGate;
import thecodex6824.thaumicaugmentation.common.block.prefab.BlockTABase;
import thecodex6824.thaumicaugmentation.common.block.trait.IItemBlockProvider;
import thecodex6824.thaumicaugmentation.common.tile.TileImpetusGate;
import thecodex6824.thaumicaugmentation.common.tile.trait.IBreakCallback;
import thecodex6824.thaumicaugmentation.common.util.BitUtil;

public class BlockImpetusGate extends BlockTABase implements IDirectionalBlock, IItemBlockProvider {

    protected static final AxisAlignedBB DOWN_BOX = new AxisAlignedBB(0.3125, 0.46875, 0.3125, 0.6875, 1.0, 0.6875);
    protected static final AxisAlignedBB EAST_BOX = new AxisAlignedBB(0.0, 0.3125, 0.3125, 0.53125, 0.6875, 0.6875);
    protected static final AxisAlignedBB NORTH_BOX = new AxisAlignedBB(0.3125, 0.3125, 0.46875, 0.6875, 0.6875, 1.0);
    protected static final AxisAlignedBB SOUTH_BOX = new AxisAlignedBB(0.3125, 0.3125, 0.0, 0.6875, 0.6875, 0.53125);
    protected static final AxisAlignedBB UP_BOX = new AxisAlignedBB(0.3125, 0.0, 0.3125, 0.6875, 0.53125, 0.6875);
    protected static final AxisAlignedBB WEST_BOX = new AxisAlignedBB(1.0, 0.3125, 0.3125, 0.46875, 0.6875, 0.6875);
    
    public BlockImpetusGate() {
        super(Material.IRON);
        setHardness(3.0F);
        setResistance(35.0F);
        setSoundType(SoundType.METAL);
        setDefaultState(getDefaultState().withProperty(IDirectionalBlock.DIRECTION, EnumFacing.UP));
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IDirectionalBlock.DIRECTION);
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(IDirectionalBlock.DIRECTION, EnumFacing.byIndex(BitUtil.getBits(meta, 0, 3)));
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(IDirectionalBlock.DIRECTION).getIndex();
    }
    
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {

        return getDefaultState().withProperty(IDirectionalBlock.DIRECTION, facing);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(IDirectionalBlock.DIRECTION, rot.rotate(state.getValue(IDirectionalBlock.DIRECTION)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(IDirectionalBlock.DIRECTION)));
    }
    
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        EnumFacing dir = state.getValue(IDirectionalBlock.DIRECTION).getOpposite();
        if (!world.getBlockState(pos.offset(dir)).isSideSolid(world, pos.offset(dir), dir)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
            EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        
        if (world.isRemote)
            return true;
        else {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof IImpetusGate) {
                IImpetusGate gate = (IImpetusGate) tile;
                if (player.isSneaking()) {
                    gate.cycleMode();
                    player.sendStatusMessage(new TextComponentTranslation(
                            gate.isInRedstoneMode() ? "thaumicaugmentation.text.gate_redstone" : "thaumicaugmentation.text.gate_manual"), false);
                }
                else {
                    gate.cycleLimit();
                    player.sendStatusMessage(new TextComponentTranslation(
                            "thaumicaugmentation.text.gate_cycle_limit", gate.getLimit()), false);
                }
                
                return true;
            }
            else
                return false;
        }
    }
    
    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        BlockPos test = pos.offset(side.getOpposite());
        IBlockState state = world.getBlockState(test);
        return state.isSideSolid(world, test, side) || state.getBlock().canPlaceTorchOnTop(state, world, test);
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(IDirectionalBlock.DIRECTION)) {
            case DOWN:  return DOWN_BOX;
            case EAST:  return EAST_BOX;
            case NORTH: return NORTH_BOX;
            case SOUTH: return SOUTH_BOX;
            case WEST:  return WEST_BOX;
            case UP:
            default:    return UP_BOX;
        }
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IBreakCallback)
            ((IBreakCallback) tile).onBlockBroken();
        
        super.breakBlock(world, pos, state);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    @Nullable
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileImpetusGate();
    }
    
    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
}