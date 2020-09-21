package Model;

import lombok.Data;
import lombok.NonNull;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value
public class User {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(User.class);

    private int id;
    private final String email;
    private final String name;

    public User(int id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public int getID() { return this.id; }
    public String getEmail() {
        return this.email;
    }
    public String getName() {
        return this.name;
    }
}
