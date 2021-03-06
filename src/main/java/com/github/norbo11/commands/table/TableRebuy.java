package com.github.norbo11.commands.table;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.MoneyMethods;
import com.github.norbo11.util.NumberMethods;

public class TableRebuy extends PluginCommand {
    public TableRebuy() {
        getAlises().add("rebuy");
        getAlises().add("addmoney");
        getAlises().add("addstack");
        getAlises().add("r");

        setDescription("Adds more money to your stack.");

        setArgumentString("[amount]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    private CardsPlayer cardsPlayer;
    private CardsTable cardsTable;

    private double amount;

    @Override
    public boolean conditions() {
        if (getArgs().length != 2) {
            showUsage();
            return false;
        }

        cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
        if (cardsPlayer != null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }

        cardsTable = cardsPlayer.getTable();
        if (cardsTable.getSettings().allowRebuys.getValue()) {
            ErrorMessages.tableDoesntAllowRebuys(getPlayer());
            return false;
        }
        if (cardsTable.isInProgress()) {
            ErrorMessages.tableInProgress(getPlayer());
            return false;
        }

        amount = NumberMethods.getDouble(getArgs()[1]);
        if (amount == -99999) {
            ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
            return false;
        }
        if (!UltimateCards.getInstance().getEconomy().has(getPlayer(), amount)) {
            ErrorMessages.notEnoughMoney(getPlayer(), amount,
                    UltimateCards.getInstance().getEconomy().getBalance(getPlayer()));
            return false;
        }

        return true;
    }

    // Adds money to the specified player
    @Override
    public void perform() {
        // Withdraw the desired amount from the ECONOMY, add it to their stack, then display the message
        MoneyMethods.withdrawMoney(getPlayer(), amount);
        cardsPlayer.setMoney(cardsPlayer.getMoney() + amount);

        cardsTable.sendTableMessage("&6" + getPlayer().getName() + "&f has added &6" + Formatter.formatMoney(amount) + "&f to his stack. New balance: &6" + Formatter.formatMoney(cardsPlayer.getMoney()));
        if (cardsTable instanceof PokerTable) {
            PokerTable pokerTable = (PokerTable) cardsTable;
            pokerTable.autoStart();
        }
    }
}
