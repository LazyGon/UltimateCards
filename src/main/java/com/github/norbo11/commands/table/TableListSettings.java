package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.cards.TableSetting;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;

public class TableListSettings extends PluginCommand {
    public TableListSettings() {
        getAlises().add("listsettings");
        getAlises().add("ls");

        setDescription("Lists all available settings for this table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    private CardsTable cardsTable;

    @Override
    public boolean conditions() {
        if (getArgs().length != 1) {
            showUsage();
            return false;
        }

        CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
        if (cardsPlayer == null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }

        cardsTable = cardsPlayer.getTable();
        return true;
    }

    // Lists all valid setting types to the player.
    @Override
    public void perform() {
        TableSetting<?>[] specificSettings = null;

        if (cardsTable instanceof PokerTable) {
            Messages.sendMessage(getPlayer(), "&fAvailable poker settings:");
            specificSettings = ((PokerTable) cardsTable).getSettings().allSettings;
        } else if (cardsTable instanceof BlackjackTable) {
            Messages.sendMessage(getPlayer(), "&fAvailable blackjack settings:");
            specificSettings = ((BlackjackTable) cardsTable).getSettings().allSettings;
        }

        for (TableSetting<?> setting : specificSettings) {
            Messages.sendMessage(getPlayer(), setting.getHelpString());
        }

        Messages.sendMessage(getPlayer(), "&fAvailable general settings:");
        for (TableSetting<?> setting : cardsTable.getSettings().allSettings) {
            Messages.sendMessage(getPlayer(), setting.getHelpString());
        }

        Messages.sendMessage(getPlayer(), "&cUsage: &6/table set [setting] [value]");

        
                /*if (cardsTable instanceof PokerTable) {
                Messages.sendMessage(getPlayer(), "&6" + UltimateCards.LINE_STRING);
                Messages.sendMessage(getPlayer(), ");
                Messages.sendMessage(getPlayer(), );
                Messages.sendMessage(getPlayer(), );
                Messages.sendMessage(getPlayer(), );
                Messages.sendMessage(getPlayer(), );
                Messages.sendMessage(getPlayer(), );
                Messages.sendMessage(getPlayer(), );
            } else if (cardsTable instanceof BlackjackTable) {
                Messages.sendMessage(getPlayer(), "&6" + UltimateCards.LINE_STRING);
                Messages.sendMessage(getPlayer(), );
                Messages.sendMessage(getPlayer(), );
            }
            Messages.sendMessage(getPlayer(), "&6" + UltimateCards.LINE_STRING);
        
            Messages.sendMessage(getPlayer(), );
            Messages.sendMessage(getPlayer(), );
            Messages.sendMessage(getPlayer(), );
            Messages.sendMessage(getPlayer(), );
            Messages.sendMessage(getPlayer(), );
            Messages.sendMessage(getPlayer(), );
            Messages.sendMessage(getPlayer(), );*/

    }
}
