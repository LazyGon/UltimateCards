package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.config.SavedTables;

public class TableUnsave extends PluginCommand {
    public TableUnsave() {
        getAlises().add("unsave");

        setDescription("Deletes a saved table permanently.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    private CardsTable cardsTable;

    // table create name buyin poker|blackjack
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
        if (cardsTable.isOwner(cardsPlayer.getPlayerName())) {
            ErrorMessages.playerNotOwner(getPlayer());
            return false;
        }

        return true;
    }

    @Override
    public void perform() throws Exception {
        Messages.sendMessage(getPlayer(), "&cUnsaving table &6[" + cardsTable.getId() + "] " + cardsTable.getName() + "&c...");
        SavedTables.unsaveTable(cardsTable);
        Messages.sendMessage(getPlayer(), "&cTable unsaved.");
    }
}
