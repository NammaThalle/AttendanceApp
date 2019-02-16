package max.project.Data;

/**
 * Created by max on 28/4/17.
 */

public class AttendanceCard {

    private String idno;
    private String regno;
    private String name;
    /* private String days;
     private String percent;*/
    private boolean flag;


    public AttendanceCard(String idno, String regno, String name,/* String days, String percent, */boolean flag) {
        this.idno = idno;
        this.regno = regno;
        this.name = name;
        /*this.days = days;
        this.percent = percent;*/
        this.flag = flag;
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }*/

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
