package Model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Project {
    // Attributes
    private int id;
    private int owner;
    private String url;
    private String title;
    private List<Milestone> milestones;

    // Constructor with parameters
    public Project(int id, String title, String url, int owner, List<Milestone> milestones) {
        this.title = title;
        this.owner = owner;
        this.url = url;
        this.id = id;
        this.milestones = milestones;
    }

    // Getters
    public String getTitle() {
        return this.title;
    }
    public int getOwner() {
        return this.owner;
    }
    public int getId() { return this.id; }
    public String getURL() { return this.url; }
    public List<Milestone> getMilestones() { return this.milestones; }

}