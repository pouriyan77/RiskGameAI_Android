package ir.ac.sbu.riskai;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class AiPlayer extends PlayerModel
{
    private List<TerritoryModel> borderTerritories;
    private TerritoryModel[][] territories;
    private Queue<TerritoryWithCriterion> sortedTerritories;

    public AiPlayer(TerritoryModel[][] territories)
    {
        super();
        this.territories = territories;
    }

    public TerritoryModel deployFirstTime(boolean isFirst)
    {
        if (borderTerritories == null || !isFirst)
        {
            obtainBorders();
            sortedTerritories = new PriorityQueue<>((o1, o2) ->
                    o2.getCriterion() - o1.getCriterion());

            for (TerritoryModel territory : borderTerritories)
            {
                sortedTerritories.add(getTerritoryWithCriterion(territory, territory.getHostPlayer()));
            }
        }
        return territories[sortedTerritories.peek().row][sortedTerritories.peek().col];
    }

    public List<TerritoryModel> deploy(int count)
    {
        List<TerritoryModel> territoryList = new ArrayList<>();
        for (int i = 0 ; i < count ; i++)
        {
            territoryList.add(deployFirstTime(false));
        }
        return territoryList;
    }

    public AttackProperties attack()
    {
        obtainBorders();
        Queue<TerritoryModel> sortedTerritoriesForAttack = new PriorityQueue<>((o1, o2) ->
                o1.getTroops() - o2.getTroops());

        for (TerritoryModel territory : borderTerritories)
        {
            if (territory.getTroops() > 1)
            {
                sortedTerritoriesForAttack.add(territory);
            }
        }
        if(sortedTerritoriesForAttack.isEmpty())
        {
            return null;
        }else
        {
            TerritoryModel attackerTerritory = sortedTerritoriesForAttack.peek();
            TerritoryModel defenderTerritory = obtainDefenderTerritory(attackerTerritory);
            int attackTroops = attackerTerritory.getTroops() - 1;
            TerritoryWithCriterion territoryWithCriterion = getTerritoryWithCriterion(
                    defenderTerritory, attackerTerritory.getHostPlayer());
            if (territoryWithCriterion.enemies == 0)
            {
                int suggestedTroops = defenderTerritory.getTroops() + 2;
                if (suggestedTroops <= attackTroops)
                {
                    attackTroops = suggestedTroops;
                }
            }
            return new AttackProperties(attackerTerritory, defenderTerritory, attackTroops);
        }
    }
    private TerritoryModel obtainDefenderTerritory(TerritoryModel attackerTerritory)
    {
        List<TerritoryModel> defenderList = getDefenders(attackerTerritory);
        Queue<TerritoryWithCriterion> sortedDefenderTerritories = new PriorityQueue<>((o1, o2) ->
                o2.getCriterion() - o1.getCriterion());
        for (TerritoryModel defenderTerritory : defenderList)
        {
            sortedDefenderTerritories.add(getTerritoryWithCriterion(defenderTerritory, attackerTerritory.getHostPlayer()));
        }
        return territories[sortedDefenderTerritories.peek().row][sortedDefenderTerritories.peek().col];
    }

    private List<TerritoryModel> getDefenders(TerritoryModel attackerTerritory)
    {
        List<TerritoryModel> defenderList = new ArrayList<>();
        int row = attackerTerritory.getRow();
        int col = attackerTerritory.getCol();

        if (isEnemy(row - 1, col - 1, attackerTerritory.getHostPlayer()))
        {
            defenderList.add(territories[row-1][col-1]);
        }
        if (isEnemy(row - 1, col, attackerTerritory.getHostPlayer()))
        {
            defenderList.add(territories[row-1][col]);
        }
        if (isEnemy(row - 1, col + 1, attackerTerritory.getHostPlayer()))
        {
            defenderList.add(territories[row-1][col+1]);
        }
        if (isEnemy(row, col - 1, attackerTerritory.getHostPlayer()))
        {
            defenderList.add(territories[row][col-1]);
        }
        if (isEnemy(row, col + 1, attackerTerritory.getHostPlayer()))
        {
            defenderList.add(territories[row][col+1]);
        }
        if (isEnemy(row + 1, col - 1, attackerTerritory.getHostPlayer()))
        {
            defenderList.add(territories[row+1][col-1]);
        }
        if (isEnemy(row + 1, col, attackerTerritory.getHostPlayer()))
        {
            defenderList.add(territories[row+1][col]);
        }
        if (isEnemy(row + 1, col + 1, attackerTerritory.getHostPlayer()))
        {
            defenderList.add(territories[row+1][col+1]);
        }
        return defenderList;
    }

    private TerritoryWithCriterion getTerritoryWithCriterion(TerritoryModel territory, int hostPlayer)
    {
        int row = territory.getRow();
        int col = territory.getCol();
        TerritoryWithCriterion territoryWithCriterion = new TerritoryWithCriterion(row, col);

        calculateEnemies(territoryWithCriterion, territory, hostPlayer);
        calculateFriends(territoryWithCriterion, territory, hostPlayer);

        return territoryWithCriterion;
    }

    private void calculateFriends(TerritoryWithCriterion territoryWithCriterion,
                                  TerritoryModel territory, int hostPlayer)
    {
        int row = territory.getRow();
        int col = territory.getCol();

        if (isFriend(row - 1, col - 1, hostPlayer))
        {
            territoryWithCriterion.friends++;
        }
        if (isFriend(row - 1, col, hostPlayer))
        {
            territoryWithCriterion.friends++;
        }
        if (isFriend(row - 1, col + 1, hostPlayer))
        {
            territoryWithCriterion.friends++;
        }
        if (isFriend(row, col - 1, hostPlayer))
        {
            territoryWithCriterion.friends++;
        }
        if (isFriend(row, col + 1, hostPlayer))
        {
            territoryWithCriterion.friends++;
        }
        if (isFriend(row + 1, col - 1, hostPlayer))
        {
            territoryWithCriterion.friends++;
        }
        if (isFriend(row + 1, col, hostPlayer))
        {
            territoryWithCriterion.friends++;
        }
        if (isFriend(row + 1, col + 1, hostPlayer))
        {
            territoryWithCriterion.friends++;
        }
    }

    private void calculateEnemies(TerritoryWithCriterion territoryWithCriterion,
                                 TerritoryModel territory, int hostPlayer)
    {
        int row = territory.getRow();
        int col = territory.getCol();

        if (isEnemy(row - 1, col - 1, hostPlayer))
        {
            territoryWithCriterion.enemies++;
        }
        if (isEnemy(row - 1, col, hostPlayer))
        {
            territoryWithCriterion.enemies++;
        }
        if (isEnemy(row - 1, col + 1, hostPlayer))
        {
            territoryWithCriterion.enemies++;
        }
        if (isEnemy(row, col - 1, hostPlayer))
        {
            territoryWithCriterion.enemies++;
        }
        if (isEnemy(row, col + 1, hostPlayer))
        {
            territoryWithCriterion.enemies++;
        }
        if (isEnemy(row + 1, col - 1, hostPlayer))
        {
            territoryWithCriterion.enemies++;
        }
        if (isEnemy(row + 1, col, hostPlayer))
        {
            territoryWithCriterion.enemies++;
        }
        if (isEnemy(row + 1, col + 1, hostPlayer))
        {
            territoryWithCriterion.enemies++;
        }
    }

    private void obtainBorders()
    {
        borderTerritories = new ArrayList<>();
        for (TerritoryModel territory : super.getTerritories())
        {
            if (isBorder(territory.getRow(), territory.getCol()))
            {
                borderTerritories.add(territory);
            }
        }
    }

    private boolean isBorder(int row, int col)
    {
        TerritoryModel territory = territories[row][col];

        return isEnemy(row - 1, col - 1, territory.getHostPlayer()) ||
                isEnemy(row - 1, col, territory.getHostPlayer()) ||
                isEnemy(row - 1, col + 1, territory.getHostPlayer()) ||
                isEnemy(row, col - 1, territory.getHostPlayer()) ||
                isEnemy(row, col + 1, territory.getHostPlayer()) ||
                isEnemy(row + 1, col - 1, territory.getHostPlayer()) ||
                isEnemy(row + 1, col, territory.getHostPlayer()) ||
                isEnemy(row + 1, col + 1, territory.getHostPlayer());
    }

    private boolean isEnemy(int row, int col, int hostPlayer)
    {
        try
        {
            if (territories[row][col].getHostPlayer() != hostPlayer)
            {
                return true;
            }
        }catch (ArrayIndexOutOfBoundsException e)
        {
            return false;
        }
        return false;
    }

    private boolean isFriend(int row, int col, int hostPlayer)
    {
        try
        {
            if (territories[row][col].getHostPlayer() == hostPlayer)
            {
                return true;
            }
        }catch (ArrayIndexOutOfBoundsException e)
        {
            return false;
        }
        return false;
    }


    @Override
    public String getName()
    {
        String name = "AI";
        name = name.concat(String.valueOf(super.getPlayerNumber()));
        return name;
    }

    private class TerritoryWithCriterion
    {
        private int row;
        private int col;
        private int enemies;
        private int friends;

        public TerritoryWithCriterion(int row, int col)
        {
            this.row = row;
            this.col = col;
        }

        int getCriterion()
        {
            return friends - enemies;
        }
    }

    class AttackProperties
    {
        private TerritoryModel attackerTerritory;
        private TerritoryModel defenderTerritory;
        private int attackTroops;

        public AttackProperties(TerritoryModel attackerTerritory, TerritoryModel defenderTerritory, int attackTroops)
        {
            this.attackerTerritory = attackerTerritory;
            this.defenderTerritory = defenderTerritory;
            this.attackTroops = attackTroops;
        }

        public TerritoryModel getAttackerTerritory()
        {
            return attackerTerritory;
        }

        public TerritoryModel getDefenderTerritory()
        {
            return defenderTerritory;
        }

        public int getAttackTroops()
        {
            return attackTroops;
        }
    }
}
