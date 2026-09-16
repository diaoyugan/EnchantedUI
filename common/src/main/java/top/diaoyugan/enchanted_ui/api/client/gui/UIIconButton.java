package top.diaoyugan.enchanted_ui.api.client.gui;

import net.minecraft.network.chat.Component;
import top.diaoyugan.enchanted_ui.client.gui.widget.button.IconButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

/** Public fluent facade for dynamic, toggleable and circular icon buttons. */
public final class UIIconButton extends UIWidget {
    private UIIconButton(IconButton delegate) { super(delegate); }
    public static Builder builder(UIIcon icon) { return new Builder(icon); }

    public static final class Builder {
        private final UIIcon icon;
        private int x,y,width=20,height=20,iconSize=16;
        private Runnable onPress=()->{};
        private BooleanSupplier selected=()->false;
        private boolean circular;
        private Component narration=Component.empty();
        private final List<Conditional> conditional=new ArrayList<>();
        private Builder(UIIcon icon){this.icon=Objects.requireNonNull(icon);}
        public Builder bounds(int x,int y,int width,int height){this.x=x;this.y=y;this.width=width;this.height=height;return this;}
        public Builder iconSize(int size){iconSize=Math.max(1,size);return this;}
        public Builder iconWhen(BooleanSupplier condition,UIIcon icon){conditional.add(new Conditional(condition,icon));return this;}
        public Builder selected(BooleanSupplier selected){this.selected=Objects.requireNonNull(selected);return this;}
        public Builder circular(boolean value){circular=value;return this;}
        public Builder onPress(Runnable action){onPress=Objects.requireNonNull(action);return this;}
        public Builder narration(Component value){narration=Objects.requireNonNull(value);return this;}
        public UIIconButton build(){
            IconButton.Builder b=new IconButton.Builder(x,y,width,height).icon(icon.texture(),icon.textureWidth(),icon.textureHeight())
                    .uv(icon.u(),icon.v()).iconSize(iconSize).selected(selected).circular(circular).narration(narration).onPress(ignored->onPress.run());
            conditional.forEach(value->b.iconWhen(value.condition(),value.icon().texture(),value.icon().textureWidth(),value.icon().textureHeight(),value.icon().u(),value.icon().v()));
            return new UIIconButton(b.build());
        }
        private record Conditional(BooleanSupplier condition,UIIcon icon){}
    }
}
