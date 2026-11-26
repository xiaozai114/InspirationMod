package com.starcrosser.inspiration.client.jei.preview;

import java.util.List;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Scene;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import com.lowdragmc.lowdraglib2.integration.xei.IngredientIO;
import com.lowdragmc.lowdraglib2.utils.data.BlockInfo;
import com.lowdragmc.lowdraglib2.utils.virtuallevel.TrackedDummyWorld;
import com.starcrosser.inspiration.client.jei.StructurePreview;
import dev.vfyjxf.taffy.style.FlexDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public final class SimulatorPreviewPanel {
    public static final int WIDTH = 158;
    public static final int HEIGHT = 144;

    private final TrackedDummyWorld world = new TrackedDummyWorld();
    private final Scene scene = new Scene();
    private final Label floorName = new Label();
    private final List<BlockPos> positions;
    private final int bottom;
    private final int height;
    private int floor = -1;

    public static ModularUI create(StructurePreview example) {
        return new SimulatorPreviewPanel(example).layout(example);
    }

    private SimulatorPreviewPanel(StructurePreview example) {
        positions = example.blocks().keySet().stream().map(p -> p.above(64)).toList();
        example.blocks().forEach((pos, state) -> world.addBlock(pos.above(64), new BlockInfo(state)));
        bottom = positions.stream().mapToInt(BlockPos::getY).min().orElseThrow();
        height = example.kind().height();
        scene.useOrtho().createScene(world);
        scene.setOrthoRange(0.95f);
        scene.setRenderFacing(false);
        scene.setRenderSelect(false);
        showFloor();
    }

    private ModularUI layout(StructurePreview example) {
        UIElement panel = new UIElement();
        panel.addClass("panel_bg");
        panel.getLayout().width(WIDTH).height(HEIGHT).paddingAll(3).gapAll(1).flexDirection(FlexDirection.COLUMN);
        var kind = example.kind();
        Label title = new Label();
        title.setText(Component.translatable("jei.inspiration.preview.title", kind.controller().getName(),
                kind.width(), kind.height(), kind.depth()));
        panel.addChild(title);
        scene.getLayout().widthPercent(100).flex(1);
        panel.addChild(scene);

        UIElement controls = new UIElement();
        controls.getLayout().height(15).gapAll(3).flexDirection(FlexDirection.ROW);
        controls.addChild(layerButton("<", -1));
        floorName.getLayout().flex(1);
        controls.addChild(floorName);
        controls.addChild(layerButton(">", 1));
        panel.addChild(controls);

        var hint = new Label();
        hint.setText(Component.translatable("jei.inspiration.preview.hint"));
        panel.addChild(hint);
        UIElement materials = new UIElement();
        materials.getLayout().height(18).gapAll(2).flexDirection(FlexDirection.ROW);
        for (var stack : example.materials()) {
            var slot = new ItemSlot().setItem(stack.copy());
            slot.xeiRecipeSlot(IngredientIO.INPUT, 1);
            slot.style(style -> style.tooltips(Component.translatable("jei.inspiration.preview.parts",
                    stack.getHoverName(), stack.getCount())));
            materials.addChild(slot);
        }
        panel.addChild(materials);
        return ModularUI.of(UI.of(panel, StylesheetManager.ORE_MERGED));
    }

    private Button layerButton(String text, int delta) {
        var button = new Button().setText(text).setOnClick(event -> {
            floor = Math.floorMod(floor + 1 + delta, height + 1) - 1;
            showFloor();
        });
        button.getLayout().width(18).height(15);
        return button;
    }

    private void showFloor() {
        var visible = floor < 0 ? positions : positions.stream().filter(p -> p.getY() == bottom + floor).toList();
        world.setBlockFilter(pos -> floor < 0 || pos.getY() == bottom + floor);
        scene.setRenderedCore(visible);
        floorName.setText(floor < 0 ? Component.translatable("jei.inspiration.preview.all")
                : Component.translatable("jei.inspiration.preview.floor", floor + 1, height));
    }
}
