package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import top.diaoyugan.enchanted_ui.api.client.gui.layout.UIBounds;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UITheme;
import top.diaoyugan.enchanted_ui.api.client.gui.theme.UIThemes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/** Generic image-and-text composition for compact rows, cards, and inspector panels. */
public final class UIImageTextPanel implements UIWidgetGroup {
    public enum Orientation { HORIZONTAL, VERTICAL }

    private final Supplier<Identifier> image;
    private final UIIcon fallbackIcon;
    private final List<Line> lines;
    private final Orientation orientation;
    private final ImageFit imageFit;
    private final ImageMask imageMask;
    private final UITheme theme;
    private final int imageSize;
    private final int lineHeight;
    private final int gap;

    private UIImageTextPanel(Builder builder) {
        image = builder.image;
        fallbackIcon = builder.fallbackIcon;
        lines = List.copyOf(builder.lines);
        orientation = builder.orientation;
        imageFit = builder.imageFit;
        imageMask = builder.imageMask;
        theme = builder.theme;
        imageSize = builder.imageSize;
        lineHeight = builder.lineHeight;
        gap = builder.gap;
    }

    public static Builder builder(Supplier<Identifier> image) {
        return new Builder(image);
    }

    @Override
    public List<AbstractWidget> build(UIBounds bounds) {
        List<AbstractWidget> widgets = new ArrayList<>();
        int maxImageHeight = orientation == Orientation.HORIZONTAL
                ? bounds.height()
                : Math.max(0, bounds.height() - gap - lineHeight * lines.size());
        int size = Math.max(0, Math.min(imageSize, Math.min(bounds.width(), maxImageHeight)));
        int imageX = orientation == Orientation.HORIZONTAL
                ? bounds.x()
                : bounds.x() + (bounds.width() - size) / 2;
        int imageY = bounds.y();

        if (size > 0) {
            UIImageWidget.Builder imageBuilder = UIImageWidget.builder(image)
                    .bounds(imageX, imageY, size, size)
                    .fit(imageFit)
                    .mask(imageMask)
                    .cornerRadius(theme.radius().card())
                    .background(theme.colors().surface())
                    .border(theme.colors().border())
                    .shadow(orientation == Orientation.VERTICAL ? theme.colors().shadow() : 0);
            if (fallbackIcon != null) imageBuilder.fallbackIcon(fallbackIcon);
            widgets.add(imageBuilder.build());
        }

        int textX = orientation == Orientation.HORIZONTAL ? imageX + size + (size > 0 ? gap : 0) : bounds.x();
        int textY = orientation == Orientation.HORIZONTAL ? bounds.y() : imageY + size + gap;
        int textWidth = orientation == Orientation.HORIZONTAL
                ? Math.max(0, bounds.width() - size - (size > 0 ? gap : 0))
                : bounds.width();
        UILabelWidget.Align defaultAlign = orientation == Orientation.HORIZONTAL
                ? UILabelWidget.Align.LEFT
                : UILabelWidget.Align.CENTER;

        for (int index = 0; index < lines.size(); index++) {
            Line line = lines.get(index);
            widgets.add(new UIMarqueeLabel(
                    textX,
                    textY + index * lineHeight,
                    textWidth,
                    lineHeight,
                    line.text(),
                    () -> line.color().getAsInt(),
                    line.align() == null ? defaultAlign : line.align()
            ));
        }
        return widgets;
    }

    private record Line(Supplier<Component> text, IntSupplier color, UILabelWidget.Align align) {}

    public static final class Builder {
        private final Supplier<Identifier> image;
        private UIIcon fallbackIcon;
        private final List<Line> lines = new ArrayList<>();
        private Orientation orientation = Orientation.HORIZONTAL;
        private ImageFit imageFit = ImageFit.COVER;
        private ImageMask imageMask = ImageMask.NONE;
        private UITheme theme = UIThemes.current();
        private int imageSize = 64;
        private int lineHeight = 16;
        private int gap = 5;

        private Builder(Supplier<Identifier> image) {
            this.image = Objects.requireNonNull(image);
        }

        public Builder fallbackIcon(UIIcon value) {
            fallbackIcon = value;
            return this;
        }

        public Builder line(Supplier<Component> text, IntSupplier color) {
            return line(text, color, null);
        }

        public Builder line(Supplier<Component> text, IntSupplier color, UILabelWidget.Align align) {
            lines.add(new Line(Objects.requireNonNull(text), Objects.requireNonNull(color), align));
            return this;
        }

        public Builder orientation(Orientation value) {
            orientation = Objects.requireNonNull(value);
            return this;
        }

        public Builder imageFit(ImageFit value) {
            imageFit = Objects.requireNonNull(value);
            return this;
        }

        public Builder imageMask(ImageMask value) {
            imageMask = Objects.requireNonNull(value);
            return this;
        }

        public Builder theme(UITheme value) {
            theme = Objects.requireNonNull(value);
            return this;
        }

        public Builder imageSize(int value) {
            imageSize = Math.max(0, value);
            return this;
        }

        public Builder lineHeight(int value) {
            lineHeight = Math.max(1, value);
            return this;
        }

        public Builder gap(int value) {
            gap = Math.max(0, value);
            return this;
        }

        public UIImageTextPanel build() {
            return new UIImageTextPanel(this);
        }
    }
}
