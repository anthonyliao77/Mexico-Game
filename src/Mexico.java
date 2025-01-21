
import java.util.Random;
import java.util.SplittableRandom;
import java.util.Scanner;

import static java.lang.System.*;

/*
 *  The Mexico dice game
 *  See https://en.wikipedia.org/wiki/Mexico_(game)
 *
 */
public class Mexico {

    public static void main(String[] args) {
        new Mexico().program();
    }

    final SplittableRandom rand = new SplittableRandom();
    final Scanner sc = new Scanner(in);
    final int maxRolls = 3; // No player may exceed this
    final int startAmount = 3; // Money for a player. Select any
    final int mexico = 1000; // A value greater than any other

    void program() {
        // test(); // <----------------- UNCOMMENT to test

        int pot = 0; // What the winner will get
        Player[] players; // The players (array of Player objects)
        Player current; // Current player for round
        Player leader; // Player starting the round

        players = getPlayers();
        current = getRandomPlayer(players);
        leader = current;

        out.println("Mexico Game Started");
        statusMsg(players);

        while (players.length > 1) { // Game over when only one player left
            clearRound(players);
            int leaderRolls = 0;
            leader = current;

            // ----- In ----------
            while (true) {
                if (allRolled(players)) {
                    Player loser = getLoser(players);
                    loser.amount--;
                    pot++; // Add to pot when a player loses a round
                    out.println(loser.name + " lost the round and now has " + loser.amount + " left.");

                    if (loser.amount == 0) {
                        players = removeLoser(players, loser);  // Remove players with no money left
                        if (players.length == 1) {
                            break; // Game over when only one player left
                        }
                        current = next(players, current);  // Update current player after removal
                        leader = current;  // New leader for next round
                    } else {
                        leader = next(players, leader);  // Set new leader if game continues
                    }
                    clearRound(players);  // Reset rolls for a new round
                    break;
                }

                String cmd = getPlayerChoice(current);
                if ("r".equals(cmd)) {
                    if (current.nRolls >= maxRolls || (current != leader && current.nRolls >= leaderRolls)) {
                        out.println("Only one roll per round buddy");
                        current = next(players, current);
                    } else {
                        rollDice(current);
                        current.nRolls++;
                        roundMsg(current);

                        if (current == leader) {
                            leaderRolls = current.nRolls;
                        }
                    }
                } else if (current.nRolls >= 1 && "n".equals(cmd)) {
                    current = next(players, current);
                } else {
                    out.println("?");
                }
            }
            statusMsg(players); // Display player status after each round
        }

        out.println("Game Over, winner is " + players[0].name + ". Wins the pot of " + (pot + 1));
        // we add one, since condition is not true, someone has lost the last round and the loop ends
    }

    // ---- Game logic methods --------------

    // TODO implement and test methods (one at the time)

    int indexOf(Player[] players, Player player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == player) {
                return i;
            }
        }
        return -1;
    }

    Player getRandomPlayer(Player[] players) {
        return players[rand.nextInt(players.length)];
    }

    Player rollDice(Player player) {
        Random rand = new Random();
        player.fstDice = (rand.nextInt(6) + 1);
        player.secDice = (rand.nextInt(6) + 1);
        return player;
    }

    int getScore(Player player) {
        int fst = player.fstDice;
        int sec = player.secDice;
        // mexico, dubbel siffra, normal
        if (fst == 2 && sec == 1 || fst == 1 && sec == 2) {
            return (Math.max(fst, sec) * 10 + Math.min(fst, sec));
        } else if (fst == sec) {
            return (fst * 10 + sec);
        } else {
            return (Math.max(fst, sec) * 10 + Math.min(fst, sec));
        }
    }

    Player getLoser(Player[] players) {
        int loserIndex = 0;
        int loserScore = getScore(players[0]);
        for (int i = 1; i < players.length; i++) {
            int score = getScore(players[i]);
            if (score < loserScore) {
                loserScore = score;
                loserIndex = i;
            }
        }
        return players[loserIndex];
    }

    Player[] removeLoser(Player[] players, Player loser) {
        Player[] newList = new Player[players.length - 1];
        int newIndex = 0;

        for (int i = 0; i < players.length; i++) {
            if (players[i] != loser) {
                newList[newIndex++] = players[i];
            }
        }
        return newList;
    }

    Player next(Player[] players, Player current) {
        int nextPlayer = indexOf(players, current);
        return players[(nextPlayer + 1) % players.length];
    }

    boolean allRolled(Player[] players) {
        int rolls = players[0].nRolls;
        for (Player player : players) {
            if (player.nRolls == 0 || player.nRolls != rolls) {
                return false;
            }
        }
        return true;
    }

    void clearRound(Player[] players) {
        for (Player clearedPlayer : players) {
                clearedPlayer.fstDice = 0;
                clearedPlayer.secDice = 0;
                clearedPlayer.nRolls = 0;
        }
    }

    // ---------- IO methods (nothing to do here) -----------------------

    Player[] getPlayers() {
        Player[] players = new Player[3];
        players[0] = new Player("Olle", startAmount, 0, 0, 0);
        players[1] = new Player("Fia", startAmount, 0, 0, 0);
        players[2] = new Player("Lisa", startAmount, 0, 0, 0);
        return players;
    }

    void statusMsg(Player[] players) {
        out.print("Status: ");
        for (Player player : players) {
            out.print(player.name + " " + player.amount + " ");
        }
        out.println();
    }

    void roundMsg(Player current) {
        out.println(current.name + " got " + current.fstDice + " and " + current.secDice);
    }

    String getPlayerChoice(Player player) {
        out.print("Player is " + player.name + " > ");
        return sc.nextLine();
    }

    // Possibly useful utility during development
    String toString(Player p) {
        return p.name + ", " + p.amount + ", " + p.fstDice + ", " + p.secDice + ", " + p.nRolls;
    }

    // Class for a player
    public class Player {
        String name;
        int amount; // Start amount (money)
        int fstDice; // Result of first dice
        int secDice; // Result of second dice
        int nRolls; // Current number of rolls

        public Player(String name, int amount, int fstDice, int secDice, int nRolls) {
            this.name = name;
            this.amount = amount;
            this.fstDice = fstDice;
            this.secDice = secDice;
            this.nRolls = nRolls;
        }
    }
}



    /**************************************************
     * Testing
     *
     * Test are logical expressions that should evaluate to true (and then be
     * written out) No testing of IO methods Uncomment in program() to run test
     * (only)
     ***************************************************/

/*
    void test() {
        // A few hard coded player to use for test
        // NOTE: Possible to debug tests from here, very efficient!

        Player[] ps = {new Player(), new Player(), new Player()};
        ps[0].fstDice = 2;
        ps[0].secDice = 6;

        ps[1].fstDice = 6;
        ps[1].secDice = 5;

        ps[2].fstDice = 6;
        ps[2].secDice = 5;

        out.println(getScore(ps[0]) == 62);
        out.println(getScore(ps[1]) == 65);
        out.println(next(ps, ps[0]) == ps[1]);
        out.println(getLoser(ps) == ps[0]);

        exit(0);
    }

}
*/