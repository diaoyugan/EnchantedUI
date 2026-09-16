package top.diaoyugan.enchanted_ui.api.client.gui.layout;

/** Flex-style sizing hints reusable by custom layout algorithms. */
public record UISizeConstraints(int minWidth, int maxWidth, int minHeight, int maxHeight, float grow, float shrink) {
    public static final UISizeConstraints FLEX = new UISizeConstraints(0,Integer.MAX_VALUE,0,Integer.MAX_VALUE,1,1);
    public UISizeConstraints {
        minWidth=Math.max(0,minWidth);minHeight=Math.max(0,minHeight);
        maxWidth=Math.max(minWidth,maxWidth);maxHeight=Math.max(minHeight,maxHeight);
        grow=Math.max(0,grow);shrink=Math.max(0,shrink);
    }
    public UIBounds apply(UIBounds bounds,UIAlignment horizontal,UIAlignment vertical){
        int width=Math.max(minWidth,Math.min(maxWidth,bounds.width()));
        int height=Math.max(minHeight,Math.min(maxHeight,bounds.height()));
        int x=align(bounds.x(),bounds.width(),width,horizontal),y=align(bounds.y(),bounds.height(),height,vertical);
        return new UIBounds(x,y,horizontal==UIAlignment.STRETCH?bounds.width():width,vertical==UIAlignment.STRETCH?bounds.height():height);
    }
    private static int align(int start,int available,int size,UIAlignment alignment){return switch(alignment){case START,STRETCH->start;case CENTER->start+(available-size)/2;case END->start+available-size;};}
}
