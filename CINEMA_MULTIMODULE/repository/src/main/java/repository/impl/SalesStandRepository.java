package repository.impl;


import connection.DbConnection;
import exceptions.MyException;
import model.SalesStand;
import repository.CrudRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static exceptions.ExceptionCode.REPOSITORY;


public class SalesStandRepository implements CrudRepository<SalesStand, Integer> {
    private Connection connection = DbConnection.getInstance().getConnection();
    /**
     * add sales stand to sql table
     * @param salesStand instance
     */
    @Override
    public void add(SalesStand salesStand) {
        final String sql = "insert into salesStand (moviesId, customerId, startDateTime) values (?,?,?)";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (salesStand == null) {
                throw new NullPointerException("SALES STAND IS NULL");
            }
            prep.setInt(1, salesStand.getMoviesId());
            prep.setInt(2, salesStand.getCustomerId());
            prep.setTimestamp(3, Timestamp.valueOf(salesStand.getStartDateTime()));


            prep.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(REPOSITORY, "SALES STAND REPOSITORY ADD EXCEPTION: " + e.getMessage());
        }
    }
    /**
     * @param salesStand fields are update automatically
     */
    @Override
    public void update(SalesStand salesStand) {
        final String sql = "update salesStand set moviesId = ?,customerId = ?, startDateTime = ?where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (salesStand == null) {
                throw new NullPointerException("SALES STAND  IS NULL");
            }
            prep.setInt(1, salesStand.getMoviesId());
            prep.setInt(2, salesStand.getCustomerId());
            prep.setObject(3, salesStand.getStartDateTime());
            prep.setInt(4, salesStand.getId());

            prep.execute();
        } catch (Exception e) {
            throw new MyException(REPOSITORY, "SALES STAND REPOSITORY UPDATE EXCEPTION: " + e.getMessage());
        }
    }
    /**
     * delete by id from table
     * @param id of sales stand
     */
    @Override
    public void delete(Integer id) {
        final String sql = "delete from salesStand  where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            prep.setInt(1, id);
            prep.execute();
        } catch (Exception e) {
            throw new MyException(REPOSITORY, "SALES STAND  REPOSITORY DELETE EXCEPTION: " + e.getMessage());
        }
    }
    /**
     * clearing all table
     */
    @Override
    public void deleteAll() {

        try (final Statement statement = connection.createStatement()) {
            final String sql = " DELETE FROM salesStand";
            final String sql1 = "DELETE FROM SQLITE_SEQUENCE WHERE name='salesStand';";

            statement.execute(sql);
            statement.execute(sql1);

        } catch (Exception e) {
            throw new MyException(REPOSITORY, "CUSTOMER REPOSITORY DELETE ALL EXCEPTION: " + e.getMessage());
        }
    }

    @Override
    public Optional<SalesStand> findById(Integer id) {
        final String sql = "select id, moviesId, customerId, startDateTime from salesStand where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            prep.setInt(1, id);

            ResultSet resultSet = prep.executeQuery();
            if (resultSet.next()) {
                return Optional.of(SalesStand.builder()
                        .id(resultSet.getInt(1))
                        .moviesId(resultSet.getInt(2))
                        .customerId(resultSet.getInt(3))
                        .startDateTime(resultSet.getTimestamp(4).toLocalDateTime())
                        .builder());
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new MyException(REPOSITORY, "SALES STAND REPOSITORY FIND BY ID EXCEPTION: " + e.getMessage());
        }
    }

    /**
     * @return sales stand List
     */
    @Override
    public List<SalesStand> findAll() {
        try (final Statement stat = connection.createStatement()) {
            final String sql = "select id, moviesId, customerId,startDateTime from salesStand";
            ResultSet resultSet = stat.executeQuery(sql);
            List<SalesStand> salesStands = new ArrayList<>();

            while (resultSet.next()) {
                salesStands.add(SalesStand.builder()
                        .id(resultSet.getInt(1))
                        .moviesId(resultSet.getInt(2))
                        .customerId(resultSet.getInt(3))
                        .startDateTime(resultSet.getTimestamp(4).toLocalDateTime())
                        .builder());
            }
            return salesStands;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(REPOSITORY, "SALES STAND REPOSITORY FIND ALL SALES STAND EXCEPTION: " + e.getMessage());
        }
    }





}




