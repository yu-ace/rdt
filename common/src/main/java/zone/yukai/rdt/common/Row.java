package zone.yukai.rdt.common;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private List<Column> rowList = new ArrayList<>();

    public List<Column> getRowList() {
        return rowList;
    }

    public void setRowList(List<Column> rowList) {
        this.rowList = rowList;
    }

    public void addColumn(Column column){
        rowList.add(column);
    }
}
