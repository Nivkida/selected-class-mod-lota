package Nivkida.selectedclassmodlota.UI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import Nivkida.selectedclassmodlota.network.ModNetwork;
import Nivkida.selectedclassmodlota.network.ClassSelectPacket;

import java.util.*;

public class ClassSelectScreen extends Screen {
    // Полностью прозрачный фон
    private static final int BACKGROUND_COLOR = 0x00000000;

    private int imageHeight;
    private int guiTop;
    private int buttonH;

    private final List<ImageButton> classButtons = new ArrayList<>();
    private final Map<ImageButton, String> buttonToClass = new HashMap<>();
    private final Map<String, List<Component>> classDescriptions = new HashMap<>();

    private String selectedClass = null;
    private Button confirmButton;
    private final Runnable onConfirm;

    public ClassSelectScreen(Runnable onConfirm) {
        super(Component.translatable("gui.selectedclassmodlota.class_select.title"));
        this.onConfirm = onConfirm;
    }

    @Override
    protected void init() {
        super.init();

        // Масштаб imageHeight относительно высоты экрана
        this.imageHeight = (int)(this.height * 0.7);
        this.guiTop = (this.height - imageHeight) / 2;

        // Инициализация описаний классов с локализацией
        classDescriptions.put("knight", getLocalizedDescription("knight"));
        classDescriptions.put("tank", getLocalizedDescription("tank"));
        classDescriptions.put("berserk", getLocalizedDescription("berserk"));
        classDescriptions.put("samurai", getLocalizedDescription("samurai"));
        classDescriptions.put("assasin", getLocalizedDescription("assasin"));
        classDescriptions.put("archer", getLocalizedDescription("archer"));
        classDescriptions.put("wizard", getLocalizedDescription("wizard"));

        String[] classNames = {"knight", "tank", "berserk", "samurai", "assasin", "archer", "wizard"};
        int count = classNames.length;

        // Расчет размеров кнопок
        int totalSpacing = (int)(this.width * 0.1);
        int usableWidth = this.width - totalSpacing;
        int spacing = (int)(usableWidth * 0.05);
        int buttonW = (usableWidth - spacing * (count - 1)) / count;
        this.buttonH = buttonW * 114 / 66;

        int y = guiTop + (int)(this.height * 0.1);

        for (int i = 0; i < count; i++) {
            String name = classNames[i];
            int x = (this.width - usableWidth) / 2 + i * (buttonW + spacing);

            ResourceLocation tex = ResourceLocation.fromNamespaceAndPath(
                    "selectedclassmodlota", "textures/gui/" + name + ".png"
            );

            // Кнопка с анимированным масштабом
            ImageButton btn = new ImageButton(x, y, buttonW, buttonH,
                    0, 0, buttonH,
                    tex, buttonW, buttonH,
                    b -> {
                        selectedClass = buttonToClass.get(b);
                        confirmButton.active = true;
                    },
                    Component.translatable("class.selectedclassmodlota." + name) // Локализованное название
            ) {
                private float scale = 1.0f;
                private float targetScale = 1.0f;
                private final float speed = 0.45f;

                @Override
                public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                    String className = buttonToClass.get(this);
                    targetScale = className.equals(selectedClass) ? 1.1f : 1.0f;

                    if (scale != targetScale) {
                        float delta = (targetScale - scale) * speed * partialTicks;
                        scale += delta;
                        if (Math.abs(scale - targetScale) < 0.01f) {
                            scale = targetScale;
                        }
                    }

                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(getX() + getWidth() / 2.0f, getY() + getHeight() / 2.0f, 0);
                    guiGraphics.pose().scale(scale, scale, 1.0f);
                    guiGraphics.pose().translate(-getX() - getWidth() / 2.0f, -getY() - getHeight() / 2.0f, 0);

                    super.render(guiGraphics, mouseX, mouseY, partialTicks);
                    guiGraphics.pose().popPose();
                }
            };

            classButtons.add(btn);
            buttonToClass.put(btn, name);
            this.addRenderableWidget(btn);
        }

        // Кнопка подтверждения с локализацией
        int cw = (int)(this.width * 0.15);
        int ch = (int)(this.height * 0.05);
        confirmButton = Button.builder(Component.translatable("gui.selectedclassmodlota.confirm"), b -> {
            if (selectedClass != null) {
                ModNetwork.INSTANCE.sendToServer(new ClassSelectPacket(selectedClass));
                onClose();
            }
        }).bounds((this.width - cw) / 2, this.guiTop + imageHeight - (int)(this.height * 0.1), cw, ch).build();

        confirmButton.active = false;
        this.addRenderableWidget(confirmButton);
    }

    // Получение локализованного описания класса
    private List<Component> getLocalizedDescription(String className) {
        List<Component> description = new ArrayList<>();

        // Название класса
        description.add(Component.translatable("class.selectedclassmodlota." + className));

        // Характеристики
        for (int i = 1; i <= 2; i++) {
            String key = "class.selectedclassmodlota." + className + ".desc." + i;
            description.add(Component.translatable(key));
        }

        return description;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (onConfirm != null) {
            Minecraft.getInstance().execute(() -> onConfirm.run());
        }
    }

    @Override
    public void renderBackground(GuiGraphics gg) {
        gg.fill(0, 0, this.width, this.height, BACKGROUND_COLOR);
    }

    @Override
    public void render(GuiGraphics gg, int mx, int my, float pt) {
        super.render(gg, mx, my, pt);

        if (selectedClass != null) {
            List<Component> description = classDescriptions.getOrDefault(selectedClass, List.of());
            int textY = guiTop + buttonH + (int)(this.height * 0.15);
            int textX = this.width / 2;
            int lineHeight = (int)(this.height * 0.025);
            int backgroundPadding = (int)(this.width * 0.01);
            int maxWidth = 0;

            for (Component line : description) {
                int width = this.font.width(line);
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }

            maxWidth = Math.min(maxWidth, (int)(this.width * 0.8));

            int bgX = textX - maxWidth / 2 - backgroundPadding;
            int bgY = textY - backgroundPadding;
            int bgWidth = maxWidth + backgroundPadding * 2;
            int bgHeight = description.size() * lineHeight + backgroundPadding * 2;
            gg.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, 0x80000000);

            for (int i = 0; i < description.size(); i++) {
                gg.drawCenteredString(this.font, description.get(i), textX, textY + i * lineHeight, 0xFFFFFF);
            }
        }
    }
}