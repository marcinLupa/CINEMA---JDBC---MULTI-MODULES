package service;



import connection.DbConnection;
import exceptions.ExceptionCode;
import exceptions.MyException;
import model.Movie;
import model.SalesStand;
import model.utils.SortingOptions;
import repository.impl.CustomerRepository;
import repository.impl.MovieRepository;
import repository.impl.SalesStandRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Marcin Lupa
 * @version 1.0
 * @since 2019-01-28
 * This is key class of CINEMA app, where void method manage is used to control functionality of app
 * in this class you can check data for movies, customers, simulate buying ticket options,
 * initialise base by exemplary resources, view statistics and get out from program.
 */
public class MenuService {
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final MovieRepository movieRepository = new MovieRepository();
    private final SalesStandRepository salesStandRepository = new SalesStandRepository();
    private final MenuUtilsService menuUtilsService = new MenuUtilsService();
    private final UserDataService userDataService = new UserDataService();

    @SuppressWarnings({"SingleStatementInBlock", "finally", "ThrowFromFinallyBlock", "CatchMayIgnoreException"})
    public void manage() {
        try {
            DbConnection.getInstance().getConnection().setAutoCommit(false);
            while (true) {
                try {
                    System.out.println("JAKIE DANE CHCESZ EDYTOWAC LUB WYSWIETLIC?" + "\n" +
                            "1 - FILM" + "\n" +
                            "2 - UZYTKOWNIK" + "\n" +
                            "3 - SYMULATOR ZAKUPU BILETU ORAZ HISTORIA TRANSAKCJI" + "\n" +
                            "4 - INICJALIZACJA BAZY DANYCH" + "\n" +
                            "5 - STATYSTYKI" + "\n" +
                            "6 - WYJSCIE Z PROGRAMU");

                    int clientOrMovie = userDataService.getInt(6);

                    switch (clientOrMovie) {
                        case 1:
                            while (true) {
                                System.out.println("MENU:");
                                System.out.println("1 - USUN FILM Z BAZY DANYCH" + "\n"
                                        + "2 - EDYCJA FILMU" + "\n"
                                        + "3 - ZOBACZ WSZYSTKIE FILMY" + "\n"
                                        + "4 - ZNAJDZ FILM PO ID" + "\n"
                                        + "5 - SORTOWANIE FILMOW" + "\n"
                                        + "6 - FILTROWANIE FILMOW" + "\n"
                                        + "7 - PODZIAL FILMOW NA GATUNKI " + "\n"
                                        + "8 - DODAJ FILM DO BAZY DANYCH " + "\n"
                                        + "9 - WYJSCIE Z MENU FILMU " + "\n");
                                System.out.println("PODAJ OPCJE Z KTOREJ CHCESZ SKORZYSTAC");

                                int option = userDataService.getInt(9);

                                switch (option) {
                                    case 1:
                                        System.out.println("CZY NA PEWNO CHCESZ USUNAC FILM Z BAZY DANYCH(TAK/NIE)?");
                                        boolean acceptDelateFilm = userDataService.getYesOrNo();
                                        if (!acceptDelateFilm) {
                                            break;
                                        } else {
                                            System.out.println("KTORY ELEMENT TABELI MA ZOSTAC USUNIETY?");
                                            int deletePosition = userDataService.getInt(movieRepository.findAll().size());

                                            List<SalesStand> sales = salesStandRepository.findAll();
                                            List<Integer> ints = sales
                                                    .stream()
                                                    .filter(x -> x.getMoviesId().equals(deletePosition))
                                                    .map(SalesStand::getId)
                                                    .collect(Collectors.toList());
                                            ints.forEach(salesStandRepository::delete);

                                            movieRepository.delete(deletePosition);
                                            break;
                                        }
                                    case 2:
                                        System.out.println("CZY NA PEWNO CHCESZ EDYTOWAC FILM Z BAZY DANYCH(TAK/NIE)?");

                                        boolean acceptEditFilm = userDataService.getYesOrNo();
                                        if (!acceptEditFilm) {
                                            break;
                                        } else {
                                            System.out.println("PODAJ ID ELEMNTU KTORY CHCESZ EDYTOWAC?");
                                            movieRepository
                                                    .update(movieRepository
                                                            .findById(userDataService.getInt(movieRepository.findAll().size()))
                                                            .orElseThrow(() -> new IllegalArgumentException("ID OF MOVIE OUT OF BASE")));

                                            break;
                                        }

                                    case 3:
                                        movieRepository
                                                .findAll()
                                                .forEach(System.out::println);
                                        break;
                                    case 4:
                                        System.out.println("PODAJ ID FILMU KTOREGO CHCESZ WYSWIETLIC");
                                        System.out.println(movieRepository
                                                .findAll()
                                                .get(userDataService.getInt(movieRepository
                                                        .findAll().size())));
                                        break;
                                    case 5:
                                        System.out.println("OPCJE SORTOWANIA: " + "\n"
                                                + "1 - ALFABETYCZNIE PO TYTULE ROSNACO" + "\n"
                                                + "2 - ALFABETYCZNIE PO TYTULE MALEJACO" + "\n"
                                                + "3 - ALFABETYCZNIE PO GATUNKU ROSNACO" + "\n"
                                                + "4 - ALFABETYCZNIE PO GATUNKU MALEJACO" + "\n"
                                                + "5 - PO CENIE OD NAJMNIEJSZEJ ROSNACO" + "\n"
                                                + "6 - PO CENIE OD NAJMNIEJSZEJ MALEJACO" + "\n"
                                                + "7 - CZAS TRWANIA OD NAJKRÓTSZEGO ROSNACO" + "\n"
                                                + "8 - CZAS TRWANIA OD NAJKRÓTSZEGO MALEJACO" + "\n"
                                                + "9 - OD NAJSTARSZEGO FILMU DO NAJMŁODSZEGO ROSNACO" + "\n"
                                                + "10 - OD NAJSTARSZEGO FILMU DO NAJMŁODSZEGO MALEJACO " + "\n");
                                        int sortingOption = userDataService.getInt(10);

                                        switch (sortingOption) {
                                            case 1:
                                                movieRepository
                                                        .sortingMovies(SortingOptions.TITLE, SortingOptions.ASC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 2:
                                                movieRepository
                                                        .sortingMovies(SortingOptions.TITLE, SortingOptions.DESC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 3:
                                                movieRepository
                                                        .sortingMovies(SortingOptions.GENRE, SortingOptions.ASC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 4:
                                                movieRepository
                                                        .sortingMovies(SortingOptions.GENRE, SortingOptions.DESC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 5:
                                                movieRepository
                                                        .sortingMovies(SortingOptions.PRICE, SortingOptions.ASC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 6:
                                                movieRepository
                                                        .sortingMovies(SortingOptions.PRICE, SortingOptions.DESC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 7:
                                                movieRepository
                                                        .sortingMovies(SortingOptions.DURATION, SortingOptions.ASC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 8:
                                                movieRepository
                                                        .sortingMovies(SortingOptions.DURATION, SortingOptions.DESC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 9:
                                                movieRepository
                                                        .sortingMovies(SortingOptions.RELASE_DATE, SortingOptions.ASC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 10:
                                                movieRepository
                                                        .sortingMovies(SortingOptions.RELASE_DATE, SortingOptions.DESC)
                                                        .forEach(System.out::println);
                                                break;
                                        }
                                        break;
                                    case 6:
                                        System.out.println("FILTROWANIE BAZY FILMOW PO");
                                        System.out.println("1 - GATUNKU" + "\n" +
                                                "2 - DACIE PREMIERY" + "\n" +
                                                "3 - CENIE" + "\n" +
                                                "4 - CZASIE TRWANIA FILMU" + "\n" +
                                                "5 - SZUKANYM SLOWIE KLUCZOWYM");
                                        int filtringOption = userDataService.getInt(5);

                                        switch (filtringOption) {
                                            case 1:
                                                movieRepository
                                                        .filteredMovies(SortingOptions.GENRE)
                                                        .forEach(System.out::println);
                                                break;
                                            case 2:
                                                movieRepository
                                                        .filteredMovies(SortingOptions.RELASE_DATE)
                                                        .forEach(System.out::println);
                                                break;
                                            case 3:
                                                movieRepository
                                                        .filteredMovies(SortingOptions.PRICE)
                                                        .forEach(System.out::println);
                                                break;
                                            case 4:
                                                movieRepository
                                                        .filteredMovies(SortingOptions.DURATION)
                                                        .forEach(System.out::println);
                                                break;
                                            case 5:
                                                movieRepository
                                                        .filteredMovies(SortingOptions.FIND_WORD)
                                                        .forEach(System.out::println);
                                                break;

                                        }
                                        break;
                                    case 7:
                                        movieRepository.genreMap()
                                                .forEach((k, v) -> System.out.println(
                                                        k + "" + v.stream().
                                                                map(Movie::getTitle)
                                                                .collect(Collectors.toList())));
                                        break;
                                    case 8:
                                        System.out.println("CZY NA PEWNO CHCESZ DODAC FILM DO BAZY DANYCH(TAK/NIE)?");
                                        boolean acceptAddFilm = userDataService.getYesOrNo();
                                        if (!acceptAddFilm) {
                                            break;
                                        } else {
                                            System.out.println("WPROWADZANIE DANYCH NOWEGO FILMU" + "\n" + "PODAJ NAZWE FILMU");
                                            String filename = userDataService.getString();
                                            movieRepository.addFilmFromFile(filename);
                                            break;
                                        }
                                    case 9:
                                        return;
                                }
                            }

                        case 2:
                            while (true) {
                                System.out.println("MENU:");
                                System.out.println("1 - USUN UZYTKOWNIKA" + "\n"
                                        + "2 - EDYCJA DANYCH UZYTKOWNIKA" + "\n"
                                        + "3 - ZOBACZ WSZYSTKICH UZYTKOWNIKOW" + "\n"
                                        + "4 - PODAJ ID UZYTKOWNIKA KTOREGO DANE CHCESZ WYSWIETLIC" + "\n"
                                        + "5 - SORTOWANIE DANYCH UZYTKOWNIKOW" + "\n"
                                        + "6 - DODAJ UZYTKOWNIKA  " + "\n"
                                        + "7 - WYJSCIE Z MENU UZYTKOWNIKA " + "\n");
                                System.out.println("PODAJ OPCJE Z KTOREJ CHCESZ SKORZYSTAC");

                                int option = userDataService.getInt(7);

                                switch (option) {
                                    case 1:
                                        System.out.println("CZY NA PEWNO CHCESZ USUNAC DANE  UZYTKOWNIKA(TAK/NIE)?");
                                        boolean acceptDelate = userDataService.getYesOrNo();
                                        if (!acceptDelate) {
                                            break;
                                        } else {
                                            System.out.println(" PODAJ ID ELEMENTU KTORY MA ZOSTAC USUNIETY?");
                                            int deletePosition = userDataService.getInt(customerRepository
                                                    .findAll().size());

                                            List<SalesStand> sales = salesStandRepository.findAll();
                                            List<Integer> ints = sales
                                                    .stream()
                                                    .filter(x -> x.getCustomerId().equals(deletePosition))
                                                    .map(SalesStand::getId)
                                                    .collect(Collectors.toList());
                                            ints.forEach(salesStandRepository::delete);

                                            customerRepository.delete(deletePosition);

                                            break;
                                        }
                                    case 2:
                                        System.out.println("CZY NA PEWNO CHCESZ EDYTOWAC DANE UZYTKOWNIKA(TAK/NIE)?");
                                        boolean acceptEdit = userDataService.getYesOrNo();
                                        if (!acceptEdit) {
                                            break;
                                        } else {
                                            System.out.println("PODAJ ID ELEMNTU KTORY CHCESZ EDYTOWAC?");
                                            customerRepository
                                                    .update(customerRepository
                                                            .findById(userDataService.getInt(customerRepository.findAll().size()))
                                                            .orElseThrow(() -> new IllegalArgumentException("ID OF CUSTOMER OUT OF BASE")));
                                            break;
                                        }
                                    case 3:
                                        customerRepository
                                                .findAll()
                                                .forEach(System.out::println);
                                        break;
                                    case 4:
                                        System.out.println("PODAJ ID UZYTKOWNIKA KTOREGO CHCESZ WYSWIETLIC");
                                        System.out.println(customerRepository
                                                .findAll()
                                                .get(userDataService.getInt(customerRepository.findAll().size())));
                                        break;
                                    case 5:
                                        System.out.println("OPCJE SORTOWANIA: " + "\n"
                                                + "1 - ALFABETYCZNIE PO IMIENIE MALEJACO" + "\n"
                                                + "2 - ALFABETYCZNIE PO IMIENIE ROSNACO" + "\n"
                                                + "3 - ALFABETYCZNIE PO NAZWISKU MALEJACO" + "\n"
                                                + "4 - ALFABETYCZNIE PO NAZWISKU ROSNACO" + "\n"
                                                + "5 - PO WIEKU OD NAJMLODSZEGO MALEJACO" + "\n"
                                                + "6 - PO WIEKU OD NAJMLODSZEGO ROSNACO" + "\n"
                                                + "7 - ALFABETYCZNIE ADRES E-MAIL MALEJACO" + "\n"
                                                + "8 - ALFABETYCZNIE ADRES E-MAIL ROSNACO" + "\n"
                                                + "9 - PO ID KARTY STALEGO KLIENTA MALEJACO" + "\n"
                                                + "10 - PO ID KARTY STALEGO KLIENTA ROSNACO" + "\n");
                                        int sortingOption = userDataService.getInt(10);

                                        switch (sortingOption) {
                                            case 1:
                                                customerRepository
                                                        .sortingCustomers(SortingOptions.NAME, SortingOptions.DESC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 2:
                                                customerRepository
                                                        .sortingCustomers(SortingOptions.NAME, SortingOptions.ASC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 3:
                                                customerRepository
                                                        .sortingCustomers(SortingOptions.SURNAME, SortingOptions.DESC)
                                                        .forEach(System.out::println);

                                                break;
                                            case 4:
                                                customerRepository
                                                        .sortingCustomers(SortingOptions.SURNAME, SortingOptions.ASC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 5:
                                                customerRepository
                                                        .sortingCustomers(SortingOptions.AGE, SortingOptions.DESC)
                                                        .forEach(System.out::println);

                                                break;
                                            case 6:
                                                customerRepository
                                                        .sortingCustomers(SortingOptions.AGE, SortingOptions.ASC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 7:
                                                customerRepository
                                                        .sortingCustomers(SortingOptions.EMAIL, SortingOptions.DESC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 8:
                                                customerRepository
                                                        .sortingCustomers(SortingOptions.EMAIL, SortingOptions.ASC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 9:
                                                customerRepository
                                                        .sortingCustomers(SortingOptions.LOYALTY_CARD_ID, SortingOptions.DESC)
                                                        .forEach(System.out::println);
                                                break;
                                            case 10:
                                                customerRepository
                                                        .sortingCustomers(SortingOptions.LOYALTY_CARD_ID, SortingOptions.ASC)
                                                        .forEach(System.out::println);
                                                break;
                                        }
                                        break;
                                    case 6:
                                        System.out.println("CZY NA PEWNO CHCESZ WPROWADZIC DANE NOWEGO UZYTKOWNIKA(TAK/NIE)?");
                                        boolean accept = userDataService.getYesOrNo();
                                        if (!accept) {
                                            break;
                                        } else {
                                            System.out.println("PODAJ IMIĘ UZYTKOWNIKA");
                                            String name = userDataService.getString();
                                            System.out.println("PODAJ NAZWISKO UZYTKOWNIKA");
                                            String surname = userDataService.getString();
                                            System.out.println("PODAJ WIEK UZYTKOWNIKA");
                                            Integer age = userDataService.getInt(150);
                                            System.out.println("PODAJ ADRES E-MAIL UZYTKOWNIKA");
                                            String email = userDataService.getString();
                                            customerRepository.addCustomerManual(name, surname, age, email);
                                            break;
                                        }
                                    case 7:
                                        return;
                                }
                            }
                        case 3:
                            menuUtilsService.managingTickets();
                            DbConnection.getInstance().getConnection().commit();
                            continue;
                        case 4:
                            new DateBaseInitializationService().dbInitialization();
                            DbConnection.getInstance().getConnection().commit();
                            continue;
                        case 5:
                            System.out.println("STATYSTYKI: " + "\n" +
                                    "1 - GATUNEK FILMU KTORY JEST NAJCZESCIEJ WYBIERANY " + "\n" +
                                    "2 - ZESTAWIENIE GATUNEK FILMU - SREDNIA CENA BILETOW ZA TEN GATUNEK " + "\n" +
                                    "3 - ILOSC KLIENTOW KTORZY MAJA KARTE " +
                                    "LOJANOLSCIOWA I STOSUNEK TEJ ILOSCI DO WSZYSTKICH KLIENTOW " + "\n" +
                                    "4 - ZESTAWIENIE TYTULOW FILMOW I ILOSC KUPIONYCH NA NIE BILETOW W ZAKRESIE CZASU OD DO " + "\n" +
                                    "5 - ZESTAWIENIE GATUNEK FILMU - SREDNI WIEK OSOB ZAINTERESOWANYCH TYM GATUNKIEM ");
                            int statisticOption = userDataService.getInt(5);

                            switch (statisticOption) {
                                case 1:
                                    System.out.println("NAJPOPULARNIEJSZY GATUNEK FILMOWY TO: "
                                            + menuUtilsService.mostPopularGenre());
                                    break;
                                case 2:
                                    menuUtilsService
                                            .genreAveragePrice()
                                            .forEach((k, v) -> System.out.println(
                                                    k + " ,SREDNIA CENA: " + String.format("%2.2f", v) + " ZŁ"));
                                    break;
                                case 3:
                                    menuUtilsService.loyaltyCardStatistic();
                                    break;
                                case 4:
                                    menuUtilsService
                                            .moviesStatistics()
                                            .forEach(
                                                    (k, v) -> System.out.println(
                                                            "FILM: " + k + ", OBEJRZANO: " + v + " RAZY"));
                                    break;
                                case 5:
                                    menuUtilsService
                                            .genreAge()
                                            .forEach((k, v) -> System.out.println(
                                                    k + ", SREDNIA WIEKU: " + String.format("%2.2f", v) + " LAT"));
                                    break;
                            }
                            break;
                        case 6:
                            return;
                    }
                    DbConnection.getInstance().getConnection().commit();
                } catch (MyException e) {
                    System.err.println(e.getExceptionInfo().getMessage());
                }
            }
        } catch (Exception e) {
            try {
                DbConnection.getInstance().getConnection().rollback();
            } catch (Exception ee) {
            } finally {
                throw new MyException(ExceptionCode.SERVICE, "MENU SERVICE EXCEPTION");
            }
        }
    }

}
