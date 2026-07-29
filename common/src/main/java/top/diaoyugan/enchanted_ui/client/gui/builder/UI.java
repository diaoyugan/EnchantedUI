package top.diaoyugan.enchanted_ui.client.gui.builder;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.api.client.gui.UILocalization;
import top.diaoyugan.enchanted_ui.client.gui.layout.HorizontalLayout;
import top.diaoyugan.enchanted_ui.client.gui.layout.VerticalLayout;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.IconButton;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.TextureButton;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.BooleanOptionWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.ColorPreviewWidget;
import top.diaoyugan.enchanted_ui.client.gui.widget.option.NumericSliderOptionWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus;

/**
 * Internal form engine used by the public {@code UIForm}/{@code UIFormPage}
 * facade. It is public only because the facade lives in the relocatable API
 * package; consumers should not depend on this implementation namespace.
 * <p>
 * The form surface stays cohesive here so every control shares one layout
 * cursor and one dirty-state controller. Widget construction details are
 * delegated to {@link FormInputFactory} and {@link FormDisplayFactory}.
 */
@ApiStatus.Internal
public final class UI {

    private UI() {
    }

    public record BuildContext(
            int screenWidth,
            int screenHeight,
            int centerX,
            int viewportLeft,
            int viewportRight
    ) {
        public BuildContext(int screenWidth, int screenHeight, int centerX) {
            this(screenWidth, screenHeight, centerX, 0, screenWidth);
        }

        public int availableWidth() {
            return Math.max(0, viewportRight - viewportLeft);
        }

        public VerticalLayout vertical(int contentWidth, int startY, int gap) {
            int leftX = centerX - contentWidth / 2;
            return new VerticalLayout(leftX, startY, gap);
        }

        public HorizontalLayout horizontal(int startX, int startY, int gap) {
            return new HorizontalLayout(startX, startY, gap);
        }
    }

    public interface FormSpec {
        void build(Form form);

        default void onOpen(Form form) {
        }

        default void onClose(Form form) {
        }

        default void onShow(Form form) {
        }

        default void onHide(Form form) {
        }

        default void onPageChanged(Form form, int previousPage, int currentPage) {
        }

        default void onSave(Form form) {
        }

        default void tick(Form form) {
        }

        default boolean keyPressed(Form form, KeyEvent event) {
            return false;
        }

        default boolean keyReleased(Form form, KeyEvent event) {
            return false;
        }
    }

    public static final class FormPage {

        private final int contentWidth;
        private final int startY;
        private final int gap;
        private final FormSpec spec;

        private Form lastForm;

        public FormPage(int contentWidth, int startY, int gap, FormSpec spec) {
            this.contentWidth = contentWidth;
            this.startY = startY;
            this.gap = gap;
            this.spec = Objects.requireNonNull(spec, "spec");
        }

        public Form lastForm() {
            return lastForm;
        }

        public List<AbstractWidget> build(BuildContext ctx) {
            int responsiveWidth = Math.min(contentWidth, Math.max(40, ctx.availableWidth() - 24));
            Form form = new Form(ctx, responsiveWidth, startY, gap);
            spec.build(form);
            lastForm = form;
            return form.widgets();
        }

        public boolean onSave() {
            if (lastForm == null) {
                return true;
            }
            if (!lastForm.runSavers()) {
                return false;
            }
            spec.onSave(lastForm);
            lastForm.markClean();
            return true;
        }

        public void onOpen() {
            if (lastForm != null) {
                spec.onOpen(lastForm);
            }
        }

        public void onClose() {
            if (lastForm != null) {
                spec.onClose(lastForm);
            }
        }

        public void onShow() {
            if (lastForm != null) {
                spec.onShow(lastForm);
            }
        }

        public void onHide() {
            if (lastForm != null) {
                spec.onHide(lastForm);
            }
        }

        public void onPageChanged(int previousPage, int currentPage) {
            if (lastForm != null) {
                spec.onPageChanged(lastForm, previousPage, currentPage);
            }
        }

        public void tick() {
            if (lastForm == null) return;
            spec.tick(lastForm);
        }

        public boolean keyPressed(KeyEvent event) {
            if (lastForm == null) return false;
            if (lastForm.keyPressed(event)) return true;
            return spec.keyPressed(lastForm, event);
        }

        public boolean keyReleased(KeyEvent event) {
            if (lastForm == null) return false;
            if (lastForm.keyReleased(event)) return true;
            return spec.keyReleased(lastForm, event);
        }

        public boolean hasUnsavedChanges() {
            return lastForm != null && lastForm.hasUnsavedChanges();
        }

        public void reload() {
            if (lastForm != null) {
                lastForm.reload();
            }
        }

        public void markClean() {
            if (lastForm != null) {
                lastForm.markClean();
            }
        }
    }

    public static final class Form {
        private final BuildContext ctx;
        private final int contentWidth;
        private final VerticalLayout layout;
        private final List<AbstractWidget> widgets;
        private final FormStateController state;
        private final FormInteractionRegistry interactions;
        private final FormInputFactory inputs;
        private final FormDisplayFactory display;

        public Form(BuildContext ctx, int contentWidth, int startY, int gap) {
            this(
                    ctx,
                    contentWidth,
                    ctx.vertical(contentWidth, startY, gap),
                    new ArrayList<>(),
                    new FormStateController(),
                    new FormInteractionRegistry()
            );
        }

        private Form(
                BuildContext ctx,
                int contentWidth,
                VerticalLayout layout,
                List<AbstractWidget> widgets,
                FormStateController state,
                FormInteractionRegistry interactions
        ) {
            this.ctx = Objects.requireNonNull(ctx, "ctx");
            this.contentWidth = contentWidth;
            this.layout = layout;
            this.widgets = widgets;
            this.state = state;
            this.interactions = interactions;
            this.inputs = new FormInputFactory(contentWidth, layout, widgets, state, interactions);
            this.display = new FormDisplayFactory(contentWidth, layout, widgets);
        }

        public BuildContext ctx() {
            return ctx;
        }

        public int contentWidth() {
            return contentWidth;
        }

        public VerticalLayout layout() {
            return layout;
        }

        public List<AbstractWidget> widgets() {
            return widgets;
        }

        public FormInputFactory inputs() {
            return inputs;
        }

        public FormDisplayFactory display() {
            return display;
        }

        public boolean validate() {
            return state.validate();
        }

        public boolean runSavers() {
            return state.runSavers();
        }

        public boolean save() {
            return state.save();
        }

        public boolean hasUnsavedChanges() {
            return state.hasUnsavedChanges();
        }

        public void reload() {
            state.reload();
        }

        public void markClean() {
            state.markClean();
        }

        public boolean keyPressed(KeyEvent event) {
            return interactions.keyPressed(event);
        }

        public boolean keyReleased(KeyEvent event) {
            return interactions.keyReleased(event);
        }

        public Form space(int height) {
            layout.next(height);
            return this;
        }

        public <T extends AbstractWidget> T widget(T widget, int height) {
            widgets.add(widget);
            layout.next(height);
            return widget;
        }

        public Form section(Component title, int indent, Consumer<Form> builder) {
            display.title(title);
            Form nested = new Form(
                    ctx,
                    Math.max(40, contentWidth - indent),
                    new VerticalLayout(layout.x() + indent, layout.y(), layout.gap()),
                    widgets,
                    state,
                    interactions
            );
            builder.accept(nested);
            layout.setY(nested.layout.y());
            return this;
        }


        public BooleanOptionWidget toggle(Component label, BooleanSupplier getter, Consumer<Boolean> setter) {
            BooleanOptionWidget w = new BooleanOptionWidget(
                    layout.x(), layout.y(),
                    contentWidth, 20,
                    label,
                    getter,
                    setter
            );
            trackModelValue(getter::getAsBoolean, setter, Function.identity(), null);
            widgets.add(w);
            layout.next(20);
            return w;
        }

        public Button button(Component label, int width, Runnable action) {
            Button button = Button.builder(label, b -> action.run())
                    .bounds(layout.x(), layout.y(), width, 20)
                    .build();
            widgets.add(button);
            layout.next(20);
            return button;
        }

        public List<Button> buttonRow(
                Component leftLabel,
                Runnable leftAction,
                Component rightLabel,
                Runnable rightAction
        ) {
            int halfWidth = (contentWidth - 4) / 2;
            Button left = Button.builder(leftLabel, b -> leftAction.run())
                    .bounds(layout.x(), layout.y(), halfWidth, 20)
                    .build();
            Button right = Button.builder(rightLabel, b -> rightAction.run())
                    .bounds(layout.x() + halfWidth + 4, layout.y(), halfWidth, 20)
                    .build();
            widgets.add(left);
            widgets.add(right);
            layout.next(20);
            return List.of(left, right);
        }

        public IconButton iconButton(
                int buttonSize,
                Identifier iconTexture,
                int texW,
                int texH,
                @Nullable Identifier hoverTexture,
                int hoverTexW,
                int hoverTexH,
                int iconSize,
                Runnable action
        ) {
            IconButton.Builder builder = new IconButton.Builder(layout.x(), layout.y(), buttonSize, buttonSize)
                    .icon(iconTexture, texW, texH)
                    .iconSize(iconSize)
                    .onPress(b -> action.run());
            if (hoverTexture != null) {
                builder.hoverIcon(hoverTexture, hoverTexW, hoverTexH);
            }
            IconButton button = builder.build();
            widgets.add(button);
            layout.next(buttonSize);
            return button;
        }

        public TextureButton textureButton(
                int width,
                int height,
                Identifier texture,
                int texW,
                int texH,
                @Nullable Identifier hoverTexture,
                int hoverTexW,
                int hoverTexH,
                Runnable action
        ) {
            TextureButton.Builder builder = new TextureButton.Builder(layout.x(), layout.y(), width, height)
                    .texture(texture, texW, texH)
                    .onPress(b -> action.run());
            if (hoverTexture != null) {
                builder.hoverTexture(hoverTexture, hoverTexW, hoverTexH);
            }
            TextureButton button = builder.build();
            widgets.add(button);
            layout.next(height);
            return button;
        }

        public List<BooleanOptionWidget> toggleRow(
                Component leftLabel,
                BooleanSupplier leftGetter,
                Consumer<Boolean> leftSetter,
                Component rightLabel,
                BooleanSupplier rightGetter,
                Consumer<Boolean> rightSetter
        ) {
            int halfWidth = (contentWidth - 4) / 2;

            BooleanOptionWidget left = new BooleanOptionWidget(
                    layout.x(), layout.y(),
                    halfWidth, 20,
                    leftLabel,
                    leftGetter,
                    leftSetter
            );
            BooleanOptionWidget right = new BooleanOptionWidget(
                    layout.x() + halfWidth + 4, layout.y(),
                    halfWidth, 20,
                    rightLabel,
                    rightGetter,
                    rightSetter
            );
            trackModelValue(leftGetter::getAsBoolean, leftSetter, Function.identity(), null);
            trackModelValue(rightGetter::getAsBoolean, rightSetter, Function.identity(), null);
            widgets.add(left);
            widgets.add(right);
            layout.next(20);

            return List.of(left, right);
        }


        public ColorGroup rgbaSlidersWithPreview(
                Component title,
                UILocalization.ColorLabels labels,
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
            return inputs.rgbaSlidersWithPreview(title, labels, rGetter, rSetter, gGetter, gSetter, bGetter, bSetter, aGetter, aSetter, alphaAsPercentage);
        }


        private <T> void trackModelValue(
                Supplier<T> currentValue,
                Consumer<T> resetAction,
                Function<T, T> copy,
                @Nullable Runnable afterReset
        ) {
            state.trackModelValue(currentValue, resetAction, copy, afterReset);
        }

        private <T> void trackWidgetValue(
                Supplier<T> widgetValue,
                Consumer<T> resetAction,
                Function<T, T> copy
        ) {
            state.trackWidgetValue(widgetValue, resetAction, copy);
        }
    }

    public record ColorGroup(
            NumericSliderOptionWidget r,
            NumericSliderOptionWidget g,
            NumericSliderOptionWidget b,
            NumericSliderOptionWidget a,
            ColorPreviewWidget preview
    ) {
    }

}
