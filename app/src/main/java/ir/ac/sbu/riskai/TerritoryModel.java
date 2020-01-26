package ir.ac.sbu.riskai;

import java.util.Objects;

public class TerritoryModel
{
    private int troops;
    private int hostPlayer;
    private int row;
    private int col;
    private int id;

    public TerritoryModel(int troops, int hostPlayer, int row, int col, int id)
    {
        this.troops = troops;
        this.hostPlayer = hostPlayer;
        this.row = row;
        this.col = col;
        this.id = id;
    }

    public int getTroops()
    {
        return troops;
    }

    public void setTroops(int troops)
    {
        this.troops = troops;
    }

    public int getHostPlayer()
    {
        return hostPlayer;
    }

    public void setHostPlayer(int hostPlayer)
    {
        this.hostPlayer = hostPlayer;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TerritoryModel that = (TerritoryModel) o;
        return row == that.row &&
                col == that.col;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(row, col);
    }

    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
