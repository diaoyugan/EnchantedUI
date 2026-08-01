package top.diaoyugan.enchanted_ui.api.client.gui.animation;

@FunctionalInterface
public interface UIEasing {
    UIEasing LINEAR = value -> value;
    UIEasing EASE_IN_OUT = value -> value * value * (3.0F - 2.0F * value);

    float apply(float value);
}
