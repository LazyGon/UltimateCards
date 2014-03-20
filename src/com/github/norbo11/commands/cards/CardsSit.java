package com.github.norbo11.commands.cards;

import java.util.ArrayList;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.MoneyMethods;
import com.github.norbo11.util.NumberMethods;

public class CardsSit extends PluginCommand {
    public CardsSit() {
        getAlises().add("sit");
        getAlises().add("join");
        getAlises().add("s");

        setDescription("Sits down at the specified table with the specified buy-in. Use 'poker' or 'bj' instead of table ID to join a random poker/bj table.");

        setArgumentString("[table ID/poker/bj] [buyin]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    CardsTable cardsTable;
    double buyin;

    @Override
    public boolean conditions() {
        if (getArgs().length == 3) {
            CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer == null) {
                String idString = getArgs()[1];
                int id = NumberMethods.getPositiveInteger(idString);
                // Check ID isn't a number
                if (id == -99999) {
                    id = NumberMethods.getInteger(idString);
                    if (CardsTable.isGameType(idString)) {
                        ArrayList<CardsTable> eligibleTables = new ArrayList<CardsTable>();
                        for (CardsTable table : CardsTable.getTables()) {
                            if (table instanceof PokerTable && idString.equalsIgnoreCase("poker")) eligibleTables.add(table);
                            else if (table instanceof BlackjackTable && (idString.equalsIgnoreCase("bj") || idString.equalsIgnoreCase("blackjack"))) eligibleTables.add(table);
                        }
                        cardsTable = eligibleTables.get(NumberMethods.getRandomInteger(eligibleTables.size() - 1));
                    } else {
                        ErrorMessages.notGameType(getPlayer());
                    }
                } else {
                    cardsTable = CardsTable.getTable(id);
                }
            
                buyin = NumberMethods.getDouble(getArgs()[2]);
                if (buyin != -99999) {
                    if (cardsTable != null) {
                        if (!cardsTable.getBannedList().contains(getPlayer().getName())) {
                            if (cardsTable.isOpen()) {
                                if (!cardsTable.isInProgress()) {
                                    // Check if the buy-in is within the bounds of the table
                                    if (buyin >= cardsTable.getSettings().getMinBuy() && buyin <= cardsTable.getSettings().getMaxBuy()) {
                                        if (UltimateCards.getEconomy().has(getPlayer().getName(), buyin)) return true;
                                        else {
                                            ErrorMessages.notEnoughMoney(getPlayer(), buyin, UltimateCards.getEconomy().getBalance(getPlayer().getName()));
                                        }
                                    } else {
                                        ErrorMessages.notWithinBuyinBounds(getPlayer(), buyin, cardsTable.getSettings().getMinBuy(), cardsTable.getSettings().getMaxBuy());
                                    }
                                } else {
                                    ErrorMessages.tableInProgress(getPlayer());
                                }
                            } else {
                                ErrorMessages.tableNotOpen(getPlayer(), getArgs()[1]);
                            }
                        } else {
                            ErrorMessages.playerIsBanned(getPlayer());
                        }
                    } else {
                        ErrorMessages.notTable(getPlayer(), getArgs()[1]);
                    }
                } else {
                    ErrorMessages.invalidNumber(getPlayer(), getArgs()[2]);
                }
            } else {
                ErrorMessages.playerSittingAtTable(getPlayer());
            }
        } else {
            showUsage();
        }
        return false;
    }

    @Override
    public void perform() throws Exception {
        if (PluginExecutor.cardsTeleport.hasPermission(getPlayer())) {
            getPlayer().teleport(cardsTable.getLocation());
        }

        MoneyMethods.withdrawMoney(getPlayer().getName(), buyin);

        boolean isOwner = cardsTable.getOwner().equalsIgnoreCase(getPlayer().getName());

        cardsTable.sendTableMessage("&6" + getPlayer().getName() + "&f" + (isOwner ? " (Owner)" : "") + " has sat down at &6" + cardsTable.getName() + "&f with &6" + Formatter.formatMoney(buyin));

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
