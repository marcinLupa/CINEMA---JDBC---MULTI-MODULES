package repository.impl;

import connection.DbConnection;
import exceptions.ExceptionCode;
import exceptions.MyException;
import model.Customer;
import model.utils.SortingOptions;
import repository.CrudRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerRepository implements CrudRepository<Customer, Integer> {
    private final Connection connection = DbConnection.getInstance().getConnection();

    /**
     * add customers to sql table
     * @param customer instance
     */
    @Override
    public void add(Customer customer) {
        final String sql = "insert into customer (name,surname, age,email) values (?, ?, ?, ?)";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (customer == null) {
                throw new NullPointerException("CUSTOMER IS NULL");
            }
            prep.setString(1, customer.getName());
            prep.setString(2, customer.getSurname());
            prep.setInt(3, customer.getAge());
            prep.setString(4, customer.getEmail());
            prep.execute();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "CUSTOMER REPOSITORY ADD EXCEPTION: " + e.getMessage());
        }
    }


    /**
     * this method is used to add new customer by data given by user
     * @param name from user
     * @param surname from user
     * @param age from user
     * @param email from user
     */
    @SuppressWarnings({"finally", "ThrowFromFinallyBlock", "CatchMayIgnoreException"})
    public void addCustomerManual(String name, String surname, Integer age, String email) {
        try {
            DbConnection.getInstance().getConnection().setAutoCommit(false);
            add(Customer.builder()
                    .name(name)
                    .surname(surname)
                    .age(age)
                    .email(email)
                    .builder());
            DbConnection.getInstance().getConnection().commit();
        } catch (Exception e) {
            try {
                DbConnection.getInstance().getConnection().rollback();
            } catch (Exception ee) {
            } finally {
                throw new MyException(ExceptionCode.REPOSITORY, e.getMessage());
            }
        }
    }

    /**
     * @param customer edited by user, fields are update from console
     */
    @Override
    public void update(Customer customer) {

        final String sql = "update customer set name = ?,surname = ?, age = ?, email = ? where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (customer == null) {
                throw new NullPointerException("CUSTOMER IS NULL");
            }
            System.out.println("DANE BEDA EDYTOWANE PO KOLEI: IMIE, NAZWISKO, WIEK, EMAIL" + "\n" +
                    "JEZELI NIE CHCESZ EDYTOWAC JAKIS DANYCH NACISNIJ ENTER");

            prep.setString(1, customer.getName());
            prep.setString(2, customer.getSurname());
            prep.setInt(3, customer.getAge());
            prep.setString(4, customer.getEmail());


            switch (1) {
                case 1:
                    System.out.println("PODAJ IMIE (OBECNIE: "+customer.getName()+")");
                    String name = new Scanner(System.in).nextLine();
                    if (name.equals("")) {
                        System.out.println("IMIE POZOSTAJE BEZ ZMIAN");
                    } else {
                        prep.setString(1, name);
                    }

                case 2:
                    System.out.println("PODAJ NAZWISKO (OBECNIE: "+customer.getSurname()+")");
                    String surname = new Scanner(System.in).nextLine();
                    if (surname.equals("")) {
                        System.out.println("NAZWISKO POZOSTAJE BEZ ZMIAN");
                    } else {
                        prep.setString(2, surname);
                    }
                case 3:
                    System.out.println("PODAJ WIEK (OBECNIE: "+customer.getAge()+")");
                    String age = new Scanner(System.in).nextLine();
                    if (age.equals("")) {
                        System.out.println("WIEK POZOSTAJE BEZ ZMIAN");
                    } else {
                        prep.setInt(3, Integer.valueOf(age));
                    }
                case 4:
                    System.out.println("PODAJ EMAIL (OBECNIE: "+customer.getEmail()+")");
                    String email= new Scanner(System.in).nextLine();
                    if (email.equals("")) {
                        System.out.println("WIEK POZOSTAJE BEZ ZMIAN");
                    } else {
                        prep.setString(4, email);
                    }
            }

            prep.setInt(5, customer.getId());
            prep.execute();

        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "CUSTOMER REPOSITORY UPDATE EXCEPTION: " + e.getMessage());
        }
    }

    /**
     * method used to automatically update customers by data from  file
     * @param customer from file
     */
    public void updateAutomatic(Customer customer) {
        final String sql = "update customer set name = ?, surname = ?, age = ?, email=? ,loyaltyCardId =?where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (customer == null) {
                throw new NullPointerException("CUSTOMER IS NULL");
            }
            prep.setString(1, customer.getName());
            prep.setString(2, customer.getSurname());
            prep.setInt(3, customer.getAge());
            prep.setString(4, customer.getEmail());
            prep.setInt(5, customer.getLoyaltyCardId());
            prep.setInt(6, customer.getId());
            prep.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(ExceptionCode.REPOSITORY, "CUSTOMER REPOSITORY UPDATE AUTOMATIC EXCEPTION: " + e.getMessage());
        }
    }


    /**
     * delete by id from table
     * @param id of customer
     */
    @Override
    public void delete(Integer id) {
        final String sql = "delete from customer  where id = ?;";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            prep.setInt(1, id);
            prep.execute();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "CUSTOMER REPOSITORY DELETE EXCEPTION: " + e.getMessage());
        }
    }


    /**
     * clearing all table
     */
    @Override
    public void deleteAll() {

        try (final Statement statement = connection.createStatement()) {
            final String sql = " DELETE FROM customer";
            final String sql1 = "DELETE FROM SQLITE_SEQUENCE WHERE name='customer';";

            statement.execute(sql);
            statement.execute(sql1);

        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(ExceptionCode.REPOSITORY, "CUSTOMER REPOSITORY DELETE ALL EXCEPTION: " + e.getMessage());
        }
    }

    @Override
    public Optional<Customer> findById(Integer id) {

        final String sql = "select id, name, surname, age, email, loyaltyCardId from customer where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            prep.setInt(1, id);

            ResultSet resultSet = prep.executeQuery();
            if (resultSet.next()) {
                return Optional.of(Customer.builder()
                        .id(resultSet.getInt(1))
                        .name(resultSet.getString(2))
                        .surname(resultSet.getString(3))
                        .age(resultSet.getInt(4))
                        .email(resultSet.getString(5))
                        .loyaltyCardId(resultSet.getInt(6))
                        .builder());
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "CUSTOMER REPOSITORY FIND BY ID EXCEPTION: " + e.getMessage());
        }
    }

    /**
     * @return customers List
     */
    @Override
    public List<Customer> findAll() {
        try (final Statement stat = connection.createStatement()) {
            final String sql = "select id,name,surname,age,email,loyaltyCardId from customer";
            ResultSet resultSet = stat.executeQuery(sql);
            List<Customer> customers = new ArrayList<>();
            while (resultSet.next()) {
                customers.add(Customer.builder()
                        .id(resultSet.getInt(1))
                        .name(resultSet.getString(2))
                        .surname(resultSet.getString(3))
                        .age(resultSet.getInt(4))
                        .email(resultSet.getString(5))
                        .loyaltyCardId(resultSet.getInt(6))
                        .builder());
            }
            return customers;
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "CUSTOMER REPOSITORY FIND ALL EXCEPTION: " + e.getMessage());
        }
    }


    /**
     * @param option what field of object will be key to sort
     * @param order what order desc or asc of sorting
     * @return List of sorted customers
     */
    public List<Customer> sortingCustomers(SortingOptions option, SortingOptions order) {
        try {
            switch (option) {
                case NAME:
                    switch (order) {
                        case ASC:
                            System.out.println("SORTOWANIE PO IMIENIU ROSNACO");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(x -> x.getName().toUpperCase()))
                                    .collect(Collectors.toList());
                        case DESC:
                            System.out.println("SORTOWANIE PO IMIENIU MALEJACO");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.<Customer, String>comparing(x -> x.getName().toUpperCase()).reversed())
                                    .collect(Collectors.toList());
                    }
                    break;
                case SURNAME:
                    switch (order) {
                        case ASC:
                            System.out.println("SORTOWANIE PO NAZWISKU ROSNACO");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(x -> x.getSurname().toUpperCase()))
                                    .collect(Collectors.toList());
                        case DESC:
                            System.out.println("SORTOWANIE PO NAZWISKU MALEJACO");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.<Customer, String>comparing(x -> x.getSurname().toUpperCase()).reversed())
                                    .collect(Collectors.toList());
                    }
                    break;
                case AGE:
                    switch (order) {
                        case ASC:
                            System.out.println("SORTOWANIE PO WIEKU OD NAJMLODSZEGO ");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Customer::getAge))
                                    .collect(Collectors.toList());

                        case DESC:
                            System.out.println("SORTOWANIE PO WIEKU OD NAJMLODSZEGO");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Customer::getAge).reversed())
                                    .collect(Collectors.toList());
                    }

                    break;
                case EMAIL:
                    switch (order) {
                        case ASC:
                            System.out.println("SORTOWANIE PO ADRESIE E-MAIL ALFABETYCZNIE ROSNACO");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(x -> x.getEmail().toUpperCase()))
                                    .collect(Collectors.toList());
                        case DESC:
                            System.out.println("SORTOWANIE PO ADRESIE E-MAIL MALEJACO");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.<Customer, String>comparing(
                                            x -> x.getEmail().toUpperCase()).reversed())
                                    .collect(Collectors.toList());
                    }

                    break;
                case LOYALTY_CARD_ID:
                    switch (order) {
                        case ASC:
                            System.out.println("SORTOWANIE PO NUMERZE KARTY STALEGO KLIENTA ROSNACO");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Customer::getLoyaltyCardId))
                                    .collect(Collectors.toList());
                        case DESC:
                            System.out.println("SORTOWANIE PO NUMERZE KARTY STALEGO KLIENTA MALEJACO");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Customer::getLoyaltyCardId).reversed())
                                    .collect(Collectors.toList());
                    }

                    break;
            }
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "CUSTOMER REPOSITORY SORTING CUSTOMERS EXCEPTION: " + e.getMessage());
        }
        return findAll();
    }
}
