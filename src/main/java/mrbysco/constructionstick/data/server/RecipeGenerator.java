package mrbysco.constructionstick.data.server;

import mrbysco.constructionstick.data.server.recipe.SmithingApplyUpgradeRecipeBuilder;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.items.template.ItemUpgradeTemplate;
import mrbysco.constructionstick.registry.ModDataComponents;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RecipeGenerator extends RecipeProvider {
	public RecipeGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	protected void buildRecipes(RecipeOutput output, HolderLookup.Provider provider) {
		stickRecipe(output, ModItems.STICK_WOODEN.get(), Inp.fromTag(Tags.Items.RODS_WOODEN));
		stickRecipe(output, ModItems.STICK_COPPER.get(), Inp.fromTag(ItemTags.STONE_TOOL_MATERIALS));
		stickRecipe(output, ModItems.STICK_IRON.get(), Inp.fromTag(Tags.Items.INGOTS_IRON));
		stickRecipe(output, ModItems.STICK_DIAMOND.get(), Inp.fromTag(Tags.Items.GEMS_DIAMOND));
		stickRecipe(output, ModItems.STICK_NETHERITE.get(), Inp.fromTag(Tags.Items.INGOTS_NETHERITE));

		templateRecipe(output, ModItems.TEMPLATE_ANGEL.get(), Inp.fromTag(Tags.Items.FEATHERS), Inp.fromTag(Tags.Items.INGOTS_GOLD));
		templateRecipe(output, ModItems.TEMPLATE_DESTRUCTION.get(), Inp.fromItem(Items.TNT), Inp.fromItem(Items.DIAMOND_PICKAXE));

		templateUpgradeRecipe(output, ModItems.TEMPLATE_ANGEL, Inp.fromTag(Tags.Items.FEATHERS), ModDataComponents.ANGEL, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_DESTRUCTION, Inp.fromTag(Tags.Items.STORAGE_BLOCKS_REDSTONE), ModDataComponents.DESTRUCTION, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_UNBREAKABLE, Inp.fromTag(Tags.Items.OBSIDIANS_CRYING), ModDataComponents.UNBREAKABLE, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_BATTERY, Inp.fromTag(Tags.Items.STORAGE_BLOCKS_COPPER), ModDataComponents.BATTERY, 0);
	}

	private void stickRecipe(RecipeOutput output, ItemLike stick, Inp material) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, stick)
				.define('X', material.ingredient())
				.define('#', Tags.Items.RODS_WOODEN)
				.pattern("  X")
				.pattern(" # ")
				.pattern("#  ")
				.unlockedBy("has_item", inventoryTrigger(material.predicate()))
				.save(output);
	}

	private <T> void templateUpgradeRecipe(RecipeOutput output, DeferredItem<? extends ItemUpgradeTemplate> template,
	                                       Inp item1, Supplier<DataComponentType<T>> component, T defaultValue) {
		for (DeferredItem<? extends ItemStick> stickHolder : ModItems.STICKS) {
			ItemStack stack = stickHolder.toStack();
			stack.set(component, defaultValue);
			SmithingApplyUpgradeRecipeBuilder.smithing(Ingredient.of(template.get()), Ingredient.of(stickHolder),
							item1.ingredient(), RecipeCategory.TOOLS, stack, template.get().getRegistryName())
					.unlocks("has_template", has(template.get()))
					.unlocks("has_stick", has(template.get()))
					.unlocks("has_addition", inventoryTrigger(item1.predicate()))
					.save(output, template.getId().withPrefix("smithing_upgrade/" + stickHolder.getId().getPath() + "_with_"));
		}

	}

	private void templateRecipe(RecipeOutput output, ItemLike template, Inp item1, Inp item2) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, template)
				.define('O', item1.ingredient())
				.define('X', item2.ingredient())
				.define('#', Tags.Items.GLASS_BLOCKS)
				.pattern(" #X")
				.pattern("#O#")
				.pattern("X# ")
				.unlockedBy("has_item", inventoryTrigger(item1.predicate()))
				.save(output);
	}
}