/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: Card.java
 * -Class file that holds a simple card
 * -Contains a toString() method which automatically returns a colored string representing the card.
 * ===================================================================================================
 */

package com.github.norbo11.game.cards;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.github.norbo11.UltimateCards;

public class Card
{
    // We read all card images here and put them in a hashmap, so they don't
    // have to be read every single time they are created
    private static HashMap<String, BufferedImage> cardImages = new HashMap<String, BufferedImage>();
    static
    {
        try
        {
            for (int i = 1; i <= 13; i++)
            {
                for (int j = 0; j <= 3; j++)
                {
                    String rank = Integer.toString(i);
                    String suit = Integer.toString(j);
                    cardImages.put(rank + suit, ImageIO.read(UltimateCards.getResourceManager().getResource("images/card" + rank + suit + ".png")));
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private byte rank;
    private byte suit;
    private BufferedImage image;

    public Card(int i, int j)
    {
        rank = (byte) i;
        suit = (byte) j;
        image = cardImages.get(Integer.toString(rank) + Integer.toString(suit));
    }

    public int getBlackjackScore()
    {
        if (isFaceCard()) return 10;
        else return rank;
    }

    public BufferedImage getImage()
    {
        return image;
    }

    public byte getRank()
    {
        return rank;
    }

    public byte getSuit()
    {
        return suit;
    }

    public boolean isFaceCard()
    {
        return rank == 11 || rank == 12 || rank == 13;
    }

    public String rankToString()
    {
        String value = "Error";

        if (rank == 1)
        {
            value = "Ace";
        } else if (rank == 11)
        {
            value = "Jack";
        } else if (rank == 12)
        {
            value = "Queen";
        } else if (rank == 13)
        {
            value = "King";
        } else
        {
            value = Byte.toString(rank);
        }

        return value;
    }

    public String suitToString()
    {
        String suit = "Error";

        if (this.suit == 0)
        {
            suit = "Spades";
        } else if (this.suit == 1)
        {
            suit = "Clubs";
        } else if (this.suit == 2)
        {
            suit = "Hearts";
        } else if (this.suit == 3)
        {
            suit = "Diamonds";
        }

        return suit;
    }

    @Override
    public String toString()
    {
        String returnValue = "Error";

        if (suit == 0) return "&7" + rankToString() + " of " + suitToString() + "&f";
        if (suit == 1) return "&8" + rankToString() + " of " + suitToString() + "&f";
        if (suit == 2) return "&4" + rankToString() + " of " + suitToString() + "&f";
        if (suit == 3) return "&c" + rankToString() + " of " + suitToString() + "&f";

        return returnValue;
    }
}