package com.lootmod.lootgenerator.client.gui;

import com.lootmod.lootgenerator.LootMod;
import com.lootmod.lootgenerator.container.LootGeneratorContainer;
import com.lootmod.lootgenerator.network.LootGenPacket;
import com.lootmod.lootgenerator.network.SimpleChannelNetwork;
import com.lootmod.lootgenerator.network.UpdateChancePacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class LootGeneratorScreen extends ContainerScreen<LootGeneratorContainer> {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");
    private final List<TextFieldWidget> chanceInputs = new ArrayList<>();

    public LootGeneratorScreen(LootGeneratorContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 185;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.chanceInputs.clear();

        // Screen-top action button to trigger procedural spawn
        this.addButton(new Button(this.leftPos + 35, this.topPos - 22, 106, 20,
                new StringTextComponent("Generate 50x50"), button -> {
            SimpleChannelNetwork.INSTANCE.sendToServer(new LootGenPacket(this.menu.tileEntity.getBlockPos()));
        }));

        // Render percentage edit boxes beneath each template inventory slot
        for (int i = 0; i < 27; i++) {
            int row = i / 9;
            int col = i % 9;
            int x = this.leftPos + 8 + col * 18;
            int y = this.topPos + 18 + row * 18 + 10;

            final int slotIndex = i;
            TextFieldWidget textField = new TextFieldWidget(this.font, x, y, 16, 8, new StringTextComponent(""));
            textField.setMaxLength(3);
            textField.setValue(String.valueOf(this.menu.tileEntity.getChance(slotIndex)));
            textField.setBordered(false);
            textField.setTextColor(0x00FF00);

            textField.setResponder(val -> {
                try {
                    int parsed = Integer.parseInt(val);
                    SimpleChannelNetwork.INSTANCE.sendToServer(new UpdateChancePacket(this.menu.tileEntity.getBlockPos(), slotIndex, parsed));
                } catch (NumberFormatException ignored) {}
            });

            this.chanceInputs.add(textField);
            this.addWidget(textField);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        // Render percentage input fields over slot graphics
        for (TextFieldWidget field : this.chanceInputs) {
            field.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.minecraft != null) {
            this.minecraft.getTextureManager().bind(CHEST_GUI_TEXTURE);
        }
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, 71);
        this.blit(matrixStack, i, j + 71, 0, 126, this.imageWidth, 96);
    }
}