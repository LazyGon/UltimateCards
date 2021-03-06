package com.github.norbo11.commands.table;

import org.bukkit.Location;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.MoneyMethods;

public class TableLeave extends PluginCommand {
    public TableLeave() {
        getAlises().add("leave");
        getAlises().add("getup");
        getAlises().add("stand");
        getAlises().add("standup");

        setDescription("Leaves the table that you are currently sitting at.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    private CardsTable cardsTable;
    private CardsPlayer cardsPlayer;

    private double money;

    @Override
    public boolean conditions() {
        if (getArgs().length != 1) {
            showUsage();
            return false;
        }

        cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
        if (cardsPlayer == null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }

        cardsTable = cardsPlayer.getTable();
        money = cardsPlayer.getMoney();
        return true;
    }

    // Deletes the specified player from the table, if they are currently sitting at one. Doesnt allow the owner to leave
    @Override
    public void perform() {
        cardsTable.playerLeave(cardsPlayer);

        MoneyMethods.depositMoney(getPlayer(), money);

        // Message
        cardsTable.sendTableMessage("&6" + getPlayer().getName() + "&f has left the table with " + "&6" + Formatter.formatMoney(money));

        // Teleport
        Location leaveLocation = cardsTable.getSettings().leaveLocation.getValue();
        if (leaveLocation != null) {
            cardsPlayer.getPlayer().teleport(leaveLocation);
        } else {
            cardsPlayer.getPlayer().teleport(cardsPlayer.getStartLocation());
        }

        // Remove player
        cardsTable.removePlayer(cardsPlayer);
    }
}
