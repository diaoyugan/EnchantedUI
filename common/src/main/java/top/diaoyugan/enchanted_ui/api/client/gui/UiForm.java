package top.diaoyugan.enchanted_ui.api.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.builder.UI;
import top.diaoyugan.enchanted_ui.client.gui.layout.VerticalLayout;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.CombinationKeyBindingButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.input.KeyBindingButtonWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.BooleanOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.IntSliderOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.TextWidget;

import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class UiForm {

    private final UI.Form delegate;

    UiForm(UI.Form delegate) {
        this.delegate = delegate;
    }

    UI.Form delegate() {
        return delegate;
    }

    public UiBuildContext ctx() {
        return new UiBuildContext(delegate.ctx());
    }

    public int contentWidth() {
        return delegate.contentWidth();
    }

    public VerticalLayout layout() {
        return delegate.layout();
    }

    public List<net.minecraft.client.gui.components.AbstractWidget> widgets() {
        return delegate.widgets();
    }

    public UiForm space(int height) {
        delegate.space(height);
        return this;
    }

    public TextWidget title(Component text) {
        return delegate.title(text);
    }

    public BooleanOptionWidget toggle(Component label, BooleanSupplier getter, Consumer<Boolean> setter) {
        return delegate.toggle(label, getter, setter);
    }

    public List<BooleanOptionWidget> toggleRow(
            Component leftLabel,
            BooleanSupplier leftGetter,
            Consumer<Boolean> leftSetter,
            Component rightLabel,
            BooleanSupplier rightGetter,
            Consumer<Boolean> rightSetter
    ) {
        return delegate.toggleRow(leftLabel, leftGetter, leftSetter, rightLabel, rightGetter, rightSetter);
    }

    public IntSliderOptionWidget intSlider(
            Component label,
            int min,
            int max,
            IntSupplier getter,
            IntConsumer setter,
            boolean percentage
    ) {
        return delegate.intSlider(label, min, max, getter, setter, percentage);
    }

    public IntSliderOptionWidget intSlider(
            Component label,
            int width,
            int min,
            int max,
            IntSupplier getter,
            IntConsumer setter,
            boolean percentage
    ) {
        return delegate.intSlider(label, width, min, max, getter, setter, percentage);
    }

    public MultiLineEditBox textArea(
            Component label,
            int height,
            Supplier<String> getter,
            Consumer<String> setter
    ) {
        return delegate.textArea(label, height, getter, setter);
    }

    public KeyBindingButtonWidget keyBinding(
            Component label,
            Consumer<InputConstants.Key> setter,
            Supplier<Component> displaySupplier,
            KeyMapping vanillaKeyMapping,
            boolean syncVanilla
    ) {
        return delegate.keyBinding(label, setter, displaySupplier, vanillaKeyMapping, syncVanilla);
    }

    public CombinationKeyBindingButtonWidget combinationKeyBinding(
            Component label,
            Supplier<Set<Integer>> getter,
            Consumer<Set<Integer>> setter
    ) {
        return delegate.combinationKeyBinding(label, getter, setter);
    }

    public UiColorGroup rgbaSlidersWithPreview(
            Component title,
            Supplier<Integer> rGetter,
            IntConsumer rSetter,
            Supplier<Integer> gGetter,
            IntConsumer gSetter,
            Supplier<Integer> bGetter,
            IntConsumer bSetter,
            Supplier<Integer> aGetter,
            IntConsumer aSetter,
            boolean alphaAsPercentage
    ) {
        UI.ColorGroup colorGroup = delegate.rgbaSlidersWithPreview(
                title,
                rGetter,
                rSetter,
                gGetter,
                gSetter,
                bGetter,
                bSetter,
                aGetter,
                aSetter,
                alphaAsPercentage
        );
        return new UiColorGroup(colorGroup.r(), colorGroup.g(), colorGroup.b(), colorGroup.a(), colorGroup.preview());
    }
}
