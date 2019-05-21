package repository.impl;



import connection.DbConnection;
import exceptions.ExceptionCode;
import exceptions.MyException;
import model.LoyaltyCard;
import repository.CrudRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoyaltyCardRepository implements CrudRepository<LoyaltyCard, Integer> {
    private final Connection connection = DbConnection.getInstance().getConnection();
    /**
     * add loyalty card to sql table
     * @param loyaltyCard instance
     */
    @Override
    public void add(LoyaltyCard loyaltyCard) {
        final String sql = "insert into loyaltyCard (expirationDate, discount, moviesNumberId) values (?, ?, ?)";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (loyaltyCard == null) {
                throw new NullPointerException("LOYALTY CARD IS NULL");
            }
            prep.setString(1, loyaltyCard.getExpirationDate());
            prep.setBigDecimal(2, loyaltyCard.getDiscount());
            prep.setInt(3, loyaltyCard.getMoviesNumberId());

            prep.execute();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "LOYALTY CARD REPOSITORY ADD EXCEPTION: " + e.getMessage());
        }
    }
    /**
     * @param loyaltyCard fields are update automatically
     */
    @Override
    public void update(LoyaltyCard loyaltyCard) {

        final String sql = "update loyaltyCard set expirationDate = ?,discount = ?, moviesNumberId = ? where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (loyaltyCard == null) {
                throw new NullPointerException("LOYALTY CARD IS NULL");
            }
            prep.setString(1, loyaltyCard.getExpirationDate());
            prep.setBigDecimal(2, loyaltyCard.getDiscount());
            prep.setInt(3, loyaltyCard.getMoviesNumberId());
            prep.setInt(4, loyaltyCard.getId());

            prep.execute();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "LOYALTY CARD REPOSITORY UPDATE EXCEPTION: " + e.getMessage());
        }
    }
    /**
     * delete by id from table
     * @param id of loyalty card
     */
    @Override
    public void delete(Integer id) {
        final String sql = "delete from loyaltyCard  where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            prep.setInt(1, id);
            prep.execute();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "LOYALTY CARD REPOSITORY DELETE EXCEPTION: " + e.getMessage());
        }
    }

    /**
     * clearing all table
     */
    @Override
    public void deleteAll() {

        try (final Statement statement = connection.createStatement()) {
            final String sql = " DELETE FROM loyaltyCard";
            final String sql1 = "DELETE FROM SQLITE_SEQUENCE WHERE name='loyaltyCard';";

            statement.execute(sql);
            statement.execute(sql1);

        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "LOYALTY CARD REPOSITORY DELETE ALL EXCEPTION: " + e.getMessage());
        }
    }

    @Override
    public Optional<LoyaltyCard> findById(Integer id) {

        final String sql = "select id, expirationDate, discount, moviesNumberId from loyaltyCard where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            prep.setInt(1, id);

            ResultSet resultSet = prep.executeQuery();
            if (resultSet.next()) {
                return Optional.of(LoyaltyCard.builder()
                        .id(resultSet.getInt(1))
                        .expirationDate(resultSet.getString(2))
                        .discount(resultSet.getBigDecimal(3))
                        .moviesNumberId(resultSet.getInt(4))
                        .builder());
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "LOYALTY CARD REPOSITORY FIND BY ID EXCEPTION: " + e.getMessage());
        }
    }
    /**
     * @return loyalty card List
     */
    @Override
    public List<LoyaltyCard> findAll() {
        try (final Statement stat = connection.createStatement()) {
            final String sql = "select id, expirationDate, discount,moviesNumberId from loyaltyCard";
            ResultSet resultSet = stat.executeQuery(sql);
            List<LoyaltyCard> loyaltyCards = new ArrayList<>();

            while (resultSet.next()) {
                loyaltyCards.add(LoyaltyCard.builder()
                        .id(resultSet.getInt(1))
                        .expirationDate(resultSet.getString(2))
                        .discount(resultSet.getBigDecimal(3))
                        .moviesNumberId(resultSet.getInt(4))
                        .builder());
            }
            return loyaltyCards;
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "LOYALTY CARD REPOSITORY LIST OF MOVIES EXCEPTION: " + e.getMessage());
        }
    }
}
