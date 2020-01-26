package ir.ac.sbu.riskai;

import java.util.ArrayList;
import java.util.List;

public class PlayerModel
{
    private int playerNumber;
    private boolean playerMode; // true for AI and false for not AI
    private List<TerritoryModel> territories;
    private boolean isAlive;
    private int numberOfIncreases;

    public PlayerModel()
    {
        isAlive = true;
    }

    public String getName()
    {
        String name = "PL";
        name = name.concat(String.valueOf(playerNumber));
        return name;
    }

    public int getPlayerNumber()
    {
        return playerNumber;
    }

    public List<TerritoryModel> getTerritories()
    {
        return territories;
    }

    public boolean isPlayerMode()
    {
        return playerMode;
    }

    public void setPlayerMode(boolean playerMode)
    {
        this.playerMode = playerMode;
    }

    public void setPlayerNumber(int playerNumber)
    {
        this.playerNumber = playerNumber;
    }

    public boolean isAlive()
    {
        return isAlive;
    }

    public void setAlive(boolean alive)
    {
        isAlive = alive;
    }

    public void addTerritory(TerritoryModel territory)
    {
        if (territories == null)
        {
            territories = new ArrayList<>();
        }
        territories.add(territory);
    }

    public void removeTerritory(TerritoryModel territory)
    {
        territories.remove(territory);
        if (territories.size() == 0)
        {
            isAlive = false;
        }
    }

    public int getNumberOfIncreases()
    {
        return numberOfIncreases;
    }

    public void setNumberOfIncreases(int numberOfIncreases)
    {
        this.numberOfIncreases = numberOfIncreases;
    }
}
