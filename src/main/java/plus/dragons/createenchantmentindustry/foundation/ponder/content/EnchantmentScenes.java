package plus.dragons.createenchantmentindustry.foundation.ponder.content;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.BeltItemElement;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.instruction.EmitParticlesInstruction;
import com.simibubi.create.foundation.utility.Pointing;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.util.NBTSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createenchantmentindustry.content.contraptions.enchanting.enchanter.BlazeEnchanterBlock;
import plus.dragons.createenchantmentindustry.content.contraptions.enchanting.enchanter.BlazeEnchanterBlockEntity;
import plus.dragons.createenchantmentindustry.content.contraptions.enchanting.printer.PrinterBlockEntity;
import plus.dragons.createenchantmentindustry.entry.CeiBlocks;
import plus.dragons.createenchantmentindustry.entry.CeiFluids;
import plus.dragons.createenchantmentindustry.entry.CeiItems;

public class EnchantmentScenes {
    public static void disenchant(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("disenchant", "Disenchanting");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(.68f);
        scene.world.setKineticSpeed(util.select.everywhere(), 32f);
        scene.showBasePlate();
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(0, 1, 0, 6, 4, 6), Direction.DOWN);

        scene.overlay.showText(100)
                .text("All received items will have their enchantments removed and the removed enchantments will be converted to liquid experience for storage.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(1, 1, 1));

        // Must propagatePipeChange first or it won't work correctly
        scene.world.propagatePipeChange(util.grid.at(2, 1, 5));

        BlockPos beltStart = util.grid.at(6, 1, 1);
        List<ItemStack> items = Stream.of(Items.NETHERITE_SWORD, Items.IRON_PICKAXE, Items.DIAMOND_CHESTPLATE, Items.ENCHANTED_BOOK, Items.LEATHER_HELMET, Items.GOLDEN_BOOTS, Items.WOODEN_AXE).map(Item::getDefaultInstance).toList();
        for (var item : items) {
            enchantRandomly(item);
            ElementLink<EntityElement> itemEntity = scene.world.createItemEntity(util.vector.centerOf(6, 4, 1), util.vector.of(0, 0, 0), item);
            scene.idle(13);
            scene.world.modifyEntity(itemEntity, Entity::discard);
            scene.world.createItemOnBelt(beltStart, Direction.DOWN, item);
            scene.idle(2);
        }

        scene.idle(80);

        scene.overlay.showText(100)
                .text("Players standing on the disenchanter will be quickly washed away their experience level, and the washed away experience value will be converted into liquid experience for storage")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(1, 1, 1));

        scene.idle(120);
    }

    public static void transformBlazeBurner(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("transform", "Using Enchanting Guide");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(0, 1, 0, 2, 1, 2), Direction.DOWN);

        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(1, 1, 1), Pointing.DOWN).whileSneaking().rightClick()
                .withItem(CeiItems.ENCHANTING_GUIDE.asStack()), 30);
        scene.overlay.showText(50)
                .text("Right-click the Blaze Burner with an Enchanting Guide in hand when sneaking to transform it to a Blaze Enchanter.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(1, 1, 1));
        scene.idle(30);
        scene.world.setBlock(util.grid.at(1, 1, 1), CeiBlocks.BLAZE_ENCHANTER.getDefaultState(), false);
        scene.idle(25);

        scene.overlay.showText(100)
                .text("To make Blaze Enchanter work, Enchanting Guide must be configured first. " +
                        "Right-clicking Blaze Enchanter or right-clicking Enchanting Guide in hands can open configuration panel.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.centerOf(1, 1, 1));
        scene.idle(105);

        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(1, 1, 1), Pointing.DOWN).whileSneaking().rightClick().withWrench(), 30);
        scene.overlay.showText(50)
                .text("To retrieve the enchanting guide, right-click the Blaze Enchanter with wrench when sneaking.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.centerOf(1, 1, 1));
        scene.idle(30);
        scene.world.setBlock(util.grid.at(1, 1, 1), AllBlocks.BLAZE_BURNER.getDefaultState().setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING), false);
        scene.idle(25);

        scene.world.setBlock(util.grid.at(1, 1, 1), CeiBlocks.BLAZE_ENCHANTER.getDefaultState(), false);
        scene.idle(10);

        scene.world.setBlock(util.grid.at(1, 1, 1), AllBlocks.BLAZE_BURNER.getDefaultState().setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.KINDLED), false);
        scene.overlay.showText(60)
                .text("You can replace the Enchanting Guide by holding it directly in your hand and right-clicking on the Blaze Enchanter.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.centerOf(1, 1, 1));
        scene.idle(60);
    }

    public static void enchant(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("enchant", "Enchanting");
        scene.configureBasePlate(0, 0, 8);
        scene.scaleSceneView(.60f);
        scene.world.setKineticSpeed(util.select.everywhere(), 0);
        scene.showBasePlate();
        scene.idle(5);
        scene.world.setKineticSpeed(util.select.everywhere(), 80F);
        scene.world.setKineticSpeed(util.select.fromTo(0, 2, 7, 5, 2, 7), -80F);
        scene.world.setKineticSpeed(util.select.fromTo(7, 2, 2, 7, 2, 7), -80F);
        scene.world.setBlock(util.grid.at(1,2,0),CeiBlocks.BLAZE_ENCHANTER.getDefaultState().setValue(BlazeEnchanterBlock.HEAT_LEVEL,
                BlazeEnchanterBlock.HeatLevel.KINDLED),false);
        scene.world.setBlock(util.grid.at(0,2,6),CeiBlocks.BLAZE_ENCHANTER.getDefaultState().setValue(BlazeEnchanterBlock.HEAT_LEVEL,
                BlazeEnchanterBlock.HeatLevel.KINDLED),false);
        scene.world.setBlock(util.grid.at(6,2,7),CeiBlocks.BLAZE_ENCHANTER.getDefaultState().setValue(BlazeEnchanterBlock.HEAT_LEVEL,
                BlazeEnchanterBlock.HeatLevel.KINDLED),false);
        scene.world.setBlock(util.grid.at(7,2,1),CeiBlocks.BLAZE_ENCHANTER.getDefaultState().setValue(BlazeEnchanterBlock.HEAT_LEVEL,
                BlazeEnchanterBlock.HeatLevel.KINDLED),false);
        scene.world.modifyBlockEntity(util.grid.at(1, 2, 0), BlazeEnchanterBlockEntity.class, be -> {
            be.setTargetItem(enchantingGuide(Enchantments.THORNS, 1));
			TransferUtil.insertFluid(be.getFluidStorage(null),new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), 1000));
        });
        scene.world.modifyBlockEntity(util.grid.at(0, 2, 6), BlazeEnchanterBlockEntity.class, be -> {
            be.setTargetItem(enchantingGuide(Enchantments.THORNS, 2));
			TransferUtil.insertFluid(be.getFluidStorage(null),new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), 1000));
		});
        scene.world.modifyBlockEntity(util.grid.at(7, 2, 1), BlazeEnchanterBlockEntity.class, be -> {
            be.setTargetItem(enchantingGuide(Enchantments.UNBREAKING, 1));
			TransferUtil.insertFluid(be.getFluidStorage(null),new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), 1000));
		});
        scene.world.modifyBlockEntity(util.grid.at(3, 1, 3), CreativeFluidTankBlockEntity.class, be -> ((CreativeFluidTankBlockEntity.CreativeSmartFluidTank) be.getTankInventory())
                .setContainedFluid(new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), 1000)));
        // Must propagatePipeChange first or it won't work correctly
        scene.world.propagatePipeChange(util.grid.at(2, 1, 2));
        scene.world.propagatePipeChange(util.grid.at(5, 1, 2));
        scene.world.propagatePipeChange(util.grid.at(5, 1, 5));
        scene.world.propagatePipeChange(util.grid.at(5, 1, 2));
        scene.world.showSection(util.select.fromTo(0, 1, 0, 7, 4, 7), Direction.DOWN);

        scene.idle(5);
        List<ItemStack> items = Stream.of(Items.NETHERITE_CHESTPLATE, Items.BOOK, Items.ENCHANTED_BOOK, Items.DIAMOND_SWORD, Items.DIAMOND_SWORD, Items.LEATHER_CHESTPLATE).map(Item::getDefaultInstance).toList();
        for (var item : items) {
            BlockPos beltStart = util.grid.at(7, 2, 0);
            ElementLink<EntityElement> itemEntity = scene.world.createItemEntity(util.vector.centerOf(7, 5, 0), util.vector.of(0, 0, 0), item);
            scene.idle(13);
            scene.world.modifyEntity(itemEntity, Entity::discard);
            scene.world.createItemOnBelt(beltStart, Direction.DOWN, item);
            scene.idle(10);
        }

        scene.overlay.showText(60)
                .text("Blaze Enchanter can enchant your items.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(1, 2, 0));
        scene.idle(70);

        scene.overlay.showText(60)
                .text("However, it cannot work on books...")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(1, 2, 0));
        scene.idle(70);

        scene.overlay.showText(60)
                .text("... nor can it add incompatible enchantments to your items.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(1, 2, 0));
        scene.idle(70);

        scene.overlay.showText(60)
                .text("Blaze Enchanter can upgrade an existing enchantment to a higher level if feasible")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(0, 2, 6));
        scene.idle(70);

        scene.idle(100);
    }

    public static void hyperEnchant(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("hyper_enchant", "Hyper-enchanting");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(.68f);
        scene.showBasePlate();
        scene.idle(5);

        scene.world.modifyBlockEntity(util.grid.at(3, 1, 1), CreativeFluidTankBlockEntity.class, be -> ((CreativeFluidTankBlockEntity.CreativeSmartFluidTank) be.getTankInventory())
                .setContainedFluid(new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), 1000)));
        scene.world.modifyBlockEntity(util.grid.at(3, 1, 3), CreativeFluidTankBlockEntity.class, be -> ((CreativeFluidTankBlockEntity.CreativeSmartFluidTank) be.getTankInventory())
                .setContainedFluid(new FluidStack(CeiFluids.HYPER_EXPERIENCE.get().getSource(), 1000)));
        scene.world.propagatePipeChange(util.grid.at(2, 1, 1));
        scene.world.propagatePipeChange(util.grid.at(2, 1, 3));
        scene.world.showSection(util.select.fromTo(0, 1, 0, 4, 3, 4), Direction.DOWN);

        scene.overlay.showText(60)
                .text("This is liquid hyper experience")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(3, 3, 3));
        scene.overlay.showOutline(PonderPalette.BLUE, new Object(), util.select.fromTo(3, 1, 3, 3, 3, 3), 80);
        scene.idle(90);

        scene.world.setKineticSpeed(util.select.everywhere(), 128F);
        scene.idle(25);

        scene.world.setBlock(util.grid.at(1,2,1),CeiBlocks.BLAZE_ENCHANTER.getDefaultState().setValue(BlazeEnchanterBlock.HEAT_LEVEL,
                BlazeEnchanterBlock.HeatLevel.KINDLED),false);
        scene.world.setBlock(util.grid.at(1,2,3),CeiBlocks.BLAZE_ENCHANTER.getDefaultState().setValue(BlazeEnchanterBlock.HEAT_LEVEL,
                BlazeEnchanterBlock.HeatLevel.SEETHING),false);
        scene.idle(15);

        scene.overlay.showOutline(PonderPalette.BLUE, new Object(), util.select.position(1, 2, 3), 80);
        scene.overlay.showText(80)
                .text("Hyper experience can make the Blaze Enchanter into seething state, and the level of the enchantment produced in this state will be one level higher than the set enchantment.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.centerOf(1, 2, 3));
        scene.idle(90);

        scene.overlay.showText(60)
                .text("Enchantment with level cap of 1 level cannot be upgraded to level 2 in hyper-enchant.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.centerOf(1, 2, 3));
        scene.idle(60);
    }

    public static void handleExperienceNugget(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("absorb_experience_nugget", "Converting Experience Nugget to Liquid");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(.68f);
        scene.world.setKineticSpeed(util.select.everywhere(), 32f);
        scene.world.setKineticSpeed(util.select.fromTo(0, 1, 2, 2, 1, 4), -32f);
        scene.showBasePlate();
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(0, 1, 0, 4, 1, 4), Direction.DOWN);

        var poses = Lists.newArrayList(util.grid.at(2, 1, 0), util.grid.at(0, 1, 2), util.grid.at(2, 1, 4), util.grid.at(4, 1, 2));
        for (var pos : poses) {
            var item = AllItems.EXP_NUGGET.asStack(64);
            ElementLink<EntityElement> itemEntity = scene.world.createItemEntity(Vec3.atCenterOf(pos.above(3)), util.vector.of(0, 0, 0), item);
            scene.idle(13);
            scene.world.modifyEntity(itemEntity, Entity::discard);
            scene.world.createItemOnBelt(pos, Direction.DOWN, item);
            scene.idle(10);
        }

        scene.overlay.showText(60)
                .text("Experience nugget can be absorbed by disenchanter.")
                .placeNearTarget()
                .pointAt(util.vector.topOf(2, 1, 2));

        scene.idle(60);
    }

    public static void dropExperienceNugget(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("drop_experience_nugget", "Maybe A Exp-farm?");
        scene.configureBasePlate(0, 0, 3);
        scene.scaleSceneView(1.2f);
        scene.showBasePlate();
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(0, 1, 0, 2, 1, 2), Direction.DOWN);
        BlockPos deployerPos = util.grid.at(1, 1, 2);
        Selection deployerSelection = util.select.position(deployerPos);

        ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
        scene.idle(10);
        scene.world.modifyBlockEntityNBT(deployerSelection, DeployerBlockEntity.class, nbt -> {
            nbt.put("HeldItem", NBTSerializer.serializeNBT(sword));
            nbt.putString("mode", "PUNCH");
        });
        scene.idle(30);
        scene.world.setKineticSpeed(util.select.everywhere(), 32f);

        scene.addKeyframe();
        ElementLink<EntityElement> sheep = scene.world.createEntity(w -> {
            Sheep entity = EntityType.SHEEP.create(w);
            entity.setColor(DyeColor.PINK);
            Vec3 p = util.vector.topOf(util.grid.at(1, 0, 0));
            entity.setPos(p.x, p.y, p.z);
            entity.xo = p.x;
            entity.yo = p.y;
            entity.zo = p.z;
			WalkAnimationState animation = entity.walkAnimation;
			animation.update(-animation.position(), 1);
			animation.setSpeed(1);
            entity.yRotO = 210;
            entity.setYRot(210);
            entity.yHeadRotO = 210;
            entity.yHeadRot = 210;
            return entity;
        });
        scene.idle(5);
        scene.world.moveDeployer(deployerPos, 1, 25);
        scene.idle(26);
        scene.world.modifyEntity(sheep, Entity::discard);
        scene.effects.emitParticles(util.vector.topOf(deployerPos.west(2))
                        .add(0, -.25, 0),
                EmitParticlesInstruction.Emitter.withinBlockSpace(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.PINK_WOOL.defaultBlockState()),
                        util.vector.of(0, 0, 0)),
                25, 1);
        scene.world.moveDeployer(deployerPos, -1, 25);

        scene.overlay.showText(60)
                .text("When mob is killed by deployer, experience nuggets are dropped.")
                .placeNearTarget()
                .pointAt(util.vector.centerOf(1, 1, 2));
        scene.overlay.showOutline(PonderPalette.BLUE, new Object(), util.select.position(1, 1, 2), 50);

        scene.world.flapFunnel(deployerPos.north(), true);
        scene.world.createItemEntity(util.vector.centerOf(deployerPos.west())
                .subtract(0, .45, 0), util.vector.of(-0.1, 0, 0), new ItemStack(Items.PINK_WOOL));
        scene.idle(10);

        scene.world.flapFunnel(deployerPos.north(), true);
        scene.world.createItemEntity(util.vector.centerOf(deployerPos.west())
                .subtract(0, .45, 0), util.vector.of(-0.1, 0, 0), new ItemStack(Items.MUTTON));
        scene.idle(10);

        scene.world.flapFunnel(deployerPos.north(), true);
        scene.world.createItemEntity(util.vector.centerOf(deployerPos.west())
                .subtract(0, .45, 0), util.vector.of(-0.1, 0, 0), AllItems.EXP_NUGGET.asStack());
        scene.idle(40);
    }

    public static void crushingWheelTweak(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("crushing_wheel_tweak", "We call it inefficiency...");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(.75f);
        scene.showBasePlate();
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(0, 1, 0, 4, 3, 4), Direction.DOWN);
        Selection crushWheelSelection = util.select.position(util.grid.at(4, 3, 3));
        scene.world.setKineticSpeed(util.select.everywhere(), 128f);
        scene.world.setKineticSpeed(crushWheelSelection, -128f);

        scene.addKeyframe();
        ElementLink<EntityElement> sheep = scene.world.createEntity(w -> {
            Sheep entity = EntityType.SHEEP.create(w);
            entity.setColor(DyeColor.PINK);
            Vec3 p = util.vector.topOf(util.grid.at(4, 3, 2));
            entity.setPos(p.x, p.y, p.z);
            entity.xo = p.x;
            entity.yo = p.y;
            entity.zo = p.z;
			WalkAnimationState animation = entity.walkAnimation;
			animation.update(-animation.position(), 1);
			animation.setSpeed(1);
            entity.yRotO = 210;
            entity.setYRot(210);
            entity.yHeadRotO = 210;
            entity.yHeadRot = 210;
            return entity;
        });
        scene.idle(10);
        scene.world.modifyEntity(sheep, Entity::discard);
        scene.effects.emitParticles(util.vector.topOf(util.grid.at(4, 3, 2))
                        .add(0, -.25, 0),
                EmitParticlesInstruction.Emitter.withinBlockSpace(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.RED_CONCRETE.defaultBlockState()),
                        util.vector.centerOf(0, 0, 0)),
                25, 1);
        ElementLink<EntityElement> itemEntity = scene.world.createItemEntity(util.vector.blockSurface(util.grid.at(4, 2, 2), Direction.DOWN, 0), util.vector.of(0, 0, 0), new ItemStack(Items.PINK_WOOL));
        ElementLink<EntityElement> itemEntity2 = scene.world.createItemEntity(util.vector.blockSurface(util.grid.at(4, 2, 2), Direction.DOWN, 0), util.vector.of(0, 0, 0), new ItemStack(AllItems.EXP_NUGGET.get()));
        scene.idle(13);
        scene.world.modifyEntity(itemEntity, Entity::discard);
        scene.world.createItemOnBelt(util.grid.at(4, 1, 2), Direction.DOWN, new ItemStack(Items.PINK_WOOL));
        scene.idle(3);
        scene.world.modifyEntity(itemEntity2, Entity::discard);
        scene.world.createItemOnBelt(util.grid.at(4, 1, 2), Direction.DOWN, new ItemStack(AllItems.EXP_NUGGET.get()));
        scene.idle(10);
        scene.overlay.showText(60)
                .text("The Crushing Wheel has a chance of dropping a very small amount of experience nugget when it kills a creature.")
                .placeNearTarget()
                .pointAt(util.vector.centerOf(4, 3, 2));
        scene.idle(60);
    }


    public static void handleExperienceBottle(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("experience_bottle", "Dealing with Bottle o' Enchanting");
        scene.configureBasePlate(0, 0, 6);
        scene.scaleSceneView(.68f);
        scene.world.setKineticSpeed(util.select.everywhere(), 16f);
        scene.showBasePlate();
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(0, 1, 0, 5, 4, 5), Direction.DOWN);

        // Must propagatePipeChange first or it won't work correctly
        scene.world.propagatePipeChange(util.grid.at(3, 1, 1));
        scene.world.propagatePipeChange(util.grid.at(2, 3, 3));

        var item = Items.EXPERIENCE_BOTTLE.getDefaultInstance();
        item.setCount(64);
        BlockPos beltStart = util.grid.at(4, 1, 0);
        ElementLink<EntityElement> itemEntity = scene.world.createItemEntity(util.vector.centerOf(4, 5, 0), util.vector.of(0, 0, 0), item);
        scene.idle(13);
        scene.world.modifyEntity(itemEntity, Entity::discard);
        scene.world.createItemOnBelt(beltStart, Direction.DOWN, item);

        scene.idle(10);

        scene.overlay.showText(80)
                .text("Bottle o' Enchanting can be emptied at Item Drain.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(2, 1, 0));

        scene.idle(80);

        scene.overlay.showText(80)
                .text("Bottle o' Enchanting also can be manufactured by Spout as well.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(0, 3, 3));

        var item2 = Items.GLASS_BOTTLE.getDefaultInstance();
        BlockPos beltStart2 = util.grid.at(0, 1, 1);
        ElementLink<EntityElement> itemEntity2 = scene.world.createItemEntity(util.vector.centerOf(0, 5, 1), util.vector.of(0, 0, 0), item2);
        scene.idle(13);
        scene.world.modifyEntity(itemEntity2, Entity::discard);
        ElementLink<BeltItemElement> beltItem = scene.world.createItemOnBelt(beltStart2, Direction.DOWN, item2);
        Selection spoutS = util.select.position(0, 3, 3);
        BlockPos spoutPos = util.grid.at(0, 3, 3);
        scene.idle(60);
        scene.world.stallBeltItem(beltItem, true);
        scene.world.modifyBlockEntityNBT(spoutS, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(10);
        scene.world.removeItemsFromBelt(spoutPos.below(2));
        scene.world.createItemOnBelt(spoutPos.below(2), Direction.UP, Items.EXPERIENCE_BOTTLE.getDefaultInstance());

        scene.idle(70);
    }

    public static void copy(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("copy", "Using Printer");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(.68f);
        scene.world.setKineticSpeed(util.select.everywhere(), 32f);
        scene.showBasePlate();
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(0, 1, 0, 6, 3, 6), Direction.DOWN);

        scene.overlay.showControls(new InputWindowElement(util.vector.centerOf(2, 3, 2), Pointing.DOWN).rightClick()
                .withItem(Items.ENCHANTED_BOOK.getDefaultInstance()), 40);
        scene.world.modifyBlockEntity(util.grid.at(2, 1, 5), CreativeFluidTankBlockEntity.class, be -> ((CreativeFluidTankBlockEntity.CreativeSmartFluidTank) be.getTankInventory())
                .setContainedFluid(new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), 1000)));
        scene.world.modifyBlockEntity(util.grid.at(2, 3, 2), PrinterBlockEntity.class, be ->
				TransferUtil.insertFluid(be.getFluidStorage(null),new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), 3000)));
        scene.overlay.showText(60)
                .text("Liquid Experience is required to duplicate enchanted books.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.centerOf(2, 3, 2));
        scene.idle(60);

        scene.world.modifyBlockEntity(util.grid.at(2, 1, 5), CreativeFluidTankBlockEntity.class, be -> ((CreativeFluidTankBlockEntity.CreativeSmartFluidTank) be.getTankInventory())
                .setContainedFluid(new FluidStack(CeiFluids.HYPER_EXPERIENCE.get().getSource(), 1000)));
        scene.world.modifyBlockEntity(util.grid.at(2, 3, 2), PrinterBlockEntity.class, be -> {
			TransferUtil.clearStorage(be.getFluidStorage(null));
			TransferUtil.insertFluid(be.getFluidStorage(null),new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), 3000));
		});
        scene.overlay.showText(100)
                .text("If the enchantment on the enchantment book you are copying has a level that exceeds its maximum level, then you will need Hyper Experience.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.centerOf(2, 3, 2));
        scene.idle(100);

        var item = Items.BOOK.getDefaultInstance();
        BlockPos beltStart = util.grid.at(6, 1, 2);
        ElementLink<BeltItemElement> beltItem = scene.world.createItemOnBelt(beltStart, Direction.DOWN, item);
        Selection copier = util.select.position(2, 3, 2);
        BlockPos copierPos = util.grid.at(2, 3, 2);
        scene.idle(60);
        scene.world.stallBeltItem(beltItem, true);
        scene.world.modifyBlockEntityNBT(copier, PrinterBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 100));
        scene.idle(95);
        scene.world.removeItemsFromBelt(copierPos.below(2));
        scene.world.createItemOnBelt(copierPos.below(2), Direction.UP, Items.ENCHANTED_BOOK.getDefaultInstance());

        scene.idle(40);

        scene.overlay.showControls(new InputWindowElement(util.vector.centerOf(2, 3, 2), Pointing.DOWN).rightClick()
                .withItem(Items.WRITTEN_BOOK.getDefaultInstance()), 40);
        scene.world.modifyBlockEntity(util.grid.at(2, 1, 5), CreativeFluidTankBlockEntity.class, be -> ((CreativeFluidTankBlockEntity.CreativeSmartFluidTank) be.getTankInventory())
                .setContainedFluid(new FluidStack(CeiFluids.INK.get().getSource(), 1000)));
        scene.world.modifyBlockEntity(util.grid.at(2, 3, 2), PrinterBlockEntity.class, be ->
		{
			TransferUtil.clearStorage(be.getFluidStorage(null));
			TransferUtil.insertFluid(be.getFluidStorage(null),new FluidStack(CeiFluids.INK.get().getSource(), 3000));
		});
        scene.overlay.showText(60)
                .text("Ink is required to duplicate written books.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.centerOf(2, 3, 2));
        scene.idle(60);

        item = Items.BOOK.getDefaultInstance();
        beltStart = util.grid.at(6, 1, 2);
        beltItem = scene.world.createItemOnBelt(beltStart, Direction.DOWN, item);
        copier = util.select.position(2, 3, 2);
        copierPos = util.grid.at(2, 3, 2);
        scene.idle(60);
        scene.world.stallBeltItem(beltItem, true);
        scene.world.modifyBlockEntityNBT(copier, PrinterBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 100));
        scene.idle(95);
        scene.world.removeItemsFromBelt(copierPos.below(2));
        scene.world.createItemOnBelt(copierPos.below(2), Direction.UP, Items.WRITTEN_BOOK.getDefaultInstance());
        scene.idle(50);

        scene.world.modifyBlockEntity(util.grid.at(2, 1, 5), CreativeFluidTankBlockEntity.class, be -> ((CreativeFluidTankBlockEntity.CreativeSmartFluidTank) be.getTankInventory())
                .setContainedFluid(new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), 1000)));
        scene.world.modifyBlockEntity(util.grid.at(2, 3, 2), PrinterBlockEntity.class, be ->
		{
			TransferUtil.clearStorage(be.getFluidStorage(null));
			TransferUtil.insertFluid(be.getFluidStorage(null),new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), 3000));
		});
        scene.overlay.showControls(new InputWindowElement(util.vector.centerOf(2, 3, 2), Pointing.DOWN).rightClick()
                .withItem(Items.NAME_TAG.getDefaultInstance()), 40);
        scene.overlay.showText(60)
                .text("Name Tag and Train Schedule can also be copied.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.centerOf(2, 3, 2));
        scene.idle(60);

        item = Items.DIAMOND_SWORD.getDefaultInstance();
        beltStart = util.grid.at(6, 1, 2);
        beltItem = scene.world.createItemOnBelt(beltStart, Direction.DOWN, item);
        copier = util.select.position(2, 3, 2);
        copierPos = util.grid.at(2, 3, 2);
        scene.idle(60);
        scene.world.stallBeltItem(beltItem, true);
        scene.world.modifyBlockEntityNBT(copier, PrinterBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 100));
        scene.idle(95);
        scene.world.removeItemsFromBelt(copierPos.below(2));
        scene.world.createItemOnBelt(copierPos.below(2), Direction.UP, Items.DIAMOND_SWORD.getDefaultInstance());

        scene.overlay.showText(70)
                .text("If you set the name tag as the print target, you can use the printer to name the item.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.centerOf(2, 3, 2));
        scene.idle(70);
    }

    public static void leak(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("leak", "Oh no! It's leaking!");
        scene.configureBasePlate(0, 0, 5);
        scene.scaleSceneView(.5f);
        scene.world.setKineticSpeed(util.select.everywhere(), 0f);
        scene.showBasePlate();
        scene.idle(5);

        scene.world.showSection(util.select.fromTo(3, 1, 3, 4, 4, 4), Direction.DOWN);
        scene.idle(5);
        scene.world.modifyBlockEntity(util.grid.at(3, 1, 3), FluidTankBlockEntity.class,
				be -> TransferUtil.insertFluid(be.getFluidStorage(null),new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), 48000)));
        scene.overlay.showText(40)
                .text("I have a tank full of experience")
                .placeNearTarget()
                .pointAt(util.vector.topOf(2, 4, 2));
        scene.idle(50);

        scene.world.showSection(util.select.fromTo(0, 1, 0, 4, 4, 2), Direction.DOWN);
        scene.world.showSection(util.select.fromTo(0, 1, 3, 2, 4, 4), Direction.DOWN);

        scene.idle(5);
        scene.overlay.showText(40)
                .text("I have an open-ended pipe")
                .placeNearTarget()
                .pointAt(util.vector.topOf(3, 4, 0));
        scene.idle(50);

        scene.world.setKineticSpeed(util.select.everywhere(), 128f);
        // Must propagatePipeChange first or it won't work correctly
        scene.world.propagatePipeChange(util.grid.at(0, 1, 3));
        scene.world.propagatePipeChange(util.grid.at(3, 1, 0));
        scene.world.propagatePipeChange(util.grid.at(3, 4, 0));
        scene.idle(80);
        scene.overlay.showText(40)
                .text("Ugh!")
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .pointAt(util.vector.topOf(3, 4, 0));
        scene.overlay.showOutline(PonderPalette.RED, new Object(), util.select.position(3, 4, 0), 40);
        scene.idle(50);
        scene.overlay.showText(80)
                .text("Don't worry, the leaked liquid experience will turn into experience orbs. Players can also stand at the opening of the pipe to absorb experience.") // We do not use PonderLocalization. For registerText only
                .colored(PonderPalette.GREEN)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector.topOf(3, 4, 0));
        scene.overlay.showOutline(PonderPalette.GREEN, new Object(), util.select.position(3, 4, 0), 40);
        scene.idle(90);

    }

    private static void enchantItem(ItemStack itemStack, Enchantment enchantment, int level) {
		if (enchantment == Enchantments.MENDING) {
            return; // 禁止附加经验修补附魔
        }
        var m = EnchantmentHelper.getEnchantments(itemStack);
        m.put(enchantment, level);
        EnchantmentHelper.setEnchantments(m, itemStack);
    }

    private static void enchantRandomly(ItemStack itemStack) {
        if (itemStack.is(Items.ENCHANTED_BOOK)) {
            return; // 禁止随机附加经验修补附魔
        } else EnchantmentHelper.enchantItem(RandomSource.create(), itemStack, 30, true);
    }

    private static ItemStack enchantingGuide(Enchantment enchantment, int level) {
		if (enchantment == Enchantments.MENDING) {
            scene.overlay.showText(40)
                .text("Mending is not allowed")
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .pointAt(util.vector.topOf(3, 4, 0));
			return;
        }
        var ret = CeiItems.ENCHANTING_GUIDE.asStack();
        ret.getOrCreateTag().putInt("index", 0);
        var book = Items.ENCHANTED_BOOK.getDefaultInstance();
        EnchantmentHelper.setEnchantments(Map.of(enchantment, level), book);
        ret.getOrCreateTag().put("target", NBTSerializer.serializeNBT(book));
        return ret;
    }
}
