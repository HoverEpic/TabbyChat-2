package mnm.mods.tabbychat.gui.settings;

import static mnm.mods.tabbychat.util.Translation.*;

import java.text.NumberFormat;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import mnm.mods.tabbychat.TabbyChat;
import mnm.mods.tabbychat.settings.GeneralSettings;
import mnm.mods.tabbychat.settings.TabbySettings;
import mnm.mods.tabbychat.util.TimeStamps;
import mnm.mods.util.Color;
import mnm.mods.util.gui.GuiGridLayout;
import mnm.mods.util.gui.GuiLabel;
import mnm.mods.util.gui.config.GuiSettingBoolean;
import mnm.mods.util.gui.config.GuiSettingEnum;
import mnm.mods.util.gui.config.GuiSettingNumber.GuiSettingDouble;
import mnm.mods.util.gui.config.SettingPanel;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class GuiSettingsGeneral extends SettingPanel<TabbySettings> {

    public GuiSettingsGeneral() {
        setLayout(Optional.of(new GuiGridLayout(10, 20)));
        setDisplayString(I18n.format(SETTINGS_GENERAL));
        setSecondaryColor(Color.of(255, 0, 255, 64));
    }

    @Override
    public void initGUI() {
        GeneralSettings sett = getSettings().general;

        int pos = 1;
        addComponent(new GuiLabel(new TextComponentTranslation(LOG_CHAT)), new int[] { 2, pos });
        GuiSettingBoolean chkLogChat = new GuiSettingBoolean(sett.logChat);
        chkLogChat.setCaption(new TextComponentTranslation(LOG_CHAT_DESC));
        addComponent(chkLogChat, new int[] { 1, pos });

        addComponent(new GuiLabel(new TextComponentTranslation(SPLIT_LOG)), new int[] { 7, pos });
        GuiSettingBoolean chkSplitLog = new GuiSettingBoolean(sett.splitLog);
        chkSplitLog.setCaption(new TextComponentTranslation(SPLIT_LOG_DESC));
        addComponent(chkSplitLog, new int[] { 6, pos });

        pos += 2;
        addComponent(new GuiLabel(new TextComponentTranslation(TIMESTAMP)), new int[] { 2, pos });
        addComponent(new GuiSettingBoolean(sett.timestampChat), new int[] { 1, pos });

        pos += 2;
        addComponent(new GuiLabel(new TextComponentTranslation(TIMESTAMP_STYLE)), new int[] { 3, pos });
        addComponent(new GuiSettingEnum<TimeStamps>(sett.timestampStyle, TimeStamps.values()), new int[] { 5, pos, 4, 1 });

        pos += 2;
        addComponent(new GuiLabel(new TextComponentTranslation(TIMESTAMP_COLOR)), new int[] { 3, pos });
        addComponent(new GuiSettingEnum<TextFormatting>(sett.timestampColor, getColors(), getColorNames()), new int[] { 5, pos, 4, 1 });

        pos += 2;
        addComponent(new GuiLabel(new TextComponentTranslation(ANTI_SPAM)), new int[] { 2, pos });
        GuiSettingBoolean chkSpam = new GuiSettingBoolean(sett.antiSpam);
        chkSpam.setCaption(new TextComponentTranslation(ANTI_SPAM_DESC));
        addComponent(chkSpam, new int[] { 1, pos });

        pos += 2;
        addComponent(new GuiLabel(new TextComponentTranslation(SPAM_PREJUDICE)), new int[] { 3, pos });
        GuiSettingDouble nud = new GuiSettingDouble(sett.antiSpamPrejudice);
        nud.getComponent().setMin(0);
        nud.getComponent().setMax(1);
        nud.getComponent().setInterval(0.05);
        nud.getComponent().setFormat(NumberFormat.getPercentInstance());
        nud.setCaption(new TextComponentTranslation(SPAM_PREJUDICE_DESC));
        addComponent(nud, new int[] { 6, pos, 2, 1 });

        pos += 2;
        addComponent(new GuiLabel(new TextComponentTranslation(UNREAD_FLASHING)), new int[] { 2, pos });
        addComponent(new GuiSettingBoolean(sett.unreadFlashing), new int[] { 1, pos });

        pos += 2;
        addComponent(new GuiLabel(new TextComponentTranslation(CHECK_UPDATES)), new int[] { 2, pos });
        addComponent(new GuiSettingBoolean(sett.checkUpdates), new int[] { 1, pos });
    }

    private static TextFormatting[] getColors() {
        return Sets.newHashSet(TextFormatting.values()).stream()
                .filter(input -> input.isColor())
                .toArray(TextFormatting[]::new);
    }

    private static String[] getColorNames() {
        return Lists.newArrayList(getColors()).stream()
                .map(input -> "colors." + input.getFriendlyName())
                .toArray(String[]::new);
    }

    @Override
    public TabbySettings getSettings() {
        return TabbyChat.getInstance().settings;
    }

}
