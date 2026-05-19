package top.diaoyugan.enchanted_ui.api.client.gui;

public record UIScreenStyle(
        boolean backgroundBlur,
        boolean bottomBarBlur,
        int bottomBarBackgroundColor,
        int bottomBarSeparatorColor
) {
    public static final UIScreenStyle DEFAULT = builder().build();

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean backgroundBlur = true;
        private boolean bottomBarBlur = true;
        private int bottomBarBackgroundColor = 0x66000000;
        private int bottomBarSeparatorColor = 0x66FFFFFF;

        private Builder() {
        }

        public Builder backgroundBlur(boolean backgroundBlur) {
            this.backgroundBlur = backgroundBlur;
            return this;
        }

        public Builder bottomBarBlur(boolean bottomBarBlur) {
            this.bottomBarBlur = bottomBarBlur;
            return this;
        }

        public Builder bottomBarBackgroundColor(int bottomBarBackgroundColor) {
            this.bottomBarBackgroundColor = bottomBarBackgroundColor;
            return this;
        }

        public Builder bottomBarSeparatorColor(int bottomBarSeparatorColor) {
            this.bottomBarSeparatorColor = bottomBarSeparatorColor;
            return this;
        }

        public UIScreenStyle build() {
            return new UIScreenStyle(
                    backgroundBlur,
                    bottomBarBlur,
                    bottomBarBackgroundColor,
                    bottomBarSeparatorColor
            );
        }
    }
}
