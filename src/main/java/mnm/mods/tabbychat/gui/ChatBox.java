package mnm.mods.tabbychat.gui;

import java.awt.Point;
import java.awt.Rectangle;

import org.lwjgl.input.Mouse;

import com.google.common.eventbus.Subscribe;
import com.mumfrey.liteloader.core.LiteLoader;

import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.api.gui.ChatGui;
import mnm.mods.tabbychat.settings.ColorSettings;
import mnm.mods.tabbychat.settings.TabbySettings;
import mnm.mods.tabbychat.util.ScaledDimension;
import mnm.mods.util.Color;
import mnm.mods.util.ILocation;
import mnm.mods.util.Location;
import mnm.mods.util.gui.BorderLayout;
import mnm.mods.util.gui.GuiPanel;
import mnm.mods.util.gui.events.GuiMouseEvent;
import mnm.mods.util.gui.events.GuiMouseEvent.MouseEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;

public class ChatBox extends GuiPanel implements ChatGui {

    private static ColorSettings colors = TabbyChat.getInstance().settings.colors;

    private ChatArea chatArea;
    private ChatTray pnlTray;
    private TextBox txtChatInput;

    private boolean dragMode;
    private Point drag;
    private Location tempbox;

    public ChatBox(ILocation rect) {
        super(new BorderLayout());
        this.addComponent(pnlTray = new ChatTray(), BorderLayout.Position.NORTH);
        this.addComponent(chatArea = new ChatArea(), BorderLayout.Position.CENTER);
        this.addComponent(txtChatInput = new TextBox(), BorderLayout.Position.SOUTH);
        this.addComponent(new Scrollbar(chatArea), BorderLayout.Position.EAST);
        super.setLocation(rect);
    }

    @Subscribe
    public void killjoysMovingCompanyForAllYourFurnitureMovingNeeds(GuiMouseEvent event) {
        ILocation bounds = getLocation();

        // divide by scale because smaller scales make the point movement larger
        int x = bounds.getXPos() + event.getMouseX();
        int y = bounds.getYPos() + event.getMouseY();

        if (event.getType() == MouseEvent.CLICK) {
            if (Mouse.isButtonDown(0) && (pnlTray.isHovered() || (GuiScreen.isAltKeyDown() && isHovered()))) {
                dragMode = !pnlTray.isHandleHovered();
                drag = new Point(x, y);
                tempbox = bounds.copy();
            }
        }

        if (drag != null) {
            if (event.getType() == MouseEvent.RELEASE) {
                drag = null;
                tempbox = null;
            } else if (event.getType() == MouseEvent.DRAG) {
                if (!dragMode) {
                    setLocation(new Location(
                            tempbox.getXPos(),
                            tempbox.getYPos() + y - drag.y,
                            tempbox.getWidth() + x - drag.x,
                            tempbox.getHeight() - y + drag.y));
                } else {
                    setLocation(getLocation().copy()
                            .setXPos(tempbox.getXPos() + x - drag.x)
                            .setYPos(tempbox.getYPos() + y - drag.y));
                }
            }
        }
    }

    @Override
    public Color getPrimaryColor() {
        return colors.chatBorderColor.get();
    }

    @Override
    public Color getSecondaryColor() {
        return colors.chatBoxColor.get();
    }

    @Override
    public float getScale() {
        return TabbyChat.getInstance().getChatGui().getChatScale();
    }

    @Override
    public void updateComponent() {
        ILocation bounds = getLocation();
        ILocation point = getActualLocation();
        float scale = getActualScale();
        ScaledResolution sr = new ScaledResolution(mc);

        int x = point.getXPos();
        int y = point.getYPos();
        int w = (int) (bounds.getWidth() * scale);
        int h = (int) (bounds.getHeight() * scale);

        int w1 = w;
        int h1 = h;
        int x1 = x;
        int y1 = y;

        w1 = Math.min(sr.getScaledWidth(), w);
        h1 = Math.min(sr.getScaledHeight(), h);
        w1 = Math.max(50, w1);
        h1 = Math.max(50, h1);

        x1 = Math.max(0, x1);
        x1 = Math.min(x1, sr.getScaledWidth() - w1);
        y1 = Math.max(0, y1);
        y1 = Math.min(y1, sr.getScaledHeight() - h1);

        if (x1 != x || y1 != y || w1 != w || h1 != h) {
            setLocation(new Location(
                    MathHelper.ceiling_double_int(x1 / scale),
                    MathHelper.ceiling_double_int(y1 / scale),
                    MathHelper.ceiling_double_int(w1 / scale),
                    MathHelper.ceiling_double_int(h1 / scale)));
        }
        super.updateComponent();
    }

    @Override
    public void setLocation(ILocation location) {
        super.setLocation(location);
        // save bounds
        TabbySettings sett = TabbyChat.getInstance().settings;
        sett.advanced.chatX.set(location.getXPos());
        sett.advanced.chatY.set(location.getYPos());
        sett.advanced.chatW.set(location.getWidth());
        sett.advanced.chatH.set(location.getHeight());
        LiteLoader.getInstance().writeConfig(sett);
    }

    @Override
    public void onClosed() {
        super.onClosed();
        updateComponent();
    }

    public int getWidth() {
        return getBounds().width;
    }

    @Override
    public ChatArea getChatArea() {
        return this.chatArea;
    }

    @Override
    public ChatTray getTray() {
        return this.pnlTray;
    }

    @Override
    public TextBox getChatInput() {
        return this.txtChatInput;
    }

    public void onScreenHeightResize(int oldWidth, int oldHeight, int newWidth, int newHeight) {

        if (oldWidth == 0 || oldHeight == 0)
            return; // first time!

        // measure the distance from the bottom, then subtract from new height

        ScaledDimension oldDim = new ScaledDimension(oldWidth, oldHeight);
        ScaledDimension newDim = new ScaledDimension(newWidth, newHeight);

        int bottom = oldDim.getScaledHeight() - getLocation().getYPos();
        int y = newDim.getScaledHeight() - bottom;
        this.setLocation(getLocation().copy().setYPos(y));
        this.updateComponent();
    }

    @Override
    public Rectangle getBounds() {
        return getLocation().asRectangle();
    }

}
