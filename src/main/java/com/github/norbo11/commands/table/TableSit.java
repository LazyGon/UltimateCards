package com.github.norbo11.commands.table;

import java.util.ArrayList;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.MoneyMethods;
import com.github.norbo11.util.NumberMethods;

public class TableSit extends PluginCommand {
    public TableSit() {
        getAlises().add("sit");
        getAlises().add("join");
        getAlises().add("s");

        setDescription("Sits down at the specified table with the specified buy-in. Use 'poker' or 'bj' instead of table ID to join a random poker/bj table.");

        setArgumentString("[table ID/poker/bj] [buyin]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    private CardsTable cardsTable;
    private double buyin;

    @Override
    public boolean conditions() {
        if (getArgs().length != 3) {
            showUsage();
            return false;
        }

        CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
        if (cardsPlayer != null) {
            ErrorMessages.playerSittingAtTable(getPlayer());
            return false;
        }

        String idString = getArgs()[1];

        int id;
        try {
            id = NumberMethods.getPositiveInteger(idString);
        } catch (NumberFormatException exc) {
            return false;
        }

        // Check ID isn't a number
        if (id == -99999) {
            if (CardsTable.isGameType(idString)) {
                ArrayList<CardsTable> eligibleTables = new ArrayList<>();
                for (CardsTable table : CardsTable.getTables()) {
                    if (table instanceof PokerTable && idString.equalsIgnoreCase("poker")) {
                        eligibleTables.add(table);
                    } else if (table instanceof BlackjackTable
                            && (idString.equalsIgnoreCase("bj") || idString.equalsIgnoreCase("blackjack"))) {
                        eligibleTables.add(table);
                    }
                }
                cardsTable = eligibleTables.get(NumberMethods.getRandomInteger(eligibleTables.size() - 1));
            } else {
                ErrorMessages.notGameType(getPlayer());
            }
        } else {
            cardsTable = CardsTable.getTable(id);
        }

        buyin = NumberMethods.getDouble(getArgs()[2]);
        if (buyin == -99999) {
            ErrorMessages.invalidNumber(getPlayer(), getArgs()[2]);
            return false;
        }
        if (cardsTable == null) {
            ErrorMessages.notTable(getPlayer(), getArgs()[1]);
            return false;
        }
        if (cardsTable.getBannedList().contains(getPlayer().getName())) {
            ErrorMessages.playerIsBanned(getPlayer());
            return false;
        }
        if (!cardsTable.isOpen()) {
            ErrorMessages.tableNotOpen(getPlayer(), getArgs()[1]);
            return false;
        }
        if (cardsTable.isInProgress()) {
            ErrorMessages.tableInProgress(getPlayer());
            return false;
        }
        // Check if the buy-in is within the bounds of the table
        if (buyin < cardsTable.getSettings().minBuy.getValue() || buyin > cardsTable.getSettings().maxBuy.getValue()) {
            ErrorMessages.notWithinBuyinBounds(getPlayer(), buyin, cardsTable.getSettings().minBuy.getValue(),
                    cardsTable.getSettings().maxBuy.getValue());
            return false;
        }
        if (!UltimateCards.getInstance().getEconomy().has(getPlayer(), buyin)) {
            ErrorMessages.notEnoughMoney(getPlayer(), buyin,
                    UltimateCards.getInstance().getEconomy().getBalance(getPlayer()));
            return false;
        }

        return true;
    }

    @Override
    public void perform() throws Exception {
        if (getPlayer().hasPermission("ucards") || getPlayer().hasPermission("ucards.cards") || getPlayer().hasPermission("ucards.cards.teleport")) {
            getPlayer().teleport(cardsTable.getSettings().startLocation.getValue());
        }

        MoneyMethods.withdrawMoney(getPlayer(), buyin);

        boolean isOwner = cardsTable.getOwner().equalsIgnoreCase(getPlayer().getName());

        cardsTable.sendTableMessage("&6" + getPlayer().getName() + "&f" + (isOwner ? " (Owner)" : "") + " sits at &6" + cardsTable.getName() + " &f(" + Formatter.formatMoneyWithoutColor(buyin) + ")");

        CardsPlayer cardsPlayer = cardsTable.playerSit(getPlayer(), buyin);
        if (isOwner) {
            cardsTable.setOwnerPlayer(cardsPlayer);
        }

        if (cardsTable instanceof PokerTable) {
            PokerTable pokerTable = (PokerTable) cardsTable;
            pokerTable.autoStart();
        }
    }
}
