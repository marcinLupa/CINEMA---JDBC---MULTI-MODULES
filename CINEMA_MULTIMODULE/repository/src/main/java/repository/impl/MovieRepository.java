package repository.impl;

import com.google.gson.JsonParseException;
import connection.DbConnection;
import exceptions.ExceptionCode;
import exceptions.MyException;
import json.impl.MovieConverter;
import model.Movie;
import model.utils.SortingOptions;
import repository.CrudRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MovieRepository implements CrudRepository<Movie, Integer> {
    private final Connection connection = DbConnection.getInstance().getConnection();

    /**
     * @param movie automatically add movie
     */
    @Override
    public void add(Movie movie) {
        final String sql = "insert into movie (title, genre, price,duration, releaseDate) values (?,?,?,?,?)";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (movie == null) {
                throw new NullPointerException("MOVIE IS NULL");
            }
            prep.setString(1, movie.getTitle());
            prep.setString(2, movie.getGenre());
            prep.setBigDecimal(3, movie.getPrice());
            prep.setInt(4, movie.getDuration());
            prep.setObject(5, movie.getReleaseDate());

            prep.execute();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "MOVIE REPOSITORY ADD EXCEPTION: " + e.getMessage());
        }
    }

    /**
     * method get the movie information from json file
     * @param filename of source from where movie will be parsed
     */
    @SuppressWarnings({"CatchMayIgnoreException", "finally", "ThrowFromFinallyBlock"})
    public void addFilmFromFile(String filename) {
        try {

            DbConnection.getInstance().getConnection().setAutoCommit(false);
            final String FILE_FOLDER = "C:/Users/Pocisk/IdeaProjects/PROJEKTY/JDBC/CINEMA_MULTIMODULE/json/src/main/resources/files/movies/";

            MovieConverter movieConverter = new MovieConverter(FILE_FOLDER
                    + filename
                    .toUpperCase()
                    .concat(".json"));
            add(movieConverter
                    .fromJson().orElseThrow(() -> new JsonParseException("JSON FILE READER EXCEPTION"))
            );
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
     * @param movie that will be edited by user, fields are update from console
     */
    @Override
    public void update(Movie movie) {

        final String sql = "update movie set title = ?,genre = ?, price = ?, duration = ?,releaseDate=? where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (movie == null) {
                throw new NullPointerException("MOVIE IS NULL");
            }

            System.out.println("DANE BEDA EDYTOWANE PO KOLEI: TYTUL, GATUNEK, CENA, CZAS TRWANIA, DATA PREMIERY"+"\n"+
                    "JEZELI NIE CHCESZ EDYTOWAC JAKIS DANYCH NACISNIJ ENTER");


            prep.setString(1, movie.getTitle());
            prep.setString(2, movie.getGenre());
            prep.setBigDecimal(3, movie.getPrice());
            prep.setInt(4, movie.getDuration());
            prep.setObject(5, movie.getReleaseDate());


            switch (1) {
                case 1:
                    System.out.println("PODAJ NOWY TYTUL (OBECNIE: "+movie.getTitle()+")");
                    String title =new Scanner(System.in).nextLine();
                    if(title.equals("")){
                        System.out.println("TYTUL POZOSTAJE BEZ ZMIAN");
                    }else{
                        prep.setString(1,title );
                    }

                case 2:
                    System.out.println("PODAJ NOWY GATUNEK FILMU (OBECNIE: "+movie.getGenre()+")");
                    String genre =new Scanner(System.in).nextLine();
                    if(genre.equals("")){
                        System.out.println("GATUNEK POZOSTAJE BEZ ZMIAN");
                    }else {
                        prep.setString(2, genre);
                    }
                case 3:
                    System.out.println("PODAJ NOWA CENE (OBECNIE: "+movie.getPrice()+")");
                    String price =new Scanner(System.in).nextLine();
                    if(price.equals("")){
                        System.out.println("CENA POZOSTAJE BEZ ZMIAN");
                    }else {
                        prep.setBigDecimal(3, new BigDecimal(price));
                    }
                case 4:
                    System.out.println("PODAJ NOWY CZAS TRWANIA FILMU (OBECNIE: "+movie.getDuration()+")");
                    String duration =new Scanner(System.in).nextLine();
                    if(duration.equals("")){
                        System.out.println("CZAS TRWANIA POZOSTAJE BEZ ZMIAN");
                    }else {
                        prep.setInt(4, Integer.valueOf(duration));
                    }
                case 5:
                    System.out.println("PODAJ NOWA DATE PREMIERY (OBECNIE: "+movie.getReleaseDate()+")");
                    String date =new Scanner(System.in).nextLine();
                    if(date.equals("")){
                        System.out.println("DATA PREMIERY POZOSTAJE BEZ ZMIAN");
                    }else {
                        prep.setDate(5, Date.valueOf(date));
                    }
            }

            prep.setInt(6, movie.getId());
            prep.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(ExceptionCode.REPOSITORY, "MOVIE REPOSITORY UPDATE EXCEPTION: " + e.getMessage());
        }
    }

    /**
     * delete by id from table
     * @param id of movie
     */
    @Override
    public void delete(Integer id) {
        final String sql = "delete from movie  where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            prep.setInt(1, id);
            prep.execute();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "MOVIE REPOSITORY DELETE EXCEPTION: " + e.getMessage());
        }
    }
    /**
     * clearing all table
     */
    @Override
    public void deleteAll() {

        try (final Statement statement = connection.createStatement()) {
            final String sql = " DELETE FROM movie";
            final String sql1 = "DELETE FROM SQLITE_SEQUENCE WHERE name='movie';";

            statement.execute(sql);
            statement.execute(sql1);

        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "CUSTOMER REPOSITORY DELETE ALL EXCEPTION: " + e.getMessage());
        }
    }

    @Override
    public Optional<Movie> findById(Integer id) {

        final String sql = "select id, title, genre, price,duration,releaseDate from movie where id = ?";
        try (final PreparedStatement prep = connection.prepareStatement(sql)) {
            if (id == null) {
                throw new NullPointerException("ID IS NULL");
            }
            prep.setInt(1, id);

            ResultSet resultSet = prep.executeQuery();
            if (resultSet.next()) {
                return Optional.of(Movie.builder()
                        .id(resultSet.getInt(1))
                        .title(resultSet.getString(2))
                        .genre(resultSet.getString(3))
                        .price(resultSet.getBigDecimal(4))
                        .duration(resultSet.getInt(5))
                        .releaseDate(resultSet.getString(6))
                        .builder());
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "MOVIE REPOSITORY FIND BY ID EXCEPTION: " + e.getMessage());
        }
    }

    /**
     * @return customers List
     */
    @Override
    public List<Movie> findAll() {
        try (final Statement stat = connection.createStatement()) {
            final String sql = "select id, title, genre,price,duration,releaseDate from movie";
            ResultSet resultSet = stat.executeQuery(sql);
            List<Movie> movies = new ArrayList<>();

            while (resultSet.next()) {
                movies.add(Movie.builder()
                        .id(resultSet.getInt(1))
                        .title(resultSet.getString(2))
                        .genre(resultSet.getString(3))
                        .price(resultSet.getBigDecimal(4))
                        .duration(resultSet.getInt(5))
                        .releaseDate(resultSet.getString(6))
                        .builder());
            }
            return movies;
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "MOVIE REPOSITORY LIST OF MOVIES EXCEPTION: " + e.getMessage());
        }
    }
    /**
     * @param option what field of object will be key to sort
     * @param order what order desc or asc of sorting
     * @return List of sorted movies
     */
    public List<Movie> sortingMovies(SortingOptions option, SortingOptions order) {

        try {
            switch (option) {
                case TITLE:
                    switch (order) {
                        case ASC:
                            System.out.println("SORTOWANIE PO TYTULE");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Movie::getTitle))
                                    .collect(Collectors.toList());
                        case DESC:
                            System.out.println("SORTOWANIE PO TYTULE");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Movie::getTitle).reversed())
                                    .collect(Collectors.toList());
                    }

                case GENRE:
                    switch (order) {
                        case ASC:
                            System.out.println("SORTOWANIE PO GATUNKU");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Movie::getGenre))
                                    .collect(Collectors.toList());
                        case DESC:
                            System.out.println("SORTOWANIE PO GATUNKU");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Movie::getGenre).reversed())
                                    .collect(Collectors.toList());
                    }

                case PRICE:
                    switch (order) {
                        case ASC:
                            System.out.println("SORTOWANIE PO CENIE");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Movie::getPrice))
                                    .collect(Collectors.toList());
                        case DESC:
                            System.out.println("SORTOWANIE PO CENIE");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Movie::getPrice).reversed())
                                    .collect(Collectors.toList());
                    }

                case DURATION:
                    switch (order) {
                        case ASC:
                            System.out.println("SORTOWANIE PO CZASIE TRWANIA FILMU");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparingInt(Movie::getDuration))
                                    .collect(Collectors.toList());
                        case DESC:
                            System.out.println("SORTOWANIE PO CZASIE TRWANIA FILMU");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparingInt(Movie::getDuration).reversed())
                                    .collect(Collectors.toList());
                    }

                case RELASE_DATE:
                    switch (order) {
                        case ASC:
                            System.out.println("SORTOWANIE PO DACIE PREMIERY");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Movie::getReleaseDate))
                                    .collect(Collectors.toList());
                        case DESC:
                            System.out.println("SORTOWANIE PO DACIE PREMIERY");
                            return findAll()
                                    .stream()
                                    .sorted(Comparator.comparing(Movie::getReleaseDate).reversed())
                                    .collect(Collectors.toList());
                    }

            }
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "MOVIE REPOSITORY SORTING MOVIES EXCEPTION: " + e.getMessage());
        }
        return findAll();
    }

    /**
     * @param option enum instance
     * @return List of filtered movies
     */
    public List<Movie> filteredMovies(SortingOptions option) {
        try {
            switch (option) {
                case GENRE:
                    System.out.println("FILTROWANIE PO GATUNKU");
                    System.out.println("PODAJ SZUKANY GATUNEK FILMU"
                            +findAll()
                            .stream()
                            .map(Movie::getGenre)
                            .collect(Collectors.toList()));
                    String genreFromUser = new Scanner(System.in).nextLine();
                    if (genreFromUser == null || !genreFromUser.matches("[A-Za-z ]*")) {
                        throw new IllegalArgumentException("GENRE NAME INCORRECT");
                    }

                    List<Movie> moviesByGenre = findAll()
                            .stream()
                            .filter(x -> x.getGenre().equalsIgnoreCase(genreFromUser))
                            .collect(Collectors.toList());
                    moviesByGenre.forEach(System.out::println);
                    if (moviesByGenre.size() == 0) {
                        throw new IllegalArgumentException("GENRE FROM USER OUT OF BASE");
                    } else {
                        return moviesByGenre;
                    }


                case RELASE_DATE:
                    System.out.println("FILTROWANIE PO DACIE PREMIERY");

                    System.out.println("PODAJ DATE OD(yyyy-MM-dd):");
                    LocalDate realeseDateFromUserFrom =
                            LocalDate.parse(new Scanner(System.in).nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    System.out.println("PODAJ DATE DO(yyyy-MM-dd):");
                    LocalDate realeseDateFromUserTo =
                            LocalDate.parse(new Scanner(System.in).nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    if (realeseDateFromUserFrom == null || realeseDateFromUserTo == null) {
                        throw new IllegalArgumentException(" RELASE DATE INCORRECT");
                    }

                    List<Movie> moviesByDate = findAll()
                            .stream()
                            .filter(
                                    x -> LocalDate
                                            .parse(x.getReleaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                            .isAfter(realeseDateFromUserFrom) && LocalDate
                                            .parse(x.getReleaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                            .isBefore(realeseDateFromUserTo))
                            .collect(Collectors.toList());
                    if (moviesByDate.size() == 0) {
                        throw new IllegalArgumentException("DATES FROM USER OUT OF BASE");
                    } else {
                        return moviesByDate;
                    }

                case PRICE:
                    System.out.println("FILTROWANIE PO CENIE");

                    System.out.println("PODAJ DATE OD");
                    BigDecimal priceFromUserMin = new Scanner(System.in).nextBigDecimal();
                    System.out.println("PODAJ DATE DO");
                    BigDecimal priceFromUserMax = new Scanner(System.in).nextBigDecimal();

                    if (priceFromUserMin == null || priceFromUserMin.compareTo(new BigDecimal(0)) < 0 ||
                            priceFromUserMax == null || priceFromUserMax.compareTo(priceFromUserMin) < 0) {
                        throw new IllegalArgumentException("PRICE INCORRECT");
                    }
                    List<Movie> moviesByPrice = findAll()
                            .stream()
                            .filter(x -> x.getPrice().compareTo(priceFromUserMin) >= 0 &&
                                    x.getPrice().compareTo(priceFromUserMax) <= 0)
                            .collect(Collectors.toList());
                    if (moviesByPrice.size() == 0) {
                        throw new IllegalArgumentException("PRICEES FROM USER OUT OF BASE");
                    } else {
                        return moviesByPrice;
                    }

                case DURATION:
                    System.out.println("FILTROWANIE PO CZASIE TRWANIA FILMU");

                    System.out.println("CZAS TRWANIA OD");
                    int durationFromUserMin = new Scanner(System.in).nextInt();
                    System.out.println("CZAS TRWANIA OD");
                    int durationFromUserMax = new Scanner(System.in).nextInt();

                    if (durationFromUserMin < 0) {
                        throw new IllegalArgumentException("DURATION INCORRECT");
                    }
                    List<Movie> moviesByDuration = findAll()
                            .stream()
                            .filter(x -> x.getDuration() > durationFromUserMin &&
                                    x.getDuration() < durationFromUserMax)
                            .collect(Collectors.toList());

                    if (moviesByDuration.size() == 0) {
                        throw new IllegalArgumentException("DURATION USER OUT OF BASE");
                    } else {
                        return moviesByDuration;
                    }
                case FIND_WORD:
                    System.out.println("FILTROWANIE PO WYRAZENIU" + "\n" +
                            "(JEZELI TO WYRAZENIE JEST W TYTULE TO FILM SPELNIA WARUNKI KRYTERIUM)");

                    System.out.println("PODAJ SZUKANE WYRAZENIE");
                    Pattern pattern = Pattern.compile(new Scanner(System.in).nextLine().toUpperCase());
                    if (pattern.toString() == null) {
                        throw new IllegalArgumentException("PATTERN INCORRECT");
                    }
                    List<Movie> moviesByPattern = findAll()
                            .stream()
                            .filter(x -> pattern.matcher(x.getTitle()).find())
                            .collect(Collectors.toList());
                    if (moviesByPattern.size() == 0) {
                        throw new IllegalArgumentException("PATTERN FROM USER OUT TITLE BASE");
                    } else {
                        return moviesByPattern;
                    }
            }
        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "MOVIE REPOSITORY FILTRING MOVIES EXCEPTION: " + e.getMessage());
        }
        return findAll();
    }

    public Map<String, List<Movie>> genreMap() {
        try {
            return findAll()
                    .stream()
                    .collect(Collectors.groupingBy(Movie::getGenre));

        } catch (Exception e) {
            throw new MyException(ExceptionCode.REPOSITORY, "MOVIE REPOSITORY GENRE MAP EXCEPTION: " + e.getMessage());
        }
    }

}
