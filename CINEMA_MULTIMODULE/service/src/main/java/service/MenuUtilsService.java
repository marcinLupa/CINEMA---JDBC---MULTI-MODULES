package service;


import exceptions.MyException;
import json.impl.MapConverter;
import model.Customer;
import model.LoyaltyCard;
import model.Movie;
import model.SalesStand;
import model.utils.Ticket;
import repository.impl.CustomerRepository;
import repository.impl.LoyaltyCardRepository;
import repository.impl.MovieRepository;
import repository.impl.SalesStandRepository;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static exceptions.ExceptionCode.*;


/**
 * @author Marcin Lupa
 * class include methods needed in Menu Service class to manage process of byuing tockets and outher staff
 */
class MenuUtilsService {
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final SalesStandRepository salesStandRepository = new SalesStandRepository();
    private final LoyaltyCardRepository loyaltyCardRepository = new LoyaltyCardRepository();
    private final UserDataService userDataService = new UserDataService();

    /**
     * Method used to manage process of buying tickets,
     * seting loyalty card to user and sending email with ticket to the customer
     * client - searching is this customer is in base
     * seance - movie choosen by client
     * clientCard - card that will be assigned tu client
     */
    void managingTickets() {
        final int VISITS_TO_LOYALTY_CARD = 3;
        final BigDecimal DISCOUNT = new BigDecimal(5);
        final int VISITS_NO_CHARGE = 5;
        final String SEANCES_FILENAME = "C:/Users/Pocisk/IdeaProjects/PROJEKTY/JDBC/CINEMA_MULTIMODULE/json/src/main/resources/files/seances/LISTOFSEANCES.json";


        Customer client;
        Movie seance;
        LoyaltyCard clientCard;

        System.out.println("PROSZE PODAC IMIE");
        String nameFromUser = userDataService.getString();
        System.out.println("PROSZE PODAC NAZWISKO");
        String surnameFromUser = userDataService.getString();
        System.out.println("PROSZE PODAC E-MAIL");
        String emailFromUser = userDataService.getEmail();


        try {
            client = creatingCustomer(nameFromUser, surnameFromUser, emailFromUser);
            System.out.println("DANE UZYTKOWNIKA: " + client + "\n");

            System.out.println(
                    "1 - ZAKUP BILETU" + "\n" +
                            "2 - HISTORIA TRANSAKCJI");
            int option = userDataService.getInt(2);
            switch (option) {
                case 1:

                    Map<String, List<LocalTime>> repertoireToday = creatingRepertoire(SEANCES_FILENAME);
                    System.out.println("REPERTUAR NA DZIS: ");
                    repertoireToday.forEach((k, v) -> System.out.println(k + " " + v));

                    seance = creatingMovie(repertoireToday);
                    System.out.println("\n" + "WYBRANY FILM: " + seance.getTitle());

                    System.out.println("\n" + "DOSTEPNE GODZINY W DNIU DZISIEJSZYM: ");
                    AtomicInteger counter1 = new AtomicInteger(1);
                    seancesToday(repertoireToday, seance).forEach(x -> System.out.println(counter1.getAndIncrement() + ". " + x));

                    System.out.println("WYBIERZ NUMER SEANSU: ");

                    salesStandRepository.add(SalesStand.builder()
                            .moviesId(seance.getId())
                            .customerId(client.getId())
                            .startDateTime(LocalDateTime.of(LocalDate.now(), seancesToday(repertoireToday, seance)
                                    .get(userDataService.getInt(seancesToday(repertoireToday, seance).size()))))
                            .builder());

                    if (howOftenClientWasInCinema(client) < VISITS_TO_LOYALTY_CARD) {
                        System.out.println("LICZBA SEANSOW POTRZEBNYCH DO OSIAGNIECIA KARTY STALEGO KLIENTA: "
                                + (VISITS_TO_LOYALTY_CARD - howOftenClientWasInCinema(client)) + "\n");
                    } else if (filtredSalesStand(client).size() > VISITS_TO_LOYALTY_CARD + VISITS_NO_CHARGE) {

                        System.out.println("WYKORZYSTAŁES JUZ KARTE ZNIZKOWA OBECNIE NIE MAMY DLA CIEBIE ZADNYCH PROMOCJI ");
                    } else if (client.getLoyaltyCardId() == 0) {
                        System.out.println("CZY CHCESZ ZALOZYC KARTE STALEGO KLIENTA?(TAK/NIE)");
                        String agreement = userDataService.getString().toUpperCase();
                        switch (agreement) {
                            case "TAK":
                                loyaltyCardRepository.add(LoyaltyCard.builder()
                                        .expirationDate(String.valueOf(LocalDate.now().plusYears(3)))
                                        .discount(DISCOUNT)
                                        .moviesNumberId(VISITS_NO_CHARGE)
                                        .builder());
                                client.setLoyaltyCardId(loyaltyCardRepository.findAll()
                                        .get(loyaltyCardRepository.findAll()
                                                .size() - 1)
                                        .getId());
                                customerRepository.updateAutomatic(client);

                                System.out.println("KWOTA ZNIZKI TO " + String.format("%2.2f", DISCOUNT) + " ZL NA KAZDY KOLEJNY SEANS");
                                System.out.println("LICZBA SEANSOW DO WYKORZYSTANIA TO " + VISITS_NO_CHARGE + "\n");
                                break;

                            case "NIE":
                                System.out.println("NIE ZALOZYLES KARTY STALEGO KLIENTA");
                                break;
                        }
                    } else {

                        clientCard = creatingLoyaltyCard(client);
                        clientCard.setMoviesNumberId(VISITS_NO_CHARGE - (howOftenClientWasInCinema(client) - VISITS_TO_LOYALTY_CARD));
                        if (clientCard.getMoviesNumberId() > 0) {
                            System.out.println("POZOSTALA LICZBA SEANSOW DO WYKORZYSTANIA W RAMACH ZNIZKI " + clientCard.getMoviesNumberId());
                            seance.setPrice(seance.getPrice().subtract(DISCOUNT));
                            System.out.println("CENA BILETU TO: " + seance.getPrice() + " ZL");
                        }
                        if (LocalDate.now().isAfter(LocalDate.parse(clientCard.getExpirationDate()))) {
                            loyaltyCardRepository.delete(clientCard.getId());
                            System.out.println("CZAS WAŻNOSCI KARTY UPLYNAL");
                        } else if (clientCard.getMoviesNumberId() <= 0) {
                            loyaltyCardRepository.delete(clientCard.getId());
                            System.out.println("WYKORZYSTALES PULE ZNIZKOWYCH SEANSOW");
                        }
                    }
                    EmailService emailService = new EmailService();
                    try {
                        emailService.sendAsHtml("marcin.lupa1987@gmail.com"
                                , "KUPILES BILET NUMER: "
                                        + salesStandRepository.findAll().get(salesStandRepository.findAll().size() - 1).getId(),

                                "<head>  " +
                                        "<body>" + "<h1>" + "TWOJ FILM TO: " + seance.getTitle() + "</h1>" +
                                        "<p>" + "CENA TWOJEGO BILETU TO: " + seance.getPrice() + " ZL " + "</p>" +
                                        "<p>" + "GODZINA ROZPOCZECIA SEANSU: " + salesStandRepository
                                        .findAll()
                                        .get(salesStandRepository
                                                .findAll()
                                                .size() - 1)
                                        .getStartDateTime()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "</p>" +
                                        "</body>" + "</html>" + " <img id='barcode'" +
                                        "\" src=\"https://api.qrserver.com/v1/create-qr-code/?data=HelloWorld&amp;size=100x100\"" +
                                        "alt=\"\"" + "title=\"HELLO\"" + "width=\"50\"" + "\" height=\"50\" />" + "</body>");
                    } catch (MessagingException e) {
                        throw new MyException(EMAIL, "E-MAIL SEND AS HTML EXCEPTION" + e.getMessage());
                    }

                    break;
                case 2:
                    ticketHistoryManager(client);
                    break;
            }
        } catch (MyException e) {
            throw new MyException(REPOSITORY,
                    "SALES STAND REPOSITORY MANAGING TICKETS EXCEPTION: " + e.getExceptionInfo().getMessage());
        }

    }

    /**
     * method used to check history of transaction of the client
     *
     * @param client that history will be checking
     */
    private void ticketHistoryManager(Customer client) {
        if (client == null) {
            throw new MyException(SERVICE, "CLIENT IS NULL");
        }
        while (true) {
            System.out.println(
                    "1 - PELNA HISTORIA ZAKUPU BILETOW" + "\n" +
                            "LUB FILTROWANIE HISTORI ZAKUPU BILETOW" + "\n" +
                            "2 - GATUNEK" + "\n" +
                            "3 - PO DATACH" + "\n" +
                            "4 - PO DŁUGOSCI TRWANIA SEANSU" + "\n" +
                            "5 - WYJSCIE Z HISTORII TRANSAKCJI");
            int option = userDataService.getInt(5);

            List<Ticket> historyTransactionsMessage = new ArrayList<>();

            switch (option) {

                case 1:
                    historyTransactionsMessage = transactionHistory(client);

                    break;
                case 2:

                    System.out.println("PODAJ SZUKANY GATUNEK FILMU: ");
                    String genreFromUser = userDataService.getString();

                    historyTransactionsMessage = transactionHistory(client)
                            .stream()
                            .filter(x -> x.getGenre().equals(genreFromUser))
                            .collect(Collectors.toList());
                    break;
                case 3:
                    System.out.println("PODAJ DATE OD(yyyy-MM-dd HH:mm):");
                    LocalDateTime date1 = userDataService.getLocalDateTime();
                    System.out.println("PODAJ DATE DO(yyyy-MM-dd HH:mm):");
                    LocalDateTime date2 = userDataService.getLocalDateTime();

                    historyTransactionsMessage = transactionHistory(client)
                            .stream()
                            .filter(x -> MenuUtilsService.filtringDates(x.getStartDateTime(), date1, date2))
                            .collect(Collectors.toList());
                    break;
                case 4:
                    System.out.println("PODAJ CZAS TRWANIA OD:");
                    int durationFromUser1 = userDataService.getInt(1000);
                    System.out.println("PODAJ CZAS TRWANIA DO:");
                    int durationFromUser2 = userDataService.getInt(1000);

                    historyTransactionsMessage = transactionHistory(client)
                            .stream()
                            .filter(x -> x.getDuration() > durationFromUser1 && x.getDuration() < durationFromUser2)
                            .collect(Collectors.toList());
                    break;
                case 5:
                    return;
            }

            historyTransactionsMessage.forEach(System.out::println);

            EmailService emailService = new EmailService();
            try {
                emailService.sendAsHtml("marcin.lupa1987@gmail.com"
                        , "HISTORIA TRANSAKCJI ",

                        "<head>  " +
                                "<body>" + "<h1>" + client.getName() + "," + "</h1>" +
                                "<p>" + "HISTORIA TWOICH TRANSAKCJI TO: " + historyTransactionsMessage
                                .stream()
                                .map(x -> "<p>" + x.getFilmTitle() + " DATA SEANSU: " + x.getStartDateTime() + "</p>")
                                .collect(Collectors.toList()) + "</p>" +
                                "</body>" +
                                "</html>");
            } catch (MessagingException e) {
                throw new MyException(EMAIL, "E-MAIL SEND AS HTML EXCEPTION" + e.getMessage());
            }
        }

    }

    /**
     * @param filename json file with information about seances
     * @return Movies and list of seances in a day
     */
    private static Map<Movie, List<String>> cinemaRosterFromJsonFile(String filename) {
        if (filename == null) {
            throw new MyException(SERVICE, "FILENAME IS NULL");
        }
        @SuppressWarnings("unchecked")
        Map<String, List<String>> stringListMap =
                new MapConverter(
                        filename)
                        .fromJson().orElseThrow(() -> new MyException(JSON, "JSON PARSE EXCEPTION"));
       new MovieRepository()
                .findAll().forEach(System.out::println);
        stringListMap.forEach((k,v)-> System.out.println(k+""+v));
        return stringListMap
                .entrySet()
                .stream()
                .collect(Collectors
                        .toMap(
                                k -> new MovieRepository()
                                        .findAll()
                                        .stream()
                                        .filter(x -> x
                                                .getTitle()
                                                .equals(k.getKey()))
                                        .findFirst()
                                        .orElseThrow(
                                                () -> new MyException(SERVICE,"ELEMENT OUT OF BASE"))
                                , Map.Entry::getValue
                                , (v1, v2) -> v1
                                , LinkedHashMap::new
                        ));
    }

    /**
     * method to check is this user is out of base
     *
     * @param nameFromUser    from console
     * @param surnameFromUser from console
     * @param emailFromUser   from console
     * @return customer if he is in the date base
     */
    private static Customer creatingCustomer(String nameFromUser, String surnameFromUser, String emailFromUser) {
        if (nameFromUser == null) {
            throw new MyException(SERVICE, "NAME FROM USER IS NULL");
        }
        if (surnameFromUser == null) {
            throw new MyException(SERVICE, "SURNAME FROM USER IS NULL");
        }
        if (emailFromUser == null) {
            throw new MyException(SERVICE, "EMAIL FROM USER IS NULL");
        }
        return new CustomerRepository()
                .findAll()
                .stream()
                .filter(x -> x.getName().equals(nameFromUser) &&
                        x.getSurname().equals(surnameFromUser) &&
                        x.getEmail().equals(emailFromUser))
                .findFirst()
                .orElseThrow(() -> new MyException(VALIDATION, "USER OUT OF BASE"));
    }

    /**
     * @param filename List of day seances
     * @return name of film and seances aat the day
     */
    @SuppressWarnings("SameParameterValue")
    private static Map<String, List<LocalTime>> creatingRepertoire(String filename) {
        if (filename == null) {
            throw new MyException(SERVICE, "FILE NAME IS NULL");
        }
        AtomicInteger counter = new AtomicInteger(1);

        return cinemaRosterFromJsonFile(filename)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        k -> counter.getAndIncrement() + ". " + k.getKey().getTitle(),
                        v -> v.getValue()
                                .stream()
                                .map(LocalTime::parse)
                                .filter(x -> x.isAfter(LocalTime.now()))
                                .collect(Collectors.toList()),
                        (v1, v2) -> v1,
                        () -> new TreeMap<>(Comparator.comparing(x -> x))));

    }

    /**
     * @param repertoireToday string movie name and List of seances
     * @return movie choosen by user
     */
    private Movie creatingMovie(Map<String, List<LocalTime>> repertoireToday) {
        if (repertoireToday == null || repertoireToday.isEmpty()) {
            throw new MyException(SERVICE, "REPERTOIRE TODAY IS NULL OR EMPTY");
        }
        System.out.println("\n" + "PROSZE PODAC NUMER FILMU");
        int movieTitleNumber = userDataService.getInt(repertoireToday.size());

        return new MovieRepository().findAll()
                .stream()
                .filter(y -> y.getTitle().equals(repertoireToday
                        .entrySet()
                        .stream()
                        .filter(k -> Integer.valueOf(k.getKey().split(". ")[0]) == movieTitleNumber)
                        .map(k -> k.getKey().split("\\. ")[1])
                        .collect(Collectors.toList()).get(0)))
                .findFirst()
                .orElseThrow(() -> new MyException(SERVICE, "THIS MOVIE IS OUT OF TIME TODAY"));
    }

    /**
     * @param repertoireToday in this day all seances
     * @param seance          chosen movie by customer
     * @return today seances for chosen movie
     */
    private static List<LocalTime> seancesToday(Map<String, List<LocalTime>> repertoireToday, Movie seance) {
        if (repertoireToday == null || repertoireToday.isEmpty()) {
            throw new MyException(SERVICE, "REPERTOIRE TODAY IS NULL OR EMPTY");
        }
        if (seance == null) {
            throw new MyException(SERVICE, "SEANCE IS NULL");
        }
        return repertoireToday
                .entrySet()
                .stream()
                .filter(x -> x.getKey().split("\\. ")[1].equals(seance.getTitle()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new MyException(SERVICE, "THIS MOVIE IS OUT OF TIME TODAY"));
    }

    /**
     * @param client who will be checked
     * @return integer needed to compute how many times client must visit cinema to get Loyalty Card
     */
    private int howOftenClientWasInCinema(Customer client) {
        if (client == null) {
            throw new MyException(SERVICE, "CLIENT IS NULL");
        }
        return (int) salesStandRepository.findAll()
                .stream()
                .filter(x -> x.getCustomerId()
                        .compareTo(client.getId()) == 0)
                .count();

    }

    private static LoyaltyCard creatingLoyaltyCard(Customer client) {
        if (client == null) {
            throw new MyException(SERVICE, "CLIENT IS NULL");
        }
        return new LoyaltyCardRepository()
                .findAll()
                .stream()
                .filter(x -> x.getId().equals(client.getLoyaltyCardId()))
                .findFirst().orElseThrow(() -> new MyException(SERVICE, "ID OF LOYALITY CARD WRONG"));

    }

    /**
     * @param client our customer
     * @return List of transactions that was maded by our customer
     */
    private List<SalesStand> filtredSalesStand(Customer client) {
        if (client == null) {
            throw new MyException(SERVICE, "CLIENT IS NULL");
        }
        return salesStandRepository.findAll()
                .stream()
                .filter(x -> x.getCustomerId().equals(client.getId()))
                .collect(Collectors.toList());

    }

    private List<Ticket> transactionHistory(Customer client) {
        if (client == null) {
            throw new MyException(SERVICE, "CLIENT IS NULL");
        }
        return filtredSalesStand(client).stream()
                .map(x -> new Ticket(
                        x.getStartDateTime()
                        , new MovieRepository()
                        .findAll()
                        .stream()
                        .filter(f -> f.getId().equals(x.getMoviesId()))
                        .findFirst()
                        .orElseThrow(() -> new MyException(SERVICE, "MOVIE OUT OF BASE")).getTitle()
                        , new MovieRepository()
                        .findAll()
                        .stream()
                        .filter(f -> f.getId().equals(x.getMoviesId()))
                        .findFirst()
                        .orElseThrow(() -> new MyException(SERVICE, "MOVIE OUT OF BASE")).getPrice()
                        , new MovieRepository()
                        .findAll()
                        .stream()
                        .filter(f -> f.getId().equals(x.getMoviesId()))
                        .findFirst()
                        .orElseThrow(() -> new MyException(SERVICE, "MOVIE OUT OF BASE")).getGenre()
                        , new MovieRepository()
                        .findAll()
                        .stream()
                        .filter(f -> f.getId().equals(x.getMoviesId()))
                        .findFirst()
                        .orElseThrow(() -> new MyException(SERVICE, "MOVIE OUT OF BASE")).getDuration()
                )).collect(Collectors.toList());

    }

    String mostPopularGenre() {

        Map<String, Long> genreAndMoviesID = new MovieRepository().findAll()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Movie::getGenre,
                                Collectors.mapping(
                                        y -> salesStandRepository.findAll()
                                                .stream()
                                                .map(SalesStand::getMoviesId)
                                                .collect(Collectors.toList()),
                                        Collectors.counting())));
        genreAndMoviesID.forEach((k, v) -> System.out.println(k + " " + v));

        return genreAndMoviesID
                .entrySet()
                .stream()

                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new MyException(SERVICE, "GENRE AND ID COUNT EXCEPTION"));
    }

    /**
     * @return Genre and average price of tickets
     */
    Map<String, BigDecimal> genreAveragePrice() {

        List<Movie> moviesSaled = salesStandRepository.findAll()
                .stream()
                .map(x ->
                        new MovieRepository()
                                .findAll()
                                .stream()
                                .filter(y -> y.getId().equals(x.getMoviesId()))
                                .findFirst()
                                .orElseThrow(() -> new MyException(SERVICE, "MOVIE ID OUT OF BASE"))
                )
                .collect(Collectors.toList());
        Map<String, List<BigDecimal>> genreAndBigDecimal = moviesSaled
                .stream()
                .collect(
                        Collectors.groupingBy(
                                Movie::getGenre,
                                Collectors.mapping(
                                        Movie::getPrice,
                                        Collectors.toList())));

        return genreAndBigDecimal.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                v -> v.getValue()
                        .stream()
                        .reduce(BigDecimal::add)
                        .orElseThrow(() -> new MyException(SERVICE, "BIG DECIMAL ADD EXCEPTION"))
                        .divide(new BigDecimal(v.getValue().size()), RoundingMode.UP)));


    }

    /**
     * Statistic of loyalty card of all customers
     */
    void loyaltyCardStatistic() {

        int loyaltyCardsize = new LoyaltyCardRepository().findAll().size();
        int customersize = new CustomerRepository().findAll().size();

        if (loyaltyCardsize == 0 || customersize == 0) {
            throw new MyException(SERVICE, "NO CARDS OR CUSTOMER TO COMPARE");
        }
        System.out.println("ILOSC UZYTKOWNIKOW POSIADAJACYCH KARTE STALEGO KLIENTA TO " +
                loyaltyCardsize);
        System.out.println("ILOSC UZYTKOWNIKOW TO " +
                customersize);
        if (loyaltyCardsize < customersize) {
            System.out.println("ICH STOSUNEK TO: 1 DO " + loyaltyCardsize % customersize);
        } else {
            System.out.println("ICH STOSUNEK TO: 1 DO " + customersize % loyaltyCardsize);
        }
    }

    /**
     * Statistic of all movies
     */
    Map<String, Long> moviesStatistics() {
        System.out.println("PODAJ DATE OD(yyyy-MM-dd HH:mm)");
        LocalDateTime dateTimeUserAt = userDataService.getLocalDateTime();
        System.out.println("PODAJ DATE DO(yyyy-MM-dd HH:mm):");
        LocalDateTime dateTimeUserTo = userDataService.getLocalDateTime();

        Map<SalesStand, Movie> salesMovies = salesStandRepository.findAll()
                .stream()
                .filter(x ->
                        x.getStartDateTime()
                                .isAfter(dateTimeUserAt) &&
                                x.getStartDateTime()
                                        .isBefore(dateTimeUserTo))
                .collect(Collectors.toMap(
                        k -> k,
                        v -> new MovieRepository()
                                .findAll()
                                .stream()
                                .filter(y -> y.getId().equals(v.getMoviesId()))
                                .findFirst()
                                .orElseThrow(() -> new MyException(SERVICE, "MOVIE OUT OF BASE"))
                ));

        return salesMovies
                .entrySet()
                .stream()
                .collect(Collectors
                        .groupingBy(
                                k -> k.getValue().getTitle(),
                                Collectors.counting()));
    }

    /**
     * @return genre and average age of clients
     */
    Map<String, Double> genreAge() {
        Map<SalesStand, Customer> customersAndMovies = salesStandRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        k -> k,
                        v -> new CustomerRepository()
                                .findAll()
                                .stream()
                                .filter(y -> y.getId().equals(v.getCustomerId()))
                                .findFirst()
                                .orElseThrow(() -> new MyException(SERVICE, "CUSTOMER OUT OF BASE"))));

        return customersAndMovies
                .entrySet()
                .stream()
                .collect(Collectors.groupingBy(
                        k -> new MovieRepository()
                                .findAll()
                                .stream()
                                .filter(y -> y.getId().equals(k.getKey().getMoviesId()))
                                .findFirst()
                                .orElseThrow(() -> new MyException(SERVICE, "MOVIE OUT OF BASE"))
                                .getGenre()
                        , Collectors.mapping(
                                v -> v.getValue().getAge(),
                                Collectors.averagingInt(x -> x))));

    }

    private static boolean filtringDates(LocalDateTime... args) {

        return args[0].isAfter(args[1]) && args[0].isBefore(args[2]);
    }
}



