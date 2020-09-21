package Model;

import java.util.List;
import java.util.ArrayList;
import java.sql.Date;

public class Milestone {
    // Attributes
    private int id;
    private String description;
    private Date intendedDueDate;
    private Date actualCompletionDate;

    // Constructor with parameters
    public Milestone(int id, String description, Date intendedDueDate, Date actualCompletionDate) {
        this.id = id;
        this.description = description;
        this.intendedDueDate = intendedDueDate;
        this.actualCompletionDate = actualCompletionDate;
    }

    // Getters
    public int getId(){ return this.id; }
    public String getDescription() {
        return this.description;
    }
    public Date getIntendedDueDate() {
        return this.intendedDueDate;
    }
    public Date getActualCompletionDate() {
        return this.actualCompletionDate;
    }

}
