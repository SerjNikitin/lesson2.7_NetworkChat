package dataBase;

import client.Entry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class RequestDB {

    public Optional<Entry> findUser(String login, String password) {
        try (Connection connection = ConnectionDB.getConnection()) {
            PreparedStatement preparedStatement = connection.
                    prepareStatement("SELECT*FROM book WHERE login=? AND password=?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(
                        new Entry(
                                resultSet.getString("login"),
                                resultSet.getString("password"),
                                resultSet.getString("name"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void save(String login, String password, String name) {
        Connection connection = ConnectionDB.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.
                    prepareStatement("INSERT INTO networkchat SET(login, password, name) VALUES (?,?,?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, name);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {
            close(connection);
        }
    }

    public void update(String name, String login) {
        Connection connection = ConnectionDB.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.
                    prepareStatement("UPDATE networkchat SET name =? WHERE login=?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, login);
            preparedStatement.execute();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                e.printStackTrace();
            }
        } finally {
            close(connection);
        }
    }

    public void delete(String login) {
        Connection connection = ConnectionDB.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.
                    prepareStatement("DELETE*FROM networkchat WHERE login=?");
            preparedStatement.setString(1, login);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection);
        }
    }

    public void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
