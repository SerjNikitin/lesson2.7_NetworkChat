package client;

public class Entry {
    private String login;
    private String password;
    private String name;

    public Entry(String login, String password, String name) {
        this.login = login;
        this.password = password;
        this.name = name;
    }

    public Entry() {
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
