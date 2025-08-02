package Nivkida.selectedclassmodlota.UI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import Nivkida.selectedclassmodlota.network.ModNetwork;
import Nivkida.selectedclassmodlota.network.ClassSelectPacket;

import java.awt.Color;
import java.util.*;

public class ClassSelectScreen extends Screen {
    private static final int BACKGROUND_COLOR = 0x00000000;
    private int imageHeight, guiTop, buttonH;
    private final List<ImageButton> classButtons = new ArrayList<>();
    private final Map<ImageButton, String> buttonToClass = new HashMap<>();
    private final Map<String, List<Component>> classDescriptions = new HashMap<>();
    private String selectedClass = null;
    private Button confirmButton;
    private final Runnable onConfirm;

    private static final String[] CLASS_ORDER = {
            "knight","tank","berserk","samurai","assasin","archer","wizard"
    };

    private final Map<String, GradientPalette> palettes = Map.of(
            "knight",  new GradientPalette(
                    Color.decode("#4B69FF"),   // Синий (рыцарская сталь)
                    Color.decode("#C0C0FF"),   // Светло-синий (отблеск)
                    Color.decode("#00004D")    // Тёмно-синий (окантовка)
            ),
            "tank",    new GradientPalette( // Оставлен оригинальный
                    Color.decode("#D3D3D3"),
                    Color.decode("#B0B0B0"),
                    Color.decode("#2F2F2F")
            ),
            "berserk", new GradientPalette(
                    Color.decode("#FF3300"),   // Кроваво-красный
                    Color.decode("#FF9966"),   // Огненный оранжевый
                    Color.decode("#4D0000")    // Тёмно-бордовый
            ),
            "samurai", new GradientPalette(
                    Color.decode("#FF4D4D"),   // Красный (самурайская честь)
                    Color.decode("#FF9999"),   // Розовый (цвет сакуры)
                    Color.decode("#330000")    // Тёмно-красный
            ),
            "assasin", new GradientPalette(
                    Color.decode("#9933FF"),   // Фиолетовый (тень)
                    Color.decode("#D9B3FF"),   // Светло-фиолетовый (скрытность)
                    Color.decode("#1A0033")    // Тёмно-фиолетовый
            ),
            "archer",  new GradientPalette(
                    Color.decode("#66FF33"),   // Зелёный (лес)
                    Color.decode("#CCFF99"),   // Светло-зелёный (листва)
                    Color.decode("#003300")    // Тёмно-зелёный
            ),
            "wizard",  new GradientPalette(
                    Color.decode("#BA55D3"),
                    Color.decode("#A100A1"),
                    Color.decode("#000021")
            )
    );

    private float animOffset = 0f;

    public ClassSelectScreen(Runnable onConfirm) {
        super(Component.translatable("gui.selectedclassmodlota.class_select.title"));
        this.onConfirm = onConfirm;
    }

    @Override
    protected void init() {
        super.init();
        imageHeight = (int)(height * 0.7);
        guiTop = (height - imageHeight) / 2;

        for (String cls : CLASS_ORDER) {
            classDescriptions.put(cls, loadDescription(cls));
        }

        int count = CLASS_ORDER.length;
        int totalPad = (int)(width * 0.1), usable = width - totalPad;
        int gap = (int)(usable * 0.05), w = (usable - gap*(count-1)) / count;
        buttonH = w * 114 / 66;
        int buttonY = guiTop + (int)(height * 0.1);

        for (int i = 0; i < count; i++) {
            String cls = CLASS_ORDER[i];
            int x = (width - usable)/2 + i*(w + gap);
            ResourceLocation tex = new ResourceLocation("selectedclassmodlota:textures/gui/" + cls + ".png");
            ImageButton btn = new ImageButton(x, buttonY, w, buttonH, 0,0,buttonH, tex, w,buttonH,
                    b -> { selectedClass = buttonToClass.get(b); confirmButton.active = true; },
                    Component.translatable("class.selectedclassmodlota." + cls)
            ) {
                private float scale = 1f;
                @Override
                public void render(GuiGraphics gg, int mx, int my, float pt) {
                    float target = cls.equals(selectedClass) ? 1.1f : 1f;
                    scale += (target - scale) * 0.45f * pt;
                    gg.pose().pushPose();
                    gg.pose().translate(getX()+getWidth()/2f, getY()+getHeight()/2f,0);
                    gg.pose().scale(scale,scale,1f);
                    gg.pose().translate(-getX()-getWidth()/2f, -getY()-getHeight()/2f,0);
                    super.render(gg,mx,my,pt);
                    gg.pose().popPose();
                }
            };
            classButtons.add(btn);
            buttonToClass.put(btn, cls);
            addRenderableWidget(btn);
        }

        int cw = (int)(width * 0.15), ch = (int)(height * 0.05);
        int cy = guiTop + imageHeight + (int)(height * 0.05);
        confirmButton = Button.builder(
                Component.translatable("gui.selectedclassmodlota.confirm"),
                b -> {
                    if (selectedClass != null) {
                        ModNetwork.INSTANCE.sendToServer(new ClassSelectPacket(selectedClass));
                        onClose();
                    }
                }
        ).bounds((width-cw)/2, cy, cw, ch).build();
        confirmButton.active = false;
        addRenderableWidget(confirmButton);
    }

    private List<Component> loadDescription(String cls) {
        List<Component> list = new ArrayList<>();
        list.add(Component.literal(I18n.get("class.selectedclassmodlota." + cls)));
        int i = 1;
        while (I18n.exists("class.selectedclassmodlota." + cls + ".desc." + i)) {
            list.add(Component.literal(I18n.get("class.selectedclassmodlota." + cls + ".desc." + i)));
            i++;
        }
        return list;
    }

    @Override
    public boolean keyPressed(int kc,int sc,int m){
        return kc==256||super.keyPressed(kc,sc,m);
    }

    @Override
    public void onClose(){
        super.onClose();
        if(onConfirm!=null) Minecraft.getInstance().execute(onConfirm);
    }

    @Override
    public void renderBackground(GuiGraphics gg){
        gg.fill(0,0,width,height,BACKGROUND_COLOR);
    }

    @Override
    public void render(GuiGraphics gg, int mx, int my, float pt) {
        super.render(gg, mx, my, pt);
        if (selectedClass == null) return;

        animOffset = (animOffset + pt * 0.075f) % 1f;

        GradientPalette pal = palettes.get(selectedClass);
        List<Component> desc = classDescriptions.get(selectedClass);

        int pad = (int)(width * 0.01f);
        int buttonY = guiTop + (int)(height * 0.1);
        int bgY = buttonY + buttonH + pad * 2;
        int lineH = (int)(height * 0.03f);
        int maxW = desc.stream().mapToInt(c->font.width(c.getString())).max().orElse(0);
        int bgW = maxW + pad*2;
        int bgH = desc.size()*lineH + pad*2;
        int bgX = width/2 - bgW/2;
        gg.fill(bgX,bgY,bgX+bgW,bgY+bgH,0x80000000);

        for (int i = 0; i < desc.size(); i++) {
            String s = desc.get(i).getString();
            int y = bgY + pad + i*lineH;
            if (i == 0) {
                int tx = width/2 - font.width(s)/2;
                float repeatFactor = s.length() < 5 ? 2.0f : 1.0f;
                for (int j = 0; j < s.length(); j++) {
                    float position = s.length() > 1 ? (float)j / (s.length() - 1) * repeatFactor : 0f;
                    float t_full = (animOffset + position) % 1f;
                    int rgb = pal.getInterpolatedColor(t_full);
                    gg.drawString(font, String.valueOf(s.charAt(j)), tx, y, rgb);
                    tx += font.width(String.valueOf(s.charAt(j)));
                }
            } else {
                gg.drawCenteredString(font, desc.get(i), width/2, y, 0xFFFFFF);
            }
        }
    }

    private static class GradientPalette {
        private final Color startColor, endColor;
        public final int outlineColor;

        public GradientPalette(Color start, Color end, Color outline) {
            this.startColor = start;
            this.endColor = end;
            this.outlineColor = outline.getRGB() & 0xFFFFFF;
        }

        public int getInterpolatedColor(float t) {
            t = Math.max(0f, Math.min(1f, t));
            int r = (int)(startColor.getRed()   + t*(endColor.getRed()   - startColor.getRed()));
            int g = (int)(startColor.getGreen() + t*(endColor.getGreen() - startColor.getGreen()));
            int b = (int)(startColor.getBlue()  + t*(endColor.getBlue()  - startColor.getBlue()));
            return (r<<16)|(g<<8)|b;
        }
    }
}