package com.esheejo;

import com.esheejo.crypto.movement.MovementScraper;
import com.esheejo.crypto.newcoins.NewCoinsScraper;

/**
 * Created by deemoshea on 16/01/2018.
 */
public class Main {

    public static void main(String[] args) {

        //MovementScraper movementScrape = new MovementScraper();
        //movementScrape.runMovementCheck();

        NewCoinsScraper ncs = new NewCoinsScraper();
                ncs.runCoinCheck();

    }

}
